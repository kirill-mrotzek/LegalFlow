package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.ContractRenewalHistory;
import de.kirillmrotzek.legalflow.repository.ContractRenewalHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractRenewalHistoryServiceTest {

    @Mock
    private ContractRenewalHistoryRepository repository;

    @InjectMocks
    private ContractRenewalHistoryService renewalHistoryService;

    @Test
    void shouldRecordRenewalHistory() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.ACTIVE);

        LocalDate previousEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2027, 12, 31);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "kirill",
                        null
                )
        );

        renewalHistoryService.record(
                contract,
                previousEndDate,
                newEndDate
        );

        ArgumentCaptor<ContractRenewalHistory> captor =
                ArgumentCaptor.forClass(
                        ContractRenewalHistory.class
                );

        verify(repository).save(captor.capture());

        ContractRenewalHistory history =
                captor.getValue();

        assertEquals(
                contract,
                history.getContract()
        );

        assertEquals(
                previousEndDate,
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
    }
}
