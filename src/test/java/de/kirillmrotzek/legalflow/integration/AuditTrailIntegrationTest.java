package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.*;
import de.kirillmrotzek.legalflow.model.AuditEvent;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.AuditEventRepository;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.ReviewWorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuditTrailIntegrationTest {

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ReviewWorkflowService reviewWorkflowService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("CONTRACT_CREATE")
                        )
                )
        );

        auditEventRepository.deleteAll();
    }

    @Test
    void shouldPersistAuditEvent() {

        AuditEvent event = new AuditEvent();

        event.setUsername("kirill");
        event.setAction(AuditAction.CONTRACT_CREATED);
        event.setEntityType("Contract");
        event.setEntityId(1L);
        event.setTimestamp(LocalDateTime.now());

        auditEventRepository.save(event);

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(1, events.size());

        AuditEvent savedEvent = events.get(0);

        assertNotNull(savedEvent.getId());
        assertEquals("kirill", savedEvent.getUsername());
        assertEquals(
                AuditAction.CONTRACT_CREATED,
                savedEvent.getAction()
        );
        assertEquals("Contract", savedEvent.getEntityType());
        assertEquals(1L, savedEvent.getEntityId());
        assertNotNull(savedEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenContractIsCreated() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-001");
        contract.setTitle("Audit Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(1, events.size());

        AuditEvent auditEvent = events.get(0);

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(
                AuditAction.CONTRACT_CREATED,
                auditEvent.getAction()
        );
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenReviewIsStarted() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-REVIEW-001");
        contract.setTitle("Audit Review Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("REVIEW_START")
                        )
                )
        );

        reviewWorkflowService.startReview(savedContract.getId());

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(2, events.size());

        AuditEvent auditEvent = events.stream()
                .filter(event ->
                        event.getAction() == AuditAction.REVIEW_STARTED
                )
                .findFirst()
                .orElseThrow();

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(AuditAction.REVIEW_STARTED, auditEvent.getAction());
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenReviewIsApproved() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-APPROVE-001");
        contract.setTitle("Audit Approve Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("REVIEW_START")
                        )
                )
        );

        reviewWorkflowService.startReview(savedContract.getId());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("REVIEW_APPROVE")
                        )
                )
        );

        reviewWorkflowService.approveReview(savedContract.getId());

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(3, events.size());

        AuditEvent auditEvent = events.stream()
                .filter(event ->
                        event.getAction() == AuditAction.REVIEW_APPROVED
                )
                .findFirst()
                .orElseThrow();

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(
                AuditAction.REVIEW_APPROVED,
                auditEvent.getAction()
        );
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenReviewIsRejected() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-REJECT-001");
        contract.setTitle("Audit Reject Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("REVIEW_START")
                        )
                )
        );

        reviewWorkflowService.startReview(savedContract.getId());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("REVIEW_REJECT")
                        )
                )
        );

        reviewWorkflowService.rejectReview(savedContract.getId());

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(3, events.size());

        AuditEvent auditEvent = events.stream()
                .filter(event ->
                        event.getAction() == AuditAction.REVIEW_REJECTED
                )
                .findFirst()
                .orElseThrow();

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(
                AuditAction.REVIEW_REJECTED,
                auditEvent.getAction()
        );
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenReviewStatusIsChanged() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-STATUS-001");
        contract.setTitle("Audit Status Change Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("REVIEW_STATUS_CHANGE")
                        )
                )
        );

        reviewWorkflowService.changeStatus(
                savedContract.getId(),
                ReviewStatus.IN_REVIEW
        );

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(2, events.size());

        AuditEvent auditEvent = events.stream()
                .filter(event ->
                        event.getAction() == AuditAction.STATUS_CHANGED
                )
                .findFirst()
                .orElseThrow();

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(
                AuditAction.STATUS_CHANGED,
                auditEvent.getAction()
        );
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenContractIsUpdated() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-UPDATE-001");
        contract.setTitle("Audit Update Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        savedContract.setTitle("Updated Audit Test Contract");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("CONTRACT_UPDATE")
                        )
                )
        );

        contractService.update(
                savedContract.getId(),
                savedContract
        );

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(2, events.size());

        AuditEvent auditEvent = events.stream()
                .filter(event ->
                        event.getAction() == AuditAction.CONTRACT_UPDATED
                )
                .findFirst()
                .orElseThrow();

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(
                AuditAction.CONTRACT_UPDATED,
                auditEvent.getAction()
        );
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }

    @Test
    void shouldCreateAuditEventWhenContractIsDeleted() {

        Contract contract = new Contract();

        contract.setContractNumber("AUDIT-DELETE-001");
        contract.setTitle("Audit Delete Test Contract");
        contract.setCounterparty("Test Company");
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setLegalReviewType(ReviewType.STANDARD);

        Contract savedContract =
                contractService.save(contract);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password",
                        List.of(
                                new SimpleGrantedAuthority("CONTRACT_DELETE")
                        )
                )
        );

        contractService.delete(savedContract.getId());

        List<AuditEvent> events =
                auditEventRepository.findAll();

        assertEquals(2, events.size());

        AuditEvent auditEvent = events.stream()
                .filter(event ->
                        event.getAction() == AuditAction.CONTRACT_DELETED
                )
                .findFirst()
                .orElseThrow();

        assertNotNull(auditEvent.getId());
        assertEquals("kirill", auditEvent.getUsername());
        assertEquals(
                AuditAction.CONTRACT_DELETED,
                auditEvent.getAction()
        );
        assertEquals("Contract", auditEvent.getEntityType());
        assertEquals(
                savedContract.getId(),
                auditEvent.getEntityId()
        );
        assertNotNull(auditEvent.getTimestamp());
    }
}
