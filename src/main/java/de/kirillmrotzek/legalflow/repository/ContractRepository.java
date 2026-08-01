package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

}
