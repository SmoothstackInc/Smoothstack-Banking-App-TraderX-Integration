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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.smoothstack.live_data.dao.StockRepository;
import com.smoothstack.live_data.event.CsvProcessingCompletedEvent;
import com.smoothstack.live_data.model.Stock;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class CsvService implements ApplicationEventPublisherAware {
    @Autowired
    private StockRepository stockRepository;
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing CSV service...");
        readCSV("src/main/resources/stocks_with_prices.csv");
        System.out.println("CSV processing completed. Publishing event...");
        eventPublisher.publishEvent(new CsvProcessingCompletedEvent(this));
    }

    private void readCSV(String csvFile) {

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] line;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                Stock stock = new Stock(
                        line[0],
                        line[1],
                        line[2],
                        line[3],
                        line[4],
                        line[5],
                        line[6],
                        line[7],
                        line[8],
                        BigDecimal.valueOf(Double.parseDouble(line[9]))
                );
                stockRepository.save(stock);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}