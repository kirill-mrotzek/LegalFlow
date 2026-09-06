package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskRule;
import de.kirillmrotzek.legalflow.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RiskEngineIntegrationTest {

    @Autowired
    private List<RiskRule> rules;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @Test
    void springContext_shouldRegisterAllRiskRules() {

        assertEquals(5, rules.size());

        assertTrue(rules.stream()
                .anyMatch(rule -> rule.getClass().getSimpleName()
                        .equals("HighContractValueRule")));

        assertTrue(rules.stream()
                .anyMatch(rule -> rule.getClass().getSimpleName()
                        .equals("AutoRenewalRule")));

        assertTrue(rules.stream()
                .anyMatch(rule -> rule.getClass().getSimpleName()
                        .equals("LongTermContractRule")));

        assertTrue(rules.stream()
                .anyMatch(rule -> rule.getClass().getSimpleName()
                        .equals("ForeignGoverningRule")));

        assertTrue(rules.stream()
                .anyMatch(rule -> rule.getClass().getSimpleName()
                        .equals("UnlimitedLiabilityRule")));
    }

    @Test
    void riskAssessmentService_shouldApplyAllRiskRules() {

        Contract contract = new Contract();

        contract.setContractValue(new BigDecimal("150000"));
        contract.setAutoRenewal(true);
        contract.setUnlimitedLiability(true);
        contract.setStartDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2026, 1, 2));
        contract.setGoverningLaw("Swiss Law");

        RiskAssessment assessment =
                riskAssessmentService.assess(contract);

        assertEquals(100, assessment.getScore());

        assertEquals(RiskLevel.HIGH, assessment.getRiskLevel());

        assertEquals(5, assessment.getFactors().size());
    }
}
