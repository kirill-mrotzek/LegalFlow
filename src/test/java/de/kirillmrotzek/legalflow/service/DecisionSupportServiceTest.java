package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.ApprovalRole;
import de.kirillmrotzek.legalflow.decision.DecisionPriority;
import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionSupportServiceTest {

    @Test
    void shouldGenerateHighRiskDecisionSupport() {

        RiskAssessment assessment = new RiskAssessment(
                100,
                RiskLevel.HIGH,
                List.of()
        );

        DecisionSupportService service = new DecisionSupportService();
        DecisionSupport decisionSupport = service.generate(assessment);

        assertEquals(
                "Management, Legal, Finance, Compliance review required",
                decisionSupport.getRecommendation()
        );
        assertEquals(
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.COMPLIANCE,
                        ApprovalRole.MANAGEMENT
                ),
                decisionSupport.getRequiredApprovals()
        );
        assertEquals(
                DecisionPriority.HIGH,
                decisionSupport.getPriority()
        );
        assertEquals(
                "Route contract for cross-functional review",
                decisionSupport.getNextAction()
        );

    }

    @Test
    void shouldGenerateMediumRiskDecisionSupport(){

        RiskAssessment assessment = new RiskAssessment(
                40,
                RiskLevel.MEDIUM,
                List.of()
        );

        DecisionSupportService service = new DecisionSupportService();
        DecisionSupport decisionSupport = service.generate(assessment);

        assertEquals(
                "Legal, Finance review required",
                decisionSupport.getRecommendation()
        );
        assertEquals(
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE
                ),
                decisionSupport.getRequiredApprovals()
        );
        assertEquals(
                DecisionPriority.MEDIUM,
                decisionSupport.getPriority()
        );
        assertEquals(
                "Route contract for Legal and Finance review",
                decisionSupport.getNextAction()
        );
    }

    @Test
    void shouldGenerateLowRiskDecisionSupport(){

        RiskAssessment assessment = new RiskAssessment(
                10,
                RiskLevel.LOW,
                List.of()
        );

        DecisionSupportService service = new DecisionSupportService();
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
    }

}
