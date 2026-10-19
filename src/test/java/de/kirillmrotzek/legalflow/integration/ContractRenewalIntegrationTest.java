package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.AuditEvent;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.ContractRenewalHistory;
import de.kirillmrotzek.legalflow.repository.AuditEventRepository;
import de.kirillmrotzek.legalflow.repository.ContractRenewalHistoryRepository;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.service.ContractRenewalService;
import de.kirillmrotzek.legalflow.service.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ContractRenewalIntegrationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractRenewalHistoryRepository renewalHistoryRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private ContractRenewalService contractRenewalService;

    @Autowired
    private ContractService contractService;

    @Test
    @WithMockUser(
            username = "kirill",
            authorities = "CONTRACT_UPDATE"
    )
    void shouldRenewContractAndPersistHistoryAndAuditEvent() {

        LocalDate oldEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2027, 12, 31);

        Contract contract = new Contract();

        contract.setTitle("Renewal Test Contract");
        contract.setContractNumber("REN-001");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(
                oldEndDate.minusYears(1)
        );
        contract.setEndDate(oldEndDate);
        contract.setCounterparty("Test Counterparty");
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractRepository.save(contract);

        Contract renewedContract =
                contractRenewalService.renewContract(
                        savedContract.getId(),
                        newEndDate
                );

        // Contract itself

        Contract persistedContract =
                contractRepository.findById(
                        savedContract.getId()
                ).orElseThrow();

        assertEquals(
                newEndDate,
                persistedContract.getEndDate()
        );

        assertEquals(
                ContractStatus.ACTIVE,
                persistedContract.getContractStatus()
        );

        assertEquals(
                newEndDate,
                renewedContract.getEndDate()
        );

        // Renewal history

        List<ContractRenewalHistory> histories =
                renewalHistoryRepository
                        .findByContractIdOrderByRenewedAtAsc(
                                savedContract.getId()
                        );

        assertEquals(1, histories.size());

        ContractRenewalHistory history =
                histories.get(0);

        assertEquals(
                savedContract.getId(),
                history.getContract().getId()
        );

        assertEquals(
                oldEndDate,
                history.getPreviousEndDate()
        );

        assertEquals(
                newEndDate,
                history.getNewEndDate()
        );

        assertEquals(
                "kirill",
                history.getRenewedBy()
        );

        assertNotNull(history.getRenewedAt());

        // Audit event

        List<AuditEvent> auditEvents =
                auditEventRepository.findAll()
                        .stream()
                        .filter(event ->
                                savedContract.getId().equals(
                                        event.getEntityId()
                                )
                        )
                        .toList();

        assertEquals(1, auditEvents.size());

        AuditEvent auditEvent =
                auditEvents.get(0);

        assertEquals(
                "kirill",
                auditEvent.getUsername()
        );

        assertEquals(
                AuditAction.CONTRACT_RENEWED,
                auditEvent.getAction()
        );

        assertEquals(
                "Contract",
                auditEvent.getEntityType()
        );

        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );

        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    @WithMockUser(
            username = "kirill",
            authorities = {
                    "CONTRACT_UPDATE",
                    "CONTRACT_DELETE"
            }
    )
    void shouldDeleteRenewedContractAndItsRenewalHistory() {

        LocalDate oldEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2027, 12, 31);

        Contract contract = new Contract();

        contract.setTitle("Renewal Delete Test Contract");
        contract.setContractNumber("REN-DELETE-001");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(
                oldEndDate.minusYears(1)
        );
        contract.setEndDate(oldEndDate);
        contract.setCounterparty("Test Counterparty");
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractRepository.save(contract);

        Long contractId = savedContract.getId();

        // Renew

        contractRenewalService.renewContract(
                contractId,
                newEndDate
        );

        // Verify renewal history exists

        List<ContractRenewalHistory> historiesBeforeDelete =
                renewalHistoryRepository
                        .findByContractIdOrderByRenewedAtAsc(
                                contractId
                        );

        assertEquals(1, historiesBeforeDelete.size());

        // Delete contract

        contractService.delete(contractId);

        // Verify contract was deleted

        assertEquals(
                0,
                contractRepository.findById(contractId).stream().count()
        );

        // Verify renewal history was deleted

        List<ContractRenewalHistory> historiesAfterDelete =
                renewalHistoryRepository
                        .findByContractIdOrderByRenewedAtAsc(
                                contractId
                        );

        assertEquals(0, historiesAfterDelete.size());
    }
}
