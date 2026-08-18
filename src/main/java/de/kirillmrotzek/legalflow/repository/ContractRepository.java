package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractRepository
        extends JpaRepository<Contract, Long>,
        JpaSpecificationExecutor<Contract> {
}