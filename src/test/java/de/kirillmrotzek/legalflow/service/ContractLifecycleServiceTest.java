package de.kirillmrotzek.legalflow.service;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ContractLifecycleServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractStatusTransitionValidator transitionValidator;

    private ContractLifecycleService lifecycleService;

    @BeforeEach
    void setUp() {
        lifecycleService = new ContractLifecycleService(
                contractRepository,
                transitionValidator
        );
    }

    @Test
    void shouldChangeStatusWhenTransitionIsAllowed() {

        Contract contract = new Contract();
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
    }
}
