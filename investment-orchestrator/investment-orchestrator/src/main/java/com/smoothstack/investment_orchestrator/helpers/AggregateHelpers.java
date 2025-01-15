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

package com.smoothstack.investment_orchestrator.helpers;

import com.smoothstack.investment_orchestrator.dto.AggregatedOHLCDTO;
import com.smoothstack.investment_orchestrator.dto.PosAggDTO;
import com.smoothstack.investment_orchestrator.dto.TimeSeriesData;
import com.smoothstack.investment_orchestrator.model.Position;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class AggregateHelpers {


    public List<PosAggDTO> transformPositions(List<Position> positions) {
        return positions.stream()
                .map(this::transform)
                .toList();
    }

    private PosAggDTO transform(Position position) {
        return new PosAggDTO(position.getTicker(), position.getSecurityName(), position.getTotalQuantity());
    }

    public AggregatedOHLCDTO aggregateOHLC(List<TimeSeriesData> data, int days) {
        if (data.size() < days) {
            throw new IllegalArgumentException("Not enough data for " + days + " day aggregation");
        }

        List<TimeSeriesData> relevantData = data.subList(0, days);

        LocalDate startDate = relevantData.get(relevantData.size() - 1).getDate();
        LocalDate endDate = relevantData.get(0).getDate();
        BigDecimal open = relevantData.get(relevantData.size() - 1).getOpen();
        BigDecimal close = relevantData.get(0).getClose();
        BigDecimal high = relevantData.stream().map(TimeSeriesData::getHigh).max(BigDecimal::compareTo).orElseThrow();
        BigDecimal low = relevantData.stream().map(TimeSeriesData::getLow).min(BigDecimal::compareTo).orElseThrow();
        Long totalVolume = relevantData.stream().mapToLong(TimeSeriesData::getVolume).sum();

        return new AggregatedOHLCDTO(startDate, endDate, open, high, low, close, totalVolume);
    }

    public String generateSentiment(String symbol) {
        // Todo: implement sentiment logic, currently just returns positive or negative based on a random number
        Random rand = new Random();
        int randomInt = rand.nextInt(2);
        return randomInt == 0 ? "negative" : "positive";
    }
}