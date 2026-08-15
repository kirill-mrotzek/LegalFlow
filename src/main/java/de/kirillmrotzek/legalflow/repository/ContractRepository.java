package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByContractStatus(ContractStatus contractStatus);

    List<Contract> findByCounterpartyContainingIgnoreCase(String counterparty);

    List<Contract> findByContractStatusAndCounterpartyContainingIgnoreCase(
            ContractStatus contractStatus,
            String counterparty
    );
}