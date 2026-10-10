package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.model.ContractApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractApprovalRepository
        extends JpaRepository<ContractApproval, Long> {
}
