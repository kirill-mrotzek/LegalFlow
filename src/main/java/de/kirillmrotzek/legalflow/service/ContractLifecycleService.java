package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidContractStatusTransitionException;
import de.kirillmrotzek.legalflow.lifecycle.ContractStatusTransitionValidator;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractLifecycleService {

    private final ContractRepository contractRepository;
    private final ContractStatusTransitionValidator transitionValidator;

    @Transactional
    public Contract changeStatus(
            Long contractId,
            ContractStatus targetStatus
    ) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new ContractNotFoundException(contractId)
                );

        ContractStatus currentStatus =
                contract.getContractStatus();

        if (!transitionValidator.isAllowed(
                currentStatus,
                targetStatus
        )) {
            throw new InvalidContractStatusTransitionException(
                    currentStatus,
                    targetStatus
            );
        }

        contract.setContractStatus(targetStatus);

        return contractRepository.save(contract);
    }
}
