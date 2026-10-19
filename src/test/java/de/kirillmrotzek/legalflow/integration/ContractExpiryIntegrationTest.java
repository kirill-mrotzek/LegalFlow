package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.model.AuditEvent;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.AuditEventRepository;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.service.ContractExpiryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContractExpiryIntegrationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private ContractExpiryService contractExpiryService;

    @Test
    void shouldExpireOverdueActiveContractAndCreateSystemAuditEvent() {

        LocalDate today = LocalDate.of(2026, 10, 19);

        Contract contract = new Contract();

        contract.setTitle("Expiry Test Contract");
        contract.setContractNumber("EXP-001");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(today.minusYears(1));
        contract.setEndDate(today.minusDays(1));
        contract.setCounterparty("Test Counterparty");
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractRepository.save(contract);

        contractExpiryService.expireContracts(today);

        Contract expiredContract =
                contractRepository.findById(savedContract.getId())
                        .orElseThrow();

        assertEquals(
                ContractStatus.EXPIRED,
                expiredContract.getContractStatus()
        );

        List<AuditEvent> auditEvents =
                auditEventRepository.findAll();

        assertEquals(1, auditEvents.size());

        AuditEvent auditEvent = auditEvents.get(0);

        assertEquals(
                "SYSTEM",
                auditEvent.getUsername()
        );

        assertEquals(
                AuditAction.STATUS_CHANGED,
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
    }
}
