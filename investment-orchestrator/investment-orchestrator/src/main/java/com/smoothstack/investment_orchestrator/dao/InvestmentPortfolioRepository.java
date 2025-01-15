package com.smoothstack.investment_orchestrator.dao;

import com.smoothstack.investment_orchestrator.model.InvestmentPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Integer> {

    Optional<InvestmentPortfolio> findByInvestmentPortfolioId(Integer investmentPortfolioId);

    @Query("SELECT p FROM InvestmentPortfolio p LEFT JOIN FETCH p.positions WHERE p.investmentPortfolioId = :id")
    Optional<InvestmentPortfolio> findByIdWithPositions(@Param("id") Integer id);
}