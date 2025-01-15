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

package com.smoothstack.live_data.helpers;

import com.smoothstack.live_data.dao.StockRepository;
import com.smoothstack.live_data.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Helpers {
    @Autowired
    private StockRepository stockRepository;
    private final Map<String, BigDecimal> initialPrices = new ConcurrentHashMap<>();
    private int TIME_FRAME_IN_SECONDS = 3600;


//    @PostConstruct
//    public void init() {
//        stockRepository.findAll().forEach(stock -> initialPrices.put(stock.getSymbol(), stock.getPrice()));
//        Executors.newSingleThreadScheduledExecutor()
//                .scheduleAtFixedRate(this::calculatePercentageChangeAndSaveToCsv, TIME_FRAME_IN_SECONDS,
//                        TIME_FRAME_IN_SECONDS, TimeUnit.SECONDS);
//    }

    private void calculatePercentageChangeAndSaveToCsv() {
        List<Stock> stocks = stockRepository.findAll();
        Map<String, String> percentageChanges = new HashMap<>();
        double totalPercentageChange = 0.0;

        for (Stock stock : stocks) {
            BigDecimal initialPrice = initialPrices.get(stock.getSymbol());
            BigDecimal currentPrice = stock.getPrice();

            double percentageChange =
                    ((currentPrice.doubleValue() - initialPrice.doubleValue()) / initialPrice.doubleValue()) * 100;
            totalPercentageChange += percentageChange;

            String formattedPercentageChange = String.format("%.2f%%", percentageChange);
            percentageChanges.put(stock.getSymbol(), formattedPercentageChange);
        }

        double averagePercentageChange = totalPercentageChange / stocks.size();
        String formattedAverageChange = String.format("%.2f%%", averagePercentageChange);
        savePercentageChangesToCsv(percentageChanges, formattedAverageChange);
    }

    private void savePercentageChangesToCsv(Map<String, String> percentageChanges, String averagePercentageChange) {
        String filePath = "src/main/resources/percentage_changes.csv";

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append("Stock Symbol,Percentage Change\n");

            for (Map.Entry<String, String> entry : percentageChanges.entrySet()) {
                writer.append(entry.getKey())
                        .append(",")
                        .append(entry.getValue())
                        .append("\n");
            }

            writer.append("Average,").append(averagePercentageChange).append("\n");

            System.out.println("CSV file saved at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}