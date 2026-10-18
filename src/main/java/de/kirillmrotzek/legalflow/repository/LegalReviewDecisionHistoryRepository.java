package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.LegalReviewDecisionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LegalReviewDecisionHistoryRepository
        extends JpaRepository<LegalReviewDecisionHistory, Long> {

    List<LegalReviewDecisionHistory>
    findByContractIdOrderByDecidedAtAsc(Long contractId);

    void deleteByContractId(Long contractId);
}
