package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidReviewStatusTransitionException;
import de.kirillmrotzek.legalflow.lifecycle.ReviewStatusTransitionValidator;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewWorkflowService {

    private final ContractRepository contractRepository;
    private final ReviewStatusTransitionValidator transitionValidator;

    @PreAuthorize("hasAuthority('REVIEW_STATUS_CHANGE')")
    @Transactional
    public Contract changeStatus(Long contractId, ReviewStatus targetStatus) {
        return changeStatusInternal(contractId, targetStatus);
    }

    private Contract changeStatusInternal(
            Long contractId,
            ReviewStatus targetStatus) {

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException(contractId));

        ReviewStatus currentStatus = contract.getReviewStatus();

        if (!transitionValidator.isAllowed(currentStatus, targetStatus)) {
            throw new InvalidReviewStatusTransitionException(
                    currentStatus,
                    targetStatus
            );
        }

        contract.setReviewStatus(targetStatus);

        return contractRepository.save(contract);
    }

    @PreAuthorize("hasAuthority('REVIEW_START')")
    @Transactional
    public Contract startReview(Long contractId) {
        return changeStatusInternal(contractId, ReviewStatus.IN_REVIEW);
    }

    @PreAuthorize("hasAuthority('REVIEW_APPROVE')")
    @Transactional
    public Contract approveReview(Long contractId) {
        return changeStatusInternal(contractId, ReviewStatus.APPROVED);
    }

    @PreAuthorize("hasAuthority('REVIEW_REJECT')")
    @Transactional
    public Contract rejectReview(Long contractId) {
        return changeStatusInternal(contractId, ReviewStatus.REJECTED);
    }
}
