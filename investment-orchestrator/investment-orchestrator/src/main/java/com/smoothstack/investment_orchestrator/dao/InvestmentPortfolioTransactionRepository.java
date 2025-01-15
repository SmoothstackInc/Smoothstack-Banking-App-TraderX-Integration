package com.smoothstack.investment_orchestrator.dao;

import com.smoothstack.investment_orchestrator.model.InvestmentPortfolioTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentPortfolioTransactionRepository extends JpaRepository<InvestmentPortfolioTransaction, Integer> {

    Optional<InvestmentPortfolioTransaction> findByInvestmentPortfolioTransactionId(Integer investmentPortfolioTransactionId);

}
