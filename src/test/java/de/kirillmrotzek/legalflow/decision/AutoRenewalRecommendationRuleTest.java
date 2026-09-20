package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoRenewalRecommendationRuleTest {

    @Test
    void evaluate_shouldReturnRecommendation_whenAutoRenewalExists() {

        RiskFactor factor = new RiskFactor(
                "AUTO_RENEWAL",
                30,
                "Contract contains auto renewal"
        );

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.MEDIUM,
                List.of(factor)
        );

        AutoRenewalRecommendationRule rule =
                new AutoRenewalRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isPresent());

        RecommendationResult recommendation = result.get();

        assertEquals(
                "Legal review required",
                recommendation.getRecommendation()
        );

        assertEquals(
                "Contract contains auto renewal",
                recommendation.getRationale()
        );

        assertEquals(
                List.of(ApprovalRole.LEGAL),
                recommendation.getApprovalRoles()
        );

        assertEquals(
                DecisionPriority.MEDIUM,
                recommendation.getPriority()
        );

        assertEquals(
                "Review notice period and renewal conditions",
                recommendation.getNextAction()
        );
    }

    @Test
    void evaluate_shouldReturnEmpty_whenAutoRenewalDoesNotExist(){

        RiskFactor factor = new RiskFactor(
                "UNLIMITED_LIABILITY",
                30,
                "Contract contains unlimited liability"
        );

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.MEDIUM,
                List.of(factor)
        );

        AutoRenewalRecommendationRule rule =
                new AutoRenewalRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isEmpty());
    }
}
