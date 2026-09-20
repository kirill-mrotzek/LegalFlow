package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ForeignGoverningLawRecommendationRuleTest {

    @Test
    void evaluate_shouldReturnRecommendation_whenForeignGoverningLawExists() {

        RiskFactor factor = new RiskFactor(
                "FOREIGN_GOVERNING_LAW",
                30,
                "Contract is governed by non-EU law"
        );

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.MEDIUM,
                List.of(factor)
        );

        ForeignGoverningLawRecommendationRule rule =
                new ForeignGoverningLawRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isPresent());

        RecommendationResult recommendation = result.get();

        assertEquals(
                "Legal review required",
                recommendation.getRecommendation()
        );

        assertEquals(
                "Contract is governed by non-EU law",
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
                "Review applicable foreign law and jurisdiction provisions",
                recommendation.getNextAction()
        );
    }

    @Test
    void evaluate_shouldReturnEmpty_whenForeignGoverningLawDoesNotExist(){

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

        ForeignGoverningLawRecommendationRule rule =
                new ForeignGoverningLawRecommendationRule();

        Optional<RecommendationResult> result = rule.evaluate(assessment);

        assertTrue(result.isEmpty());
    }
}
