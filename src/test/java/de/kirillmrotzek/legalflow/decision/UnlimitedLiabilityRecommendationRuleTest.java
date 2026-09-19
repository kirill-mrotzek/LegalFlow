package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnlimitedLiabilityRecommendationRuleTest {

    @Test
    void evaluate_shouldReturnRecommendation_whenUnlimitedLiabilityExists() {

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

        UnlimitedLiabilityRecommendationRule rule =
                new UnlimitedLiabilityRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isPresent());

        RecommendationResult recommendation = result.get();

        assertEquals(
                "Legal review required",
                recommendation.getRecommendation()
        );

        assertEquals(
                "Contract contains unlimited liability",
                recommendation.getRationale()
        );

        assertEquals(
                List.of(ApprovalRole.LEGAL),
                recommendation.getApprovalRoles()
        );

        assertEquals(
                DecisionPriority.HIGH,
                recommendation.getPriority()
        );

        assertEquals(
                "Assign contract to Legal for review",
                recommendation.getNextAction()
        );
    }

    @Test
    void evaluate_shouldReturnEmpty_whenUnlimitedLiabilityDoesNotExist(){

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

        UnlimitedLiabilityRecommendationRule rule =
                new UnlimitedLiabilityRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isEmpty());

    }
}
