package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.ContractRenewalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRenewalHistoryRepository
        extends JpaRepository<ContractRenewalHistory, Long> {

    List<ContractRenewalHistory>
    findByContractIdOrderByRenewedAtAsc(Long contractId);

    void deleteByContractId(Long contractId);
}
