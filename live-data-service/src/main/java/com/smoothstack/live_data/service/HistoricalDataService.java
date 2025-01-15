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

import com.smoothstack.live_data.dao.HistoricalPriceRepository;
import com.smoothstack.live_data.dao.StockRepository;
import com.smoothstack.live_data.dto.TimeSeriesDTO;
import com.smoothstack.live_data.dto.TimeSeriesData;
import com.smoothstack.live_data.event.CsvProcessingCompletedEvent;
import com.smoothstack.live_data.model.HistoricalPrice;
import com.smoothstack.live_data.model.Stock;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class HistoricalDataService implements ApplicationListener<CsvProcessingCompletedEvent> {
    @Autowired
    private HistoricalPriceRepository historicalPriceRepository;
    @Autowired
    private StockRepository stockRepository;

    private static final int BATCH_SIZE = 1000;
    private static final int DAYS = 365;


    @Override
    public void onApplicationEvent(CsvProcessingCompletedEvent event) {
        log.info("Received CsvProcessingCompletedEvent. Initializing historical data...");
        initializeHistoricalData();
    }

    @PostConstruct
    public void postConstruct() {
        log.info("HistoricalDataService bean created");
    }

    private void initializeHistoricalData() {
        if (historicalPriceRepository.count() == 0) {
            log.info("Generating historical data...");
            generateHistoricalData();
        } else {
            log.info("Historical data already exists. Skipping generation.");
        }
    }


    public void generateHistoricalData() {
        long startTime = System.nanoTime();
        List<Stock> stocks = stockRepository.findAll();

        stocks.parallelStream().forEach(stock -> {
            BigDecimal lastClosePrice = null;
            List<HistoricalPrice> stockPrices = new ArrayList<>(DAYS);
            LocalDate date = LocalDate.now();
            double basePrice = stock.getPrice().doubleValue();

            for (int i = 0; i < DAYS; i++) {
                double variation = basePrice * ThreadLocalRandom.current().nextDouble() * 0.1;
                double open = basePrice - variation;
                double close = basePrice + variation;
                double high = Math.max(open, close) + variation;
                double low = Math.min(open, close) - variation;

                HistoricalPrice price = new HistoricalPrice();
                price.setSymbol(stock.getSymbol());
                price.setDate(date.minusDays(i));
                price.setOpen(BigDecimal.valueOf(open).setScale(2, RoundingMode.HALF_UP));
                price.setClose(BigDecimal.valueOf(close).setScale(2, RoundingMode.HALF_UP));
                price.setHigh(BigDecimal.valueOf(high).setScale(2, RoundingMode.HALF_UP));
                price.setLow(BigDecimal.valueOf(low).setScale(2, RoundingMode.HALF_UP));
                price.setVolume(ThreadLocalRandom.current().nextLong(1000, 10000));
                stockPrices.add(price);
                lastClosePrice = price.getClose();
                if (stockPrices.size() == BATCH_SIZE) {
                    historicalPriceRepository.saveAll(stockPrices);
                    stockPrices.clear();
                }
            }
            if (!stockPrices.isEmpty()) {
                historicalPriceRepository.saveAll(stockPrices);
                stockPrices.clear();
            }

            stock.setPrice(lastClosePrice);
        });


        stockRepository.saveAll(stocks);

        long endTime = System.nanoTime();
        long durationInNanos = endTime - startTime;
        double durationInSeconds = durationInNanos / 1_000_000_000.0;
        log.info("\u001B[31mHistorical data generated in: {} seconds.\u001B[0m", String.format("%.2f",
                durationInSeconds));
    }

    public TimeSeriesDTO getTimeSeriesForSymbol(String symbol) {
        List<HistoricalPrice> prices = historicalPriceRepository.findBySymbolOrderByDateAsc(symbol);
        List<TimeSeriesData> history = prices.stream().map(p -> new TimeSeriesData(p.getDate(), p.getOpen(),
                p.getClose(), p.getHigh(), p.getLow(), p.getVolume())).toList();
        return new TimeSeriesDTO(symbol, history);
    }
}