package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HighContractValueRecommendationRuleTest {

    @Test
    void evaluate_shouldReturnRecommendation_whenHighContractValueExists(){

        RiskFactor factor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds € 100.000"
        );

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.MEDIUM,
                List.of(factor)
        );

        HighContractValueRecommendationRule rule =
                new HighContractValueRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isPresent());

        RecommendationResult recommendation = result.get();

        assertEquals(
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.MANAGEMENT
                ),
                recommendation.getApprovalRoles()
        );

        assertEquals(
                "Finance review required",
                recommendation.getRecommendation()
        );

        assertEquals(
                "Contract value exceeds € 100.000",
                recommendation.getRationale()
        );

        assertEquals(
                DecisionPriority.MEDIUM,
                recommendation.getPriority()
        );

        assertEquals(
                "Route contract for Legal, Finance, Management approval",
                recommendation.getNextAction()
        );
    }

    @Test
    void evaluate_shouldReturnEmpty_whenHighContractValueDoesNotExist(){

        RiskFactor factor = new RiskFactor(
                "UNLIMITED_LIABILITY",
                25,
                "Contract contains unlimited liability"
        );

        RiskAssessment assessment = new RiskAssessment(
                25,
                RiskLevel.HIGH,
                List.of(factor)
        );

        HighContractValueRecommendationRule rule =
                new HighContractValueRecommendationRule();


        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isEmpty());
    }


}
