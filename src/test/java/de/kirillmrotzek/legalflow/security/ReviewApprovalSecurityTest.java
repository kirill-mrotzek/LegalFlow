package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewApprovalSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void userWithReviewApproveAuthority_shouldApproveContractReview()
            throws Exception {

        Contract contract = createContractInReview();
        contract = contractRepository.save(contract);

        mockMvc.perform(
                        post("/contracts/{id}/review/approve", contract.getId())
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().isOk());

        Contract updatedContract = contractRepository
                .findById(contract.getId())
                .orElseThrow();

        assertThat(updatedContract.getReviewStatus())
                .isEqualTo(ReviewStatus.APPROVED);
    }

    private Contract createContractInReview() {
        Contract contract = new Contract();

        contract.setTitle("RBAC Test Contract");
        contract.setContractNumber("RBAC-" + System.nanoTime());
        contract.setContractType(ContractType.SERVICE);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.IN_REVIEW);
        contract.setRiskLevel(RiskLevel.LOW);

        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));

        contract.setAutoRenewal(false);
        contract.setUnlimitedLiability(false);

        contract.setCounterparty("RBAC Test Counterparty");
        contract.setGoverningLaw("German law");

        contract.setContractValue(BigDecimal.valueOf(1000));

        contract.setLegalReviewType(ReviewType.STANDARD);
        contract.setLegalReviewReason("RBAC integration test");

        return contract;
    }
}
