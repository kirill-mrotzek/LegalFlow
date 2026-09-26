package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidReviewStatusTransitionException;
import de.kirillmrotzek.legalflow.lifecycle.ReviewStatusTransitionValidator;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewWorkflowService {

    private final ContractRepository contractRepository;
    private final ReviewStatusTransitionValidator transitionValidator;

    @Transactional
    public Contract changeStatus(
            Long contractId,
            ReviewStatus targetStatus
    ) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new ContractNotFoundException(contractId)
                );

        ReviewStatus currentStatus =
                contract.getReviewStatus();

        if (!transitionValidator.isAllowed(
                currentStatus,
                targetStatus
        )) {
            throw new InvalidReviewStatusTransitionException(
                    currentStatus,
                    targetStatus
            );
        }

        contract.setReviewStatus(targetStatus);

        return contractRepository.save(contract);
    }
}