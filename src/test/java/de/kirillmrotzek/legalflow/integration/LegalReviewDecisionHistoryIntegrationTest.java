package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.LegalReviewDecisionHistory;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.repository.LegalReviewDecisionHistoryRepository;
import de.kirillmrotzek.legalflow.decision.LegalReviewDecision;
import de.kirillmrotzek.legalflow.service.LegalReviewDecisionHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LegalReviewDecisionHistoryIntegrationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private LegalReviewDecisionHistoryRepository historyRepository;

    @Autowired
    private LegalReviewDecisionHistoryService historyService;

    @Test
    @WithMockUser(username = "kirill")
    void shouldStoreDecisionHistoryInChronologicalOrder() {

        Contract contract = new Contract();

        contract.setContractNumber("DECISION-HISTORY-001");
        contract.setTitle("Decision History Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setContractType(ContractType.SERVICE);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setLegalReviewType(ReviewType.STANDARD);
        contract.setLegalReviewReason(
                "Standard legal review required"
        );
        contract.setContractValue(new BigDecimal("10000"));
        contract.setStartDate(LocalDate.of(2026, 1, 1));
        contract.setEndDate(LocalDate.of(2027, 1, 1));
        contract.setAutoRenewal(false);

        Contract savedContract =
                contractRepository.save(contract);

        historyService.record(
                savedContract,
                new LegalReviewDecision(
                        ReviewType.STANDARD,
                        "Standard review required."
                )
        );

        historyService.record(
                savedContract,
                new LegalReviewDecision(
                        ReviewType.ENHANCED,
                        "Unlimited liability detected."
                )
        );

        historyService.record(
                savedContract,
                new LegalReviewDecision(
                        ReviewType.STANDARD,
                        "Enhanced review factors no longer detected."
                )
        );

        List<LegalReviewDecisionHistory> history =
                historyRepository
                        .findByContractIdOrderByDecidedAtAsc(
                                savedContract.getId()
                        );

        assertEquals(3, history.size());

        assertEquals(
                ReviewType.STANDARD,
                history.get(0).getReviewType()
        );

        assertEquals(
                ReviewType.ENHANCED,
                history.get(1).getReviewType()
        );

        assertEquals(
                ReviewType.STANDARD,
                history.get(2).getReviewType()
        );

        assertEquals(
                "Standard review required.",
                history.get(0).getReason()
        );

        assertEquals(
                "Unlimited liability detected.",
                history.get(1).getReason()
        );

        assertEquals(
                "Enhanced review factors no longer detected.",
                history.get(2).getReason()
        );

        assertEquals("kirill", history.get(0).getDecidedBy());
        assertEquals("kirill", history.get(1).getDecidedBy());
        assertEquals("kirill", history.get(2).getDecidedBy());

        assertNotNull(history.get(0).getDecidedAt());
        assertNotNull(history.get(1).getDecidedAt());
        assertNotNull(history.get(2).getDecidedAt());
    }
}
