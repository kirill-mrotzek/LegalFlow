package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HighContractValueRecommendationRule implements RecommendationRule {

    @Override
    public Optional<RecommendationResult> evaluate(RiskAssessment assessment) {

        Optional<RiskFactor> factor = assessment.getFactors()
                .stream()
                .filter(f -> "HIGH_CONTRACT_VALUE".equals(f.getCode()))
                .findFirst();

        return factor.map(f -> new RecommendationResult(
                "Finance review required",
                f.getReason(),
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.MANAGEMENT
                ),
                DecisionPriority.MEDIUM,
                "Route contract for Legal, Finance, Management approval"));
    }
}
