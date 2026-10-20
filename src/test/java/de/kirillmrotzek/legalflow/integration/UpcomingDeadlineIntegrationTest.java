package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRenewalHistoryRepository;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.service.UpcomingDeadlineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UpcomingDeadlineIntegrationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UpcomingDeadlineService upcomingDeadlineService;

    @Autowired
    private ContractRenewalHistoryRepository contractRenewalHistoryRepository;

    @BeforeEach
    void setUp() {
        contractRenewalHistoryRepository.deleteAll();
        contractRepository.deleteAll();
    }

    @Test
    @WithMockUser(
            username = "kirill",
            authorities = {"CONTRACT_READ"}
    )
    void findUpcomingDeadlines_shouldReturnOnlyActiveContractsWithinRange() {

        LocalDate today = LocalDate.now();

        Contract yesterday = createContract(
                "Yesterday",
                ContractStatus.ACTIVE,
                today.minusDays(1)
        );

        Contract todayContract = createContract(
                "Today",
                ContractStatus.ACTIVE,
                today
        );

        Contract inSevenDays = createContract(
                "In Seven Days",
                ContractStatus.ACTIVE,
                today.plusDays(7)
        );

        Contract inEightDays = createContract(
                "In Eight Days",
                ContractStatus.ACTIVE,
                today.plusDays(8)
        );

        Contract expiredStatus = createContract(
                "Expired Status",
                ContractStatus.EXPIRED,
                today.plusDays(3)
        );

        contractRepository.saveAll(List.of(
                yesterday,
                todayContract,
                inSevenDays,
                inEightDays,
                expiredStatus
        ));

        List<Contract> result =
                upcomingDeadlineService.findUpcomingDeadlines(7);

        assertEquals(2, result.size());

        assertTrue(result.stream()
                .anyMatch(contract ->
                        "Today".equals(contract.getCounterparty())));

        assertTrue(result.stream()
                .anyMatch(contract ->
                        "In Seven Days".equals(contract.getCounterparty())));

        assertFalse(result.stream()
                .anyMatch(contract ->
                        "Yesterday".equals(contract.getCounterparty())));

        assertFalse(result.stream()
                .anyMatch(contract ->
                        "In Eight Days".equals(contract.getCounterparty())));

        assertFalse(result.stream()
                .anyMatch(contract ->
                        "Expired Status".equals(contract.getCounterparty())));
    }

    private Contract createContract(
            String counterparty,
            ContractStatus status,
            LocalDate endDate
    ) {
        Contract contract = new Contract();

        contract.setTitle("Test Contract - " + counterparty);
        contract.setContractNumber(
                "TEST-" + counterparty.toUpperCase().replace(" ", "-")
        );
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(status);
        contract.setReviewStatus(ReviewStatus.APPROVED);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(endDate.minusDays(30));
        contract.setEndDate(endDate);
        contract.setCounterparty(counterparty);
        contract.setLegalReviewType(ReviewType.STANDARD);

        return contract;
    }
}
