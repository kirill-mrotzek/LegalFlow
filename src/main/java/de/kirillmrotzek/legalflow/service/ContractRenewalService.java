package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.AuditAction;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ContractRenewalService {

    private final ContractRepository contractRepository;
    private final ContractRenewalHistoryService renewalHistoryService;
    private final AuditService auditService;

    @PreAuthorize("hasAuthority('CONTRACT_UPDATE')")
    @Transactional
    public Contract renewContract(
            Long contractId,
            LocalDate newEndDate
    ) {

        Contract contract =
                contractRepository.findById(contractId)
                        .orElseThrow(() ->
                                new ContractNotFoundException(contractId)
                        );

        if (contract.getContractStatus()
                != ContractStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Only ACTIVE contracts can be renewed"
            );
        }

        LocalDate previousEndDate =
                contract.getEndDate();

        if (!newEndDate.isAfter(previousEndDate)) {

            throw new IllegalArgumentException(
                    "New end date must be after current end date"
            );
        }

        contract.setEndDate(newEndDate);

        Contract savedContract =
                contractRepository.save(contract);

        renewalHistoryService.record(
                savedContract,
                previousEndDate,
                newEndDate
        );

        auditService.log(
                AuditAction.CONTRACT_RENEWED,
                "Contract",
                savedContract.getId()
        );

        return savedContract;
    }
}
