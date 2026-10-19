package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractExpiryServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractLifecycleService contractLifecycleService;

    @InjectMocks
    private ContractExpiryService contractExpiryService;

    @Test
    void shouldExpireOverdueContracts() {

        LocalDate today = LocalDate.of(2026, 10, 19);

        Contract contract1 = new Contract();
        contract1.setId(1L);

        Contract contract2 = new Contract();
        contract2.setId(2L);

        when(contractRepository.findByContractStatusAndEndDateBefore(
                ContractStatus.ACTIVE,
                today
        )).thenReturn(List.of(contract1, contract2));

        contractExpiryService.expireContracts(today);

        verify(contractLifecycleService)
                .expireContract(1L);

        verify(contractLifecycleService)
                .expireContract(2L);
    }

    @Test
    void shouldNotChangeStatusWhenThereAreNoExpiredContracts() {

        LocalDate today = LocalDate.of(2026, 10, 19);

        when(contractRepository.findByContractStatusAndEndDateBefore(
                ContractStatus.ACTIVE,
                today
        )).thenReturn(List.of());

        contractExpiryService.expireContracts(today);

        verifyNoInteractions(contractLifecycleService);
    }
}
