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

import com.smoothstack.investment_orchestrator.dao.InvestmentPortfolioRepository;
import com.smoothstack.investment_orchestrator.dao.PositionRepository;
import com.smoothstack.investment_orchestrator.dao.PositionTransactionRepository;
import com.smoothstack.investment_orchestrator.dto.TradeRequestDTO;
import com.smoothstack.investment_orchestrator.exception.custom.InsufficientFundsException;
import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import com.smoothstack.investment_orchestrator.model.Position;
import com.smoothstack.investment_orchestrator.model.PositionTransaction;
import org.springframework.stereotype.Service;

@Service
public class TradeHelpers {
    private final PositionRepository pr;
    private final InvestmentPortfolioRepository ipr;
    private final PositionTransactionRepository ptr;


    public TradeHelpers(PositionRepository positionRepository, InvestmentPortfolioRepository ipr,
                        PositionTransactionRepository ptr) {
        this.pr = positionRepository;
        this.ipr = ipr;
        this.ptr = ptr;
    }


    public void handleTransaction(InvestmentPortfolio portfolio, TradeRequestDTO tradeRequest, String side) {
        boolean isBuy = "Buy".equalsIgnoreCase(side);
        double transactionAmount = tradeRequest.getPrice() * tradeRequest.getQuantity();
        if (isBuy) {
            checkSufficientFunds(portfolio, transactionAmount);
        }
        Position position = getOrCreatePosition(tradeRequest);

        if (!isBuy) {
            checkSufficientQuantity(position, tradeRequest.getQuantity());
        }
        position = updatePosition(position, tradeRequest, transactionAmount, isBuy);
        saveTransaction(position, tradeRequest, side);
        updatePortfolio(portfolio, transactionAmount, isBuy);
    }

    private void checkSufficientFunds(InvestmentPortfolio portfolio, double transactionAmount) {
        if (transactionAmount > portfolio.getAmtAvailable()) {
            throw new InsufficientFundsException("Insufficient funds in portfolio");
        }
    }

    private Position getOrCreatePosition(TradeRequestDTO tradeRequest) {
        Position position = pr.findByInvestmentPortfolioIdAndTicker(
                tradeRequest.getInvestmentPortfolioId(),
                tradeRequest.getTicker()
        );

        if (position == null) {
            position = new Position(tradeRequest.getInvestmentPortfolioId(),
                    tradeRequest.getTicker(), tradeRequest.getSecurityName(), 0d, 0);
        }

        return position;
    }

    private void checkSufficientQuantity(Position position, int quantity) {
        if (position.getTotalQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient quantity to sell");
        }
    }

    private Position updatePosition(Position position, TradeRequestDTO tradeRequest, double transactionAmount,
                                    boolean isBuy) {
        int quantityChange = isBuy ? tradeRequest.getQuantity() : -tradeRequest.getQuantity();
        double valueChange = isBuy ? transactionAmount : -transactionAmount;

        position.setTotalQuantity(position.getTotalQuantity() + quantityChange);
        position.setTotalValue(position.getTotalValue() + valueChange);
        return pr.save(position);
    }

    private void saveTransaction(Position position, TradeRequestDTO tradeRequest, String side) {
        if (position.getPositionId() == null) {
            pr.save(position);
        }
        PositionTransaction transaction = new PositionTransaction();
        transaction.setPositionId(position.getPositionId());
        transaction.setLockedPrice(tradeRequest.getPrice());
        transaction.setQuantity(tradeRequest.getQuantity());
        transaction.setSide(side);
        transaction.setPosition(position);
        ptr.save(transaction);
    }

    private void updatePortfolio(InvestmentPortfolio portfolio, double transactionAmount, boolean isBuy) {
        portfolio.setTotalInvested(portfolio.getTotalInvested() + (isBuy ? transactionAmount : -transactionAmount));
        portfolio.setAmtAvailable(portfolio.getAmtAvailable() + (isBuy ? -transactionAmount : transactionAmount));
        ipr.save(portfolio);
    }
}