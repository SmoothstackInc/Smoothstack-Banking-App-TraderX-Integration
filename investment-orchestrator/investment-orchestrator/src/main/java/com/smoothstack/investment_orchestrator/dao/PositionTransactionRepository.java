package com.smoothstack.investment_orchestrator.dao;

import com.smoothstack.investment_orchestrator.model.PositionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionTransactionRepository extends JpaRepository<PositionTransaction, Integer> {

    Optional<PositionTransaction> findByPositionTransactionId(Integer positionTransactionId);

}
