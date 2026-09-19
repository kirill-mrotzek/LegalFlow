package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.risk.RiskAssessment;

import java.util.Optional;

public interface RecommendationRule {

    Optional<RecommendationResult> evaluate(RiskAssessment assessment);
}
