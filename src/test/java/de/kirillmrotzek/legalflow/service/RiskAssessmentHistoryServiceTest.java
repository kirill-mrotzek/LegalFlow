package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.RiskAssessment;
import de.kirillmrotzek.legalflow.repository.RiskAssessmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentHistoryServiceTest {

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @InjectMocks
    private RiskAssessmentHistoryService historyService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void record_shouldPersistRiskAssessmentHistory() {

        Contract contract = new Contract();
        contract.setId(1L);

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("kirill");

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        historyService.record(
                contract,
                RiskLevel.HIGH
        );

        ArgumentCaptor<RiskAssessment> captor =
                ArgumentCaptor.forClass(RiskAssessment.class);

        verify(riskAssessmentRepository)
                .save(captor.capture());

        RiskAssessment savedAssessment =
                captor.getValue();

        assertSame(
                contract,
                savedAssessment.getContract()
        );

        assertEquals(
                RiskLevel.HIGH,
                savedAssessment.getRiskLevel()
        );

        assertEquals(
                "kirill",
                savedAssessment.getAssessedBy()
        );

        assertNotNull(
                savedAssessment.getAssessedAt()
        );
    }
}
