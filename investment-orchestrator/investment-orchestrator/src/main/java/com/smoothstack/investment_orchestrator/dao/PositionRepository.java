package com.smoothstack.investment_orchestrator.dao;

import com.smoothstack.investment_orchestrator.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    Optional<Position> findByPositionId(Integer positionId);

    Position findByInvestmentPortfolioIdAndTicker(Integer portfolioId, String ticker);

    Optional<List<Position>> findByInvestmentPortfolioId(Integer investmentPortfolioId);
}