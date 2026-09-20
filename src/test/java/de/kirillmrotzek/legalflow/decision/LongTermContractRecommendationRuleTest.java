package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LongTermContractRecommendationRuleTest {

    @Test
    void evaluate_shouldReturnRecommendation_whenLongTermContractExists() {

        RiskFactor factor = new RiskFactor(
                "LONG_TERM_CONTRACT",
                30,
                "Long term Contract"
        );

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.MEDIUM,
                List.of(factor)
        );

        LongTermContractRecommendationRule rule =
                new LongTermContractRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isPresent());

        RecommendationResult recommendation = result.get();

        assertEquals(
                "Legal review required",
                recommendation.getRecommendation()
        );

        assertEquals(
                "Long term Contract",
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
                "Review contract termination and exit provisions",
                recommendation.getNextAction()
        );
    }

    @Test
    void evaluate_shouldReturnEmpty_whenLongTermContractDoesNotExist(){

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

        LongTermContractRecommendationRule rule =
                new LongTermContractRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isEmpty());
    }
}
