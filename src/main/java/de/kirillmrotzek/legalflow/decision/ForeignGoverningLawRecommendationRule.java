package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ForeignGoverningLawRecommendationRule implements RecommendationRule {

    @Override
    public Optional<RecommendationResult> evaluate(RiskAssessment assessment) {

        Optional<RiskFactor> factor = assessment.getFactors()
                .stream()
                .filter(f -> "FOREIGN_GOVERNING_LAW".equals(f.getCode()))
                .findFirst();

        return factor.map(f -> new RecommendationResult(
                "Legal review required",
                f.getReason(),
                List.of(ApprovalRole.LEGAL),
                DecisionPriority.MEDIUM,
                "Review applicable foreign law and jurisdiction provisions"));
    }
}
