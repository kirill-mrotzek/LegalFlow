package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.*;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionSupportServiceTest {

    @Test
    void shouldGenerateDecisionSupportForUnlimitedLiability() {

        RiskFactor factor = new RiskFactor(
                "UNLIMITED_LIABILITY",
                25,
                "Contract contains unlimited liability",
                "Unlimited liability increases potential financial exposure"
        );

        RiskAssessment assessment = new RiskAssessment(
                25,
                RiskLevel.HIGH,
                List.of(factor)
        );

        RecommendationRule rule =
                new UnlimitedLiabilityRecommendationRule();

        DecisionSupportService service =
                new DecisionSupportService(List.of(rule));

        DecisionSupport decisionSupport =
                service.generate(assessment);

        assertEquals(
                "Legal review required",
                decisionSupport.getRecommendation()
        );

        assertEquals(
                "Contract contains unlimited liability",
                decisionSupport.getRationale()
        );

        assertEquals(
                List.of(ApprovalRole.LEGAL),
                decisionSupport.getRequiredApprovals()
        );

        assertEquals(
                DecisionPriority.HIGH,
                decisionSupport.getPriority()
        );

        assertEquals(
                "Assign contract to Legal for review",
                decisionSupport.getNextAction()
        );

    }

    @Test
    void shouldGenerateFallbackDecisionSupport_whenNoRecommendationRulesTriggered(){

        RiskAssessment assessment = new RiskAssessment(
                10,
                RiskLevel.LOW,
                List.of()
        );

        DecisionSupportService service =
                new DecisionSupportService(List.of());

        DecisionSupport decisionSupport = service.generate(assessment);

        assertEquals(
                "Legal review required",
                decisionSupport.getRecommendation()
        );
        assertEquals(
                List.of(
                        ApprovalRole.LEGAL
                ),
                decisionSupport.getRequiredApprovals()
        );
        assertEquals(
                DecisionPriority.LOW,
                decisionSupport.getPriority()
        );
        assertEquals(
                "Route contract for Legal review",
                decisionSupport.getNextAction()
        );
        assertEquals(
                "No specific recommendation rules were triggered",
                decisionSupport.getRationale()
        );
    }

    @Test
    void shouldAggregateRecommendationsFromMultipleRules() {

        RiskFactor highValueFactor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds € 100.000",
                "High contract value increases potential financial exposure"
        );

        RiskFactor unlimitedLiabilityFactor = new RiskFactor(
                "UNLIMITED_LIABILITY",
                25,
                "Contract contains unlimited liability",
                "Unlimited liability increases potential financial exposure"
        );

        RiskAssessment assessment = new RiskAssessment(
                55,
                RiskLevel.MEDIUM,
                List.of(
                        highValueFactor,
                        unlimitedLiabilityFactor
                )
        );

        List<RecommendationRule> rules = List.of(
                new UnlimitedLiabilityRecommendationRule(),
                new HighContractValueRecommendationRule()
        );

        DecisionSupportService service =
                new DecisionSupportService(rules);

        DecisionSupport decisionSupport =
                service.generate(assessment);

        assertEquals(
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.MANAGEMENT
                ),
                decisionSupport.getRequiredApprovals()
        );

        assertEquals(
                DecisionPriority.HIGH,
                decisionSupport.getPriority()
        );
    }

    @Test
    void shouldAggregateRecommendationsFromAllRiskFactors() {

        RiskFactor highValueFactor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds € 100.000",
                "High contract value increases potential financial exposure"
        );

        RiskFactor unlimitedLiabilityFactor = new RiskFactor(
                "UNLIMITED_LIABILITY",
                25,
                "Contract contains unlimited liability",
                "Unlimited liability increases potential financial exposure"
        );

        RiskFactor longTermFactor = new RiskFactor(
                "LONG_TERM_CONTRACT",
                15,
                "Long term Contract",
                "Long contract terms increase the duration of legal and financial exposure"
        );

        RiskFactor foreignGoverningFactor = new RiskFactor(
                "FOREIGN_GOVERNING_LAW_NON_EU",
                20,
                "Contract is governed by non-EU law",
                "Non-EU governing law increases legal complexity and potential enforcement risk"
        );

        RiskFactor autoRenewalFactor = new RiskFactor(
                "AUTO_RENEWAL",
                10,
                "Contract contains auto renewal",
                "Automatic renewal can extend contractual obligations if termination deadlines are missed"
        );

        RiskAssessment assessment = new RiskAssessment(
                100,
                RiskLevel.HIGH,
                List.of(
                        highValueFactor,
                        unlimitedLiabilityFactor,
                        longTermFactor,
                        foreignGoverningFactor,
                        autoRenewalFactor
                )
        );

        List<RecommendationRule> rules = List.of(
                new UnlimitedLiabilityRecommendationRule(),
                new HighContractValueRecommendationRule(),
                new LongTermContractRecommendationRule(),
                new ForeignGoverningLawRecommendationRule(),
                new AutoRenewalRecommendationRule()
        );

        DecisionSupportService service =
                new DecisionSupportService(rules);

        DecisionSupport decisionSupport =
                service.generate(assessment);

        assertEquals(
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.MANAGEMENT
                ),
                decisionSupport.getRequiredApprovals()
        );

        assertEquals(
                DecisionPriority.HIGH,
                decisionSupport.getPriority()
        );

        assertEquals(
                "Legal review required; " +
                        "Finance review required; " +
                        "Legal review required; " +
                        "Legal review required; " +
                        "Legal review required",
                decisionSupport.getRecommendation()
        );

        assertEquals(
                "Contract contains unlimited liability; " +
                        "Contract value exceeds € 100.000; " +
                        "Long term Contract; " +
                        "Contract is governed by non-EU law; " +
                        "Contract contains auto renewal",
                decisionSupport.getRationale()
        );

        assertEquals(
                "Assign contract to Legal for review; " +
                        "Route contract for Legal, Finance, Management approval; " +
                        "Review contract termination and exit provisions; " +
                        "Review applicable foreign law and jurisdiction provisions; " +
                        "Review notice period and renewal conditions",
                decisionSupport.getNextAction()
        );
    }
}
