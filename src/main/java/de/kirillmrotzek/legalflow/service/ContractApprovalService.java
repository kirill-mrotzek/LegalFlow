package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ApprovalRole;
import de.kirillmrotzek.legalflow.enums.ApprovalStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.ContractApproval;
import de.kirillmrotzek.legalflow.repository.ContractApprovalRepository;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractApprovalService {

    private final ContractApprovalRepository contractApprovalRepository;
    private final ContractRepository contractRepository;
    private final ApprovalStatusTransitionValidator transitionValidator;

    @Transactional
    public ContractApproval createApproval(
            Long contractId,
            ApprovalRole approvalRole) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Contract not found: " + contractId
                        ));

        ContractApproval approval = new ContractApproval();
        approval.setContract(contract);
        approval.setApprovalRole(approvalRole);
        approval.setStatus(ApprovalStatus.PENDING);

        return contractApprovalRepository.save(approval);
    }

    @Transactional
    public ContractApproval approve(
            Long approvalId,
            String decidedBy) {

        ContractApproval approval = findById(approvalId);

        changeStatus(
                approval,
                ApprovalStatus.APPROVED,
                decidedBy
        );

        return contractApprovalRepository.save(approval);
    }

    @Transactional
    public ContractApproval reject(
            Long approvalId,
            String decidedBy) {

        ContractApproval approval = findById(approvalId);

        changeStatus(
                approval,
                ApprovalStatus.REJECTED,
                decidedBy
        );

        return contractApprovalRepository.save(approval);
    }

    private ContractApproval findById(Long approvalId) {
        return contractApprovalRepository.findById(approvalId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Approval not found: " + approvalId
                        ));
    }

    private void changeStatus(
            ContractApproval approval,
            ApprovalStatus targetStatus,
            String decidedBy) {

        if (!transitionValidator.isAllowed(
                approval.getStatus(),
                targetStatus)) {

            throw new IllegalStateException(
                    "Invalid approval status transition: "
                            + approval.getStatus()
                            + " -> "
                            + targetStatus
            );
        }

        approval.setStatus(targetStatus);
        approval.setDecidedBy(decidedBy);
        approval.setDecidedAt(LocalDateTime.now());
    }
}
