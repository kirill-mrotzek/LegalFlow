package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.LegalReviewDecision;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalReviewDecisionService {

    public LegalReviewDecision decide(RiskAssessment assessment) {

        List<String> enhancedReasons = assessment.getFactors()
                .stream()
                .filter(f ->
                        "FOREIGN_GOVERNING_LAW_NON_EU".equals(f.getCode())
                                || "UNLIMITED_LIABILITY".equals(f.getCode())
                )
                .map(RiskFactor::getReason)
                .toList();

        String reason = String.join("; ", enhancedReasons);

        if (enhancedReasons.isEmpty()) {
            return new LegalReviewDecision(
                    ReviewType.STANDARD,
                    "Standard legal review required; no specific enhanced legal review factors detected."
            );
        }

        return new LegalReviewDecision(
                ReviewType.ENHANCED,
                reason
        );
    }
}
