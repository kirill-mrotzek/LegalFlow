package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.ContractRenewalHistory;
import de.kirillmrotzek.legalflow.repository.ContractRenewalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractRenewalHistoryService {

    private final ContractRenewalHistoryRepository repository;

    public void record(
            Contract contract,
            LocalDate previousEndDate,
            LocalDate newEndDate
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        ContractRenewalHistory history =
                new ContractRenewalHistory();

        history.setContract(contract);
        history.setPreviousEndDate(previousEndDate);
        history.setNewEndDate(newEndDate);
        history.setRenewedAt(LocalDateTime.now());
        history.setRenewedBy(authentication.getName());

        repository.save(history);
    }

    public void deleteByContractId(Long contractId) {
        repository.deleteByContractId(contractId);
    }
}
