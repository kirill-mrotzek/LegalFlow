package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.enums.ApprovalRole;
import de.kirillmrotzek.legalflow.enums.ApprovalStatus;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.ContractApproval;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContractApprovalRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractApprovalRepository contractApprovalRepository;

    @Test
    void shouldSaveAndLoadContractApproval() {

        Contract contract = new Contract();

        contract.setTitle("Repository Test Contract");
        contract.setContractNumber("REPO-" + System.nanoTime());
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.IN_REVIEW);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setAutoRenewal(false);
        contract.setUnlimitedLiability(false);
        contract.setCounterparty("Repository Test Counterparty");
        contract.setGoverningLaw("German law");
        contract.setContractValue(BigDecimal.valueOf(1000));
        contract.setLegalReviewType(ReviewType.STANDARD);
        contract.setLegalReviewReason("Repository integration test");

        Contract savedContract = contractRepository.save(contract);

        ContractApproval approval = new ContractApproval();
        approval.setContract(savedContract);
        approval.setApprovalRole(ApprovalRole.LEGAL);
        approval.setStatus(ApprovalStatus.PENDING);

        ContractApproval savedApproval =
                contractApprovalRepository.save(approval);

        ContractApproval loadedApproval =
                contractApprovalRepository.findById(savedApproval.getId())
                        .orElseThrow();

        assertThat(loadedApproval.getApprovalRole())
                .isEqualTo(ApprovalRole.LEGAL);

        assertThat(loadedApproval.getStatus())
                .isEqualTo(ApprovalStatus.PENDING);

        assertThat(loadedApproval.getContract().getId())
                .isEqualTo(savedContract.getId());
    }
}
