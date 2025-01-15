/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.smoothstack.live_data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smoothstack.live_data.dao.StockRepository;
import com.smoothstack.live_data.dto.StockPriceEvent;
import com.smoothstack.live_data.model.Stock;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RealTimeDataService {
    @Autowired
    private StockRepository stockRepository;
    private final Map<WebSocketSession, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, BigDecimal> stockUpdates = new ConcurrentHashMap<>();

    private double DRIFT_PERCENTAGE = 0.02;
    private double VOLATILITY_PERCENTAGE = 0.05;
    private int TIME_FRAME_IN_SECONDS = 3600;
    private final int UPDATE_FREQUENCY = 5;


    @PostConstruct
    public void init() {

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::updateStockPrices, 0, UPDATE_FREQUENCY, TimeUnit.SECONDS);
    }

    public void addSubscriptions(WebSocketSession session, List<String> symbols) {
        sessionSubscriptions.computeIfAbsent(session, k -> ConcurrentHashMap.newKeySet()).addAll(symbols);
    }

    public void removeSubscriptions(WebSocketSession session, List<String> symbols) {
        Set<String> subscriptions = sessionSubscriptions.get(session);
        if (subscriptions != null) {
            symbols.forEach(subscriptions::remove);
            if (subscriptions.isEmpty()) {
                sessionSubscriptions.remove(session);
            }
        }
    }

    public void removeAllSubscriptions(WebSocketSession session) {
        sessionSubscriptions.remove(session);
    }


    private void updateStockPrices() {
        List<Stock> stocks = stockRepository.findAll();
        Random random = new Random();

        double driftPerUpdate = (DRIFT_PERCENTAGE / TIME_FRAME_IN_SECONDS) * UPDATE_FREQUENCY;
        double volatilityPerUpdate =
                VOLATILITY_PERCENTAGE * Math.sqrt((double) UPDATE_FREQUENCY / TIME_FRAME_IN_SECONDS);

        for (Stock stock : stocks) {
            double randomWalk = random.nextGaussian() * volatilityPerUpdate;
            double totalChange = driftPerUpdate + randomWalk;

            BigDecimal currentPrice = stock.getPrice();
            BigDecimal priceChange = currentPrice.multiply(BigDecimal.valueOf(totalChange));
            BigDecimal newPrice = currentPrice.add(priceChange).setScale(2, RoundingMode.HALF_UP);

            BigDecimal minPrice = new BigDecimal("0.01");
            if (newPrice.compareTo(minPrice) < 0) {
                newPrice = minPrice;
            }

            stock.setPrice(newPrice);
            stockRepository.save(stock);
            stockUpdates.put(stock.getSymbol(), stock.getPrice());
        }
        notifySubscribers();
    }

    private void notifySubscribers() {
        StockPriceEvent event = new StockPriceEvent(new HashMap<>(stockUpdates));
        for (Map.Entry<WebSocketSession, Set<String>> entry : sessionSubscriptions.entrySet()) {
            Map<String, BigDecimal> relevantUpdates = event.updates().entrySet().stream()
                    .filter(update -> entry.getValue().contains(update.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!relevantUpdates.isEmpty()) {
                sendStockUpdate(entry.getKey(), new StockPriceEvent(relevantUpdates));
            }
        }
        stockUpdates.clear();
    }

    private void sendStockUpdate(WebSocketSession session, StockPriceEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            session.sendMessage(new TextMessage(eventJson));
        } catch (IOException e) {
            System.err.println("Error sending stock update: " + e.getMessage());
            try {
                session.close();
            } catch (IOException ex) {
                System.err.println("Error closing WebSocket session: " + ex.getMessage());
            }
        }
    }
}