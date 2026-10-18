package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.LegalReviewDecision;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.LegalReviewDecisionHistory;
import de.kirillmrotzek.legalflow.repository.LegalReviewDecisionHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LegalReviewDecisionHistoryServiceTest {

    @Mock
    private LegalReviewDecisionHistoryRepository repository;

    private LegalReviewDecisionHistoryService service;

    @BeforeEach
    void setUp() {
        service =
                new LegalReviewDecisionHistoryService(repository);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        "password"
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRecordDecisionHistory() {

        Contract contract = new Contract();

        LegalReviewDecision decision =
                new LegalReviewDecision(
                        ReviewType.ENHANCED,
                        "Unlimited liability detected."
                );

        service.record(contract, decision);

        ArgumentCaptor<LegalReviewDecisionHistory> captor =
                ArgumentCaptor.forClass(
                        LegalReviewDecisionHistory.class
                );

        verify(repository).save(captor.capture());

        LegalReviewDecisionHistory history =
                captor.getValue();

        assertSame(contract, history.getContract());
        assertEquals(
                ReviewType.ENHANCED,
                history.getReviewType()
        );
        assertEquals(
                "Unlimited liability detected.",
                history.getReason()
        );
        assertEquals(
                "kirill",
                history.getDecidedBy()
        );
        assertNotNull(history.getDecidedAt());
    }

    @Test
    void shouldDeleteDecisionHistoryByContractId() {

        Long contractId = 42L;

        service.deleteByContractId(contractId);

        verify(repository)
                .deleteByContractId(contractId);
    }
}
