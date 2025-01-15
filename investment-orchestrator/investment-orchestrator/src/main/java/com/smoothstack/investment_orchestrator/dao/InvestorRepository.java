package com.smoothstack.investment_orchestrator.dao;

import com.smoothstack.investment_orchestrator.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Integer> {

    Optional<Investor> findByInvestorId(Integer investorId);

}
