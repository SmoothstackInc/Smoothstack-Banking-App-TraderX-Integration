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

package com.smoothstack.investment_orchestrator.serviceImpl;

import com.smoothstack.investment_orchestrator.dao.InvestmentPortfolioRepository;
import com.smoothstack.investment_orchestrator.dao.PositionRepository;
import com.smoothstack.investment_orchestrator.dto.*;
import com.smoothstack.investment_orchestrator.helpers.AggregateHelpers;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LiveDataService {
    private final RestTemplate restTemplate;
    private static final String LIVE_DATA_BASE_URL = "http://localhost:9000/api/v1/stocks";

    private final AggregateHelpers aggH;

    private final PositionRepository positionRepository;
    private final InvestmentPortfolioRepository iPR;

    @Autowired
    public LiveDataService(RestTemplate restTemplate, PositionRepository positionRepository, AggregateHelpers aggH,
                           InvestmentPortfolioRepository iPR) {
        this.aggH = aggH;
        this.restTemplate = restTemplate;
        this.positionRepository = positionRepository;
        this.iPR = iPR;
    }

    public TimeSeriesDTO getStockHistory(String symbol) {
        String url = LIVE_DATA_BASE_URL + "/history/" + symbol;
        try {
            return restTemplate.getForObject(url, TimeSeriesDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch stock history for symbol: " + symbol, e);
        }
    }

    public MetaDataDTO getMetaData(String symbol) {
        String url = LIVE_DATA_BASE_URL + "/meta-data/" + symbol;
        try {
            return restTemplate.getForObject(url, MetaDataDTO.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch stock meta data for symbol: " + symbol, e);
        }
    }

    public AdviceAggregateModelDTO getAdvice(Integer investmentPortfolioId, String symbol) {
        PortfolioAggregateDTO portfolioAggregateDTO = getPortfolioAggregate(investmentPortfolioId);
        Optional<InvestmentPortfolio> investmentPortfolioOpt = iPR.findById(investmentPortfolioId);
        if (investmentPortfolioOpt.isEmpty()) {
            throw new RuntimeException("Could not find investment portfolio with id: " + investmentPortfolioId);
        }

        Integer investorId = investmentPortfolioOpt.get().getInvestorId();
        AggregatedStockDataDTO aggregatedStockDataDTO = getAggregatedStockData(symbol,
                investorId);
        return new AdviceAggregateModelDTO(aggregatedStockDataDTO, portfolioAggregateDTO);
    }

    public AggregatedStockDataDTO getAggregatedStockData(String symbol, Integer investorId) {
        TimeSeriesDTO historicalData = getStockHistory(symbol);


        List<TimeSeriesData> sortedData = historicalData.data().stream()
                .sorted(Comparator.comparing(TimeSeriesData::getDate).reversed())
                .toList();

        AggregatedOHLCDTO fiveDayAggregate = aggH.aggregateOHLC(sortedData, 5);
        AggregatedOHLCDTO tenDayAggregate = aggH.aggregateOHLC(sortedData, 10);
        AggregatedOHLCDTO fifteenDayAggregate = aggH.aggregateOHLC(sortedData, 15);
        AggregatedOHLCDTO thirtyDayAggregate = aggH.aggregateOHLC(sortedData, 30);

        return new AggregatedStockDataDTO(
                symbol,
                aggH.generateSentiment(symbol),
                fiveDayAggregate,
                tenDayAggregate,
                fifteenDayAggregate,
                thirtyDayAggregate
        );
    }

    private PortfolioAggregateDTO getPortfolioAggregate(Integer investmentPortfolioId) {
        Optional<InvestmentPortfolio> ipo = iPR.findById(investmentPortfolioId);
        if (ipo.isPresent()) {
            InvestmentPortfolio ip = ipo.get();
            return new PortfolioAggregateDTO(ip.getTotalInvested(), ip.getAmtAvailable(),
                    this.getPosAgg(investmentPortfolioId));
        } else {
            throw new RuntimeException("Investment Portfolio with id: " + investmentPortfolioId + " not found");
        }
    }


    private List<PosAggDTO> getPosAgg(Integer investmentPortfolioId) {
        return positionRepository.findByInvestmentPortfolioId(investmentPortfolioId)
                .map(aggH::transformPositions)
                .orElse(Collections.emptyList());
    }
}