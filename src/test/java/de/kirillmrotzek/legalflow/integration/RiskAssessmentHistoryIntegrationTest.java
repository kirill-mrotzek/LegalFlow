package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.RiskAssessment;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.repository.RiskAssessmentRepository;
import de.kirillmrotzek.legalflow.service.RiskAssessmentHistoryService;
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
class RiskAssessmentHistoryIntegrationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    private RiskAssessmentHistoryService historyService;

    @Test
    @WithMockUser(username = "kirill")
    void shouldStoreRiskAssessmentHistoryInChronologicalOrder() {

        Contract contract = new Contract();

        contract.setContractNumber("HISTORY-001");
        contract.setTitle("Risk History Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setContractType(ContractType.SERVICE);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setLegalReviewType(ReviewType.STANDARD);
        contract.setLegalReviewReason("Standard legal review required");
        contract.setContractValue(new BigDecimal("10000"));
        contract.setStartDate(LocalDate.of(2026, 1, 1));
        contract.setEndDate(LocalDate.of(2027, 1, 1));
        contract.setAutoRenewal(false);

        Contract savedContract =
                contractRepository.save(contract);

        historyService.record(
                savedContract,
                RiskLevel.MEDIUM
        );

        historyService.record(
                savedContract,
                RiskLevel.HIGH
        );

        historyService.record(
                savedContract,
                RiskLevel.MEDIUM
        );

        List<RiskAssessment> history =
                riskAssessmentRepository
                        .findByContractIdOrderByAssessedAtAsc(
                                savedContract.getId()
                        );

        assertEquals(3, history.size());

        assertEquals(
                RiskLevel.MEDIUM,
                history.get(0).getRiskLevel()
        );

        assertEquals(
                RiskLevel.HIGH,
                history.get(1).getRiskLevel()
        );

        assertEquals(
                RiskLevel.MEDIUM,
                history.get(2).getRiskLevel()
        );

        assertEquals(
                "kirill",
                history.get(0).getAssessedBy()
        );

        assertEquals(
                "kirill",
                history.get(1).getAssessedBy()
        );

        assertEquals(
                "kirill",
                history.get(2).getAssessedBy()
        );

        assertNotNull(history.get(0).getAssessedAt());
        assertNotNull(history.get(1).getAssessedAt());
        assertNotNull(history.get(2).getAssessedAt());
    }
}