package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.LegalReviewDecision;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.LegalReviewDecisionHistory;
import de.kirillmrotzek.legalflow.repository.LegalReviewDecisionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LegalReviewDecisionHistoryService {

    private final LegalReviewDecisionHistoryRepository repository;

    public void record(
            Contract contract,
            LegalReviewDecision decision
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        LegalReviewDecisionHistory history =
                new LegalReviewDecisionHistory();

        history.setContract(contract);
        history.setReviewType(decision.getReviewType());
        history.setReason(decision.getReason());
        history.setDecidedAt(LocalDateTime.now());
        history.setDecidedBy(authentication.getName());

        repository.save(history);
    }

    public void deleteByContractId(Long contractId) {
        repository.deleteByContractId(contractId);
    }
}
