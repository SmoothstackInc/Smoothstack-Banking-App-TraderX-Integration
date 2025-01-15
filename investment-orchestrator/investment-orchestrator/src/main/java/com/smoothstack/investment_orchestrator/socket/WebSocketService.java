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

package com.smoothstack.investment_orchestrator.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketService extends TextWebSocketHandler {
    private WebSocketSession session;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Integer, Map<String, Double>> portfolioStockPrices = new ConcurrentHashMap<>();
    private UpdateListener updateListener;

    @PostConstruct
    public void connect() {
        WebSocketClient client = new StandardWebSocketClient();
        try {
            client.execute(this, "ws://localhost:9000/ws/stocks")
                    .toCompletableFuture()
                    .thenAccept(session -> {
                        this.session = session;
                        log.info("WebSocket connection established");
                    })
                    .exceptionally(ex -> {
                        log.error("Error connecting to WebSocket", ex);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Error connecting to WebSocket", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket connection closed: " + status);
    }


    public void subscribeToStocks(Integer portfolioId, List<String> symbols) {
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> subscriptionMessage = Map.of(
                        "action", "subscribe",
                        "symbols", symbols
                );
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(subscriptionMessage)));
                portfolioStockPrices.put(portfolioId, new ConcurrentHashMap<>());
                symbols.forEach(symbol -> portfolioStockPrices.get(portfolioId).put(symbol, 0.0));
                log.info("Subscribed portfolio {} to symbols: {}", portfolioId, symbols);
            } catch (IOException e) {
                log.error("Error subscribing to stocks for portfolio {}", portfolioId, e);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            Map<String, Double> updates = objectMapper.convertValue(jsonNode.get("updates"), new TypeReference<>() {
            });
            Set<Integer> updatedPortfolios = new HashSet<>();


            portfolioStockPrices.forEach((portfolioId, stockPrices) -> {
                boolean portfolioUpdated = false;
                for (Map.Entry<String, Double> entry : updates.entrySet()) {
                    String symbol = entry.getKey();
                    Double price = entry.getValue();
                    if (stockPrices.containsKey(symbol)) {
                        stockPrices.put(symbol, price);
                        portfolioUpdated = true;
                    }
                }
                if (portfolioUpdated) {
                    updatedPortfolios.add(portfolioId);
                }
            });
            if (updateListener != null) {
                for (Integer portfolioId : updatedPortfolios) {
                    updateListener.onUpdate(portfolioId, portfolioStockPrices.get(portfolioId));
                }
            }
        } catch (IOException e) {
            log.error("Error handling stock update", e);
        }
    }

    public void registerUpdateListener(UpdateListener listener) {
        this.updateListener = listener;
    }

    public interface UpdateListener {
        void onUpdate(Integer portfolioId, Map<String, Double> prices);
    }

    public Map<String, Double> getLatestPricesForPortfolio(Integer portfolioId) {
        return portfolioStockPrices.getOrDefault(portfolioId, new ConcurrentHashMap<>());
    }
}