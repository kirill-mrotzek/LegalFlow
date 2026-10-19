package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractExpiryService {

    private final ContractRepository contractRepository;
    private final ContractLifecycleService contractLifecycleService;

    public void expireContracts(LocalDate today) {

        List<Contract> expiredContracts =
                contractRepository.findByContractStatusAndEndDateBefore(
                        ContractStatus.ACTIVE,
                        today
                );

        for (Contract contract : expiredContracts) {
            contractLifecycleService.expireContract(
                    contract.getId()
            );
        }
    }
}
