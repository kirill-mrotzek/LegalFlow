package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidContractStatusTransitionException;
import de.kirillmrotzek.legalflow.lifecycle.ContractStatusTransitionValidator;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractLifecycleService {

    private final ContractRepository contractRepository;
    private final ContractStatusTransitionValidator transitionValidator;
    private final AuditService auditService;

    @PreAuthorize("hasAuthority('CONTRACT_STATUS_CHANGE')")
    @Transactional
    public Contract changeStatus(
            Long contractId,
            ContractStatus targetStatus
    ) {

        Contract contract =
                changeStatusInternal(contractId, targetStatus);

        auditService.log(
                AuditAction.STATUS_CHANGED,
                "Contract",
                contract.getId()
        );

        return contract;
    }

    @Transactional
    public Contract expireContract(Long contractId) {

        Contract contract =
                changeStatusInternal(
                        contractId,
                        ContractStatus.EXPIRED
                );

        auditService.logSystem(
                AuditAction.STATUS_CHANGED,
                "Contract",
                contract.getId()
        );

        return contract;
    }

    private Contract changeStatusInternal(
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
