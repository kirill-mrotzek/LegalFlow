package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.ApprovalRole;
import de.kirillmrotzek.legalflow.enums.DecisionPriority;
import de.kirillmrotzek.legalflow.enums.RiskFactorCode;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UnlimitedLiabilityRecommendationRule implements RecommendationRule {

    @Override
    public Optional<RecommendationResult> evaluate(RiskAssessment assessment) {

        Optional<RiskFactor> factor = assessment.getFactors()
                .stream()
                .filter(f ->
                        f.getRiskFactorCode() == RiskFactorCode.UNLIMITED_LIABILITY
                )
                .findFirst();

        return factor.map(f -> new RecommendationResult(
                "Legal review required",
                f.getReason(),
                List.of(ApprovalRole.LEGAL),
                DecisionPriority.HIGH,
                "Assign contract to Legal for review"));
    }
}
