package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Page<Contract> findByContractStatus(
            ContractStatus contractStatus,
            Pageable pageable
    );

    Page<Contract> findByCounterpartyContainingIgnoreCase(
            String counterparty,
            Pageable pageable
    );

    Page<Contract> findByContractStatusAndCounterpartyContainingIgnoreCase(
            ContractStatus contractStatus,
            String counterparty,
            Pageable pageable
    );
}