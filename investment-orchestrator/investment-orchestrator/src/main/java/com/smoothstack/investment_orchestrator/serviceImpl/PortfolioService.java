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
import com.smoothstack.investment_orchestrator.dto.PortfolioDataDTO;
import com.smoothstack.investment_orchestrator.dto.SimplePositionDTO;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import com.smoothstack.investment_orchestrator.model.Position;
import com.smoothstack.investment_orchestrator.socket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class PortfolioService {
    private final InvestmentPortfolioRepository investmentPortfolioRepository;
    private final WebSocketService webSocketService;

    @Autowired
    public PortfolioService(InvestmentPortfolioRepository investmentPortfolioRepository,
                            WebSocketService webSocketService) {
        this.investmentPortfolioRepository = investmentPortfolioRepository;
        this.webSocketService = webSocketService;
    }

    public void subscribeToPortfolioStocks(Integer portfolioId) {
        InvestmentPortfolio portfolio = investmentPortfolioRepository.findByIdWithPositions(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with ID: " + portfolioId));

        List<String> stockSymbols = portfolio.getPositions().stream()
                .map(Position::getTicker)
                .distinct()
                .toList();


        webSocketService.subscribeToStocks(portfolioId, stockSymbols);
    }

    public PortfolioDataDTO getPortfolioData(Integer portfolioId) {
        List<SimplePositionDTO> simplePositions = getSimplePositions(portfolioId);
        return new PortfolioDataDTO(simplePositions);
    }

    private List<SimplePositionDTO> getSimplePositions(Integer portfolioId) {
        Optional<InvestmentPortfolio> portfolioOpt = investmentPortfolioRepository.findByIdWithPositions(portfolioId);
        if (portfolioOpt.isEmpty()) {
            throw new RuntimeException("Portfolio not found with ID: " + portfolioId);
        }

        InvestmentPortfolio portfolio = portfolioOpt.get();

        return portfolio.getPositions().stream()
                .map(position -> new SimplePositionDTO(position.getTicker(), position.getTotalQuantity()))
                .toList();
    }
}