package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.ApprovalRole;
import de.kirillmrotzek.legalflow.decision.DecisionPriority;
import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionSupportService {

    public DecisionSupport generate(RiskAssessment assessment) {

        RiskLevel riskLevel = assessment.getRiskLevel();
        return switch (riskLevel) {

            case LOW -> new DecisionSupport(
                    "Legal review required",
                    "Based on risk assessment",
                    List.of(ApprovalRole.LEGAL),
                    DecisionPriority.LOW,
                    "Route contract for Legal review"
            );
            case MEDIUM -> new DecisionSupport(
                    "Legal, Finance review required",
                    "Based on risk assessment",
                    List.of(
                            ApprovalRole.LEGAL,
                            ApprovalRole.FINANCE
                    ),
                    DecisionPriority.MEDIUM,
                    "Route contract for Legal and Finance review"
            );
            case HIGH -> new DecisionSupport(
                    "Management, Legal, Finance, Compliance review required",
                    "Based on risk assessment",
                    List.of(
                            ApprovalRole.LEGAL,
                            ApprovalRole.FINANCE,
                            ApprovalRole.COMPLIANCE,
                            ApprovalRole.MANAGEMENT
                    ),
                    DecisionPriority.HIGH,
                    "Route contract for cross-functional review"
            );

        };
    }
}
