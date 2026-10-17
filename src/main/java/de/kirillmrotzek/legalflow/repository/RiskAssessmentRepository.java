package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByContractIdOrderByAssessedAtAsc(Long contractId);

    void deleteByContractId(Long contractId);
}
