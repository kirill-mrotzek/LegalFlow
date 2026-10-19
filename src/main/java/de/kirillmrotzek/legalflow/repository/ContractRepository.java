package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository
        extends JpaRepository<Contract, Long>,
        JpaSpecificationExecutor<Contract> {

    List<Contract> findByContractStatusAndEndDateBefore(
            ContractStatus contractStatus,
            LocalDate date
    );
}