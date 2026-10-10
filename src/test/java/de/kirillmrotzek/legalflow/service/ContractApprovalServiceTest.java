package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ApprovalRole;
import de.kirillmrotzek.legalflow.enums.ApprovalStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.ContractApproval;
import de.kirillmrotzek.legalflow.repository.ContractApprovalRepository;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractApprovalServiceTest {

    @Mock
    private ContractApprovalRepository contractApprovalRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ApprovalStatusTransitionValidator transitionValidator;

    @InjectMocks
    private ContractApprovalService contractApprovalService;

    @Test
    void createApproval_shouldCreatePendingApproval() {

        Contract contract = new Contract();
        contract.setId(1L);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        ContractApproval savedApproval = new ContractApproval();
        savedApproval.setContract(contract);
        savedApproval.setApprovalRole(ApprovalRole.LEGAL);
        savedApproval.setStatus(ApprovalStatus.PENDING);

        when(contractApprovalRepository.save(any(ContractApproval.class)))
                .thenReturn(savedApproval);

        ContractApproval result =
                contractApprovalService.createApproval(
                        1L,
                        ApprovalRole.LEGAL
                );

        assertThat(result.getContract()).isSameAs(contract);
        assertThat(result.getApprovalRole())
                .isEqualTo(ApprovalRole.LEGAL);
        assertThat(result.getStatus())
                .isEqualTo(ApprovalStatus.PENDING);
    }

    @Test
    void approve_shouldApprovePendingApproval() {

        Contract contract = new Contract();
        contract.setId(1L);

        ContractApproval approval = new ContractApproval();
        approval.setId(10L);
        approval.setContract(contract);
        approval.setApprovalRole(ApprovalRole.LEGAL);
        approval.setStatus(ApprovalStatus.PENDING);

        when(contractApprovalRepository.findById(10L))
                .thenReturn(Optional.of(approval));

        when(transitionValidator.isAllowed(
                ApprovalStatus.PENDING,
                ApprovalStatus.APPROVED
        )).thenReturn(true);

        when(contractApprovalRepository.save(approval))
                .thenReturn(approval);

        ContractApproval result =
                contractApprovalService.approve(
                        10L,
                        "kirill"
                );

        assertThat(result.getStatus())
                .isEqualTo(ApprovalStatus.APPROVED);

        assertThat(result.getDecidedBy())
                .isEqualTo("kirill");

        assertThat(result.getDecidedAt())
                .isNotNull();
    }

    @Test
    void reject_shouldRejectPendingApproval() {

        Contract contract = new Contract();
        contract.setId(1L);

        ContractApproval approval = new ContractApproval();
        approval.setId(10L);
        approval.setContract(contract);
        approval.setApprovalRole(ApprovalRole.LEGAL);
        approval.setStatus(ApprovalStatus.PENDING);

        when(contractApprovalRepository.findById(10L))
                .thenReturn(Optional.of(approval));

        when(transitionValidator.isAllowed(
                ApprovalStatus.PENDING,
                ApprovalStatus.REJECTED
        )).thenReturn(true);

        when(contractApprovalRepository.save(approval))
                .thenReturn(approval);

        ContractApproval result =
                contractApprovalService.reject(
                        10L,
                        "kirill"
                );

        assertThat(result.getStatus())
                .isEqualTo(ApprovalStatus.REJECTED);

        assertThat(result.getDecidedBy())
                .isEqualTo("kirill");

        assertThat(result.getDecidedAt())
                .isNotNull();
    }

    @Test
    void reject_shouldRejectApprovedApproval() {

        Contract contract = new Contract();
        contract.setId(1L);

        ContractApproval approval = new ContractApproval();
        approval.setId(10L);
        approval.setContract(contract);
        approval.setApprovalRole(ApprovalRole.LEGAL);
        approval.setStatus(ApprovalStatus.APPROVED);

        when(contractApprovalRepository.findById(10L))
                .thenReturn(Optional.of(approval));

        when(transitionValidator.isAllowed(
                ApprovalStatus.APPROVED,
                ApprovalStatus.REJECTED
        )).thenReturn(false);

        assertThatThrownBy(() ->
                contractApprovalService.reject(
                        10L,
                        "kirill"
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Invalid approval status transition"
                );
        verify(contractApprovalRepository, never())
                .save(any(ContractApproval.class));

    }

    @Test
    void approve_shouldThrowWhenApprovalDoesNotExist() {

        when(contractApprovalRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                contractApprovalService.approve(999L, "kirill")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Approval not found: 999");

        verify(contractApprovalRepository, never())
                .save(any(ContractApproval.class));
    }

    @Test
    void reject_shouldThrowWhenApprovalDoesNotExist() {

        when(contractApprovalRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                contractApprovalService.reject(999L, "kirill")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Approval not found: 999");

        verify(contractApprovalRepository, never())
                .save(any(ContractApproval.class));
    }

}
