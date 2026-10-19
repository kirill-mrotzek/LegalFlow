package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidContractStatusTransitionException;
import de.kirillmrotzek.legalflow.lifecycle.ContractStatusTransitionValidator;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ContractLifecycleServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractStatusTransitionValidator transitionValidator;

    @Mock
    private AuditService auditService;

    private ContractLifecycleService lifecycleService;

    @BeforeEach
    void setUp() {
        lifecycleService = new ContractLifecycleService(
                contractRepository,
                transitionValidator,
                auditService
        );
    }

    @Test
    void shouldChangeStatusWhenTransitionIsAllowed() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.DRAFT);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ContractStatus.DRAFT,
                ContractStatus.SIGNED
        )).thenReturn(true);

        when(contractRepository.save(contract))
                .thenReturn(contract);

        Contract result = lifecycleService.changeStatus(
                1L,
                ContractStatus.SIGNED
        );

        assertEquals(
                ContractStatus.SIGNED,
                result.getContractStatus()
        );

        verify(contractRepository).save(contract);

        verify(auditService).log(
                AuditAction.STATUS_CHANGED,
                "Contract",
                1L
        );
    }

    @Test
    void shouldRejectInvalidTransition() {

        Contract contract = new Contract();
        contract.setContractStatus(ContractStatus.DRAFT);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ContractStatus.DRAFT,
                ContractStatus.ACTIVE
        )).thenReturn(false);

        assertThrows(
                InvalidContractStatusTransitionException.class,
                () -> lifecycleService.changeStatus(
                        1L,
                        ContractStatus.ACTIVE
                )
        );
        verify(contractRepository, never()).save(contract);

        verify(auditService, never()).log(
                any(),
                anyString(),
                anyLong()
        );
    }

    @Test
    void shouldThrowExceptionWhenContractDoesNotExist() {

        when(contractRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContractNotFoundException.class,
                () -> lifecycleService.changeStatus(
                        1L,
                        ContractStatus.SIGNED
                )
        );

        verify(auditService, never()).log(
                any(),
                anyString(),
                anyLong()
        );
    }

    @Test
    void shouldExpireContractAutomatically() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.ACTIVE);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ContractStatus.ACTIVE,
                ContractStatus.EXPIRED
        )).thenReturn(true);

        when(contractRepository.save(contract))
                .thenReturn(contract);

        Contract result =
                lifecycleService.expireContract(1L);

        assertEquals(
                ContractStatus.EXPIRED,
                result.getContractStatus()
        );

        verify(contractRepository)
                .save(contract);

        verify(auditService)
                .logSystem(
                        AuditAction.STATUS_CHANGED,
                        "Contract",
                        1L
                );

        verify(auditService, never())
                .log(
                        any(),
                        anyString(),
                        anyLong()
                );
    }

    @Test
    void shouldRejectAutomaticExpiryWhenTransitionIsInvalid() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.DRAFT);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        assertThrows(
                InvalidContractStatusTransitionException.class,
                () -> lifecycleService.expireContract(1L)
        );

        verify(contractRepository, never())
                .save(any(Contract.class));

        verify(auditService, never())
                .logSystem(
                        any(),
                        anyString(),
                        anyLong()
                );
    }
}
