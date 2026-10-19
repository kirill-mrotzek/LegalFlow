package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractRenewalServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractRenewalHistoryService renewalHistoryService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ContractRenewalService contractRenewalService;

    @Test
    void shouldRenewActiveContract() {

        LocalDate oldEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2027, 12, 31);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setEndDate(oldEndDate);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(contractRepository.save(contract))
                .thenReturn(contract);

        Contract result =
                contractRenewalService.renewContract(
                        1L,
                        newEndDate
                );

        assertEquals(
                newEndDate,
                result.getEndDate()
        );

        verify(contractRepository)
                .save(contract);

        verify(renewalHistoryService)
                .record(
                        contract,
                        oldEndDate,
                        newEndDate
                );

        verify(auditService)
                .log(
                        AuditAction.CONTRACT_RENEWED,
                        "Contract",
                        1L
                );
    }

    @Test
    void shouldRejectRenewalForNonActiveContract() {

        LocalDate oldEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2027, 12, 31);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setEndDate(oldEndDate);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        assertThrows(
                IllegalStateException.class,
                () -> contractRenewalService.renewContract(
                        1L,
                        newEndDate
                )
        );

        verify(contractRepository, never())
                .save(any());

        verifyNoInteractions(renewalHistoryService);
        verifyNoInteractions(auditService);
    }

    @Test
    void shouldRejectRenewalWhenNewEndDateIsNotAfterCurrentEndDate() {

        LocalDate oldEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2026, 12, 31);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setEndDate(oldEndDate);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        assertThrows(
                IllegalArgumentException.class,
                () -> contractRenewalService.renewContract(
                        1L,
                        newEndDate
                )
        );

        verify(contractRepository, never())
                .save(any());

        verifyNoInteractions(renewalHistoryService);
        verifyNoInteractions(auditService);
    }

    @Test
    void shouldRejectRenewalWhenNewEndDateIsBeforeCurrentEndDate() {

        LocalDate oldEndDate =
                LocalDate.of(2026, 12, 31);

        LocalDate newEndDate =
                LocalDate.of(2026, 12, 30);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setEndDate(oldEndDate);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        assertThrows(
                IllegalArgumentException.class,
                () -> contractRenewalService.renewContract(
                        1L,
                        newEndDate
                )
        );

        verify(contractRepository, never())
                .save(any());

        verifyNoInteractions(renewalHistoryService);
        verifyNoInteractions(auditService);
    }
}
