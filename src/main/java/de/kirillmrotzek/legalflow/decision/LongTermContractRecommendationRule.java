package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LongTermContractRecommendationRule implements RecommendationRule {

    @Override
    public Optional<RecommendationResult> evaluate(RiskAssessment assessment) {

        Optional<RiskFactor> factor = assessment.getFactors()
                .stream()
                .filter(f -> "LONG_TERM_CONTRACT".equals(f.getCode()))
                .findFirst();

        return factor.map(f -> new RecommendationResult(
                "Legal review required",
                f.getReason(),
                List.of(ApprovalRole.LEGAL),
                DecisionPriority.MEDIUM,
                "Review contract termination and exit provisions"));
    }
}
