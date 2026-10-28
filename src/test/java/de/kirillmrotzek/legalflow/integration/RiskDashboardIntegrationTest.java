package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.dto.RiskDashboardResponse;
import de.kirillmrotzek.legalflow.enums.*;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRenewalHistoryRepository;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.service.RiskDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class RiskDashboardIntegrationTest {

    @Autowired
    private RiskDashboardService riskDashboardService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractRenewalHistoryRepository contractRenewalHistoryRepository;

    @BeforeEach
    void setUp() {
        contractRenewalHistoryRepository.deleteAll();
        contractRepository.deleteAll();
    }
    @WithMockUser(
            username = "kirill",
            authorities = {"CONTRACT_READ"}
    )
    @Test
    void getRiskDashboard_shouldReturnCorrectAggregatedValues() {

        LocalDate today = LocalDate.now();

        Contract highRiskPendingExpiring =
                createContract(
                        RiskLevel.HIGH,
                        ReviewStatus.PENDING,
                        ContractStatus.ACTIVE,
                        today.plusDays(5)
                );

        Contract highRiskApprovedActive =
                createContract(
                        RiskLevel.HIGH,
                        ReviewStatus.APPROVED,
                        ContractStatus.ACTIVE,
                        today.plusDays(60)
                );

        Contract mediumRiskPendingExpiring =
                createContract(
                        RiskLevel.MEDIUM,
                        ReviewStatus.PENDING,
                        ContractStatus.ACTIVE,
                        today.plusDays(7)
                );

        Contract mediumRiskRejectedActive =
                createContract(
                        RiskLevel.MEDIUM,
                        ReviewStatus.REJECTED,
                        ContractStatus.ACTIVE,
                        today.plusDays(30)
                );

        Contract lowRiskPendingNotExpiring =
                createContract(
                        RiskLevel.LOW,
                        ReviewStatus.PENDING,
                        ContractStatus.ACTIVE,
                        today.plusDays(31)
                );

        Contract lowRiskApprovedExpired =
                createContract(
                        RiskLevel.LOW,
                        ReviewStatus.APPROVED,
                        ContractStatus.EXPIRED,
                        today.minusDays(1)
                );

        contractRepository.save(highRiskPendingExpiring);
        contractRepository.save(highRiskApprovedActive);
        contractRepository.save(mediumRiskPendingExpiring);
        contractRepository.save(mediumRiskRejectedActive);
        contractRepository.save(lowRiskPendingNotExpiring);
        contractRepository.save(lowRiskApprovedExpired);

        RiskDashboardResponse response =
                riskDashboardService.getRiskDashboard(7);

        assertEquals(6L, response.getTotalContracts());

        assertEquals(2L, response.getHighRiskContracts());
        assertEquals(2L, response.getMediumRiskContracts());
        assertEquals(2L, response.getLowRiskContracts());

        assertEquals(3L, response.getPendingReviews());

        assertEquals(2L, response.getExpiringContracts());
    }

    private Contract createContract(
            RiskLevel riskLevel,
            ReviewStatus reviewStatus,
            ContractStatus contractStatus,
            LocalDate endDate
    ) {
        Contract contract = new Contract();

        contract.setContractNumber("TEST-" + System.nanoTime());
        contract.setTitle("Test Contract");
        contract.setCounterparty("Test Counterparty");
        contract.setContractType(ContractType.SERVICE);

        contract.setStartDate(LocalDate.now().minusDays(30));
        contract.setEndDate(endDate);

        contract.setContractStatus(contractStatus);
        contract.setRiskLevel(riskLevel);
        contract.setReviewStatus(reviewStatus);
        contract.setLegalReviewType(ReviewType.STANDARD);

        contract.setContractValue(BigDecimal.valueOf(10_000));
        contract.setAutoRenewal(false);
        contract.setUnlimitedLiability(false);

        return contract;
    }
}
