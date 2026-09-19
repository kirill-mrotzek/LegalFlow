package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.*;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DecisionSupportService {

    private final List<RecommendationRule> recommendationRules;

    public DecisionSupportService(List<RecommendationRule> recommendationRules) {
        this.recommendationRules = recommendationRules;
    }

    public DecisionSupport generate(RiskAssessment assessment) {

        List<RecommendationResult> recommendations =
                recommendationRules.stream()
                        .flatMap(rule -> rule.evaluate(assessment).stream())
                        .toList();

        List<ApprovalRole> approvalRoles =
                recommendations.stream()
                        .flatMap(recommendationResult -> recommendationResult.getApprovalRoles().stream())
                        .distinct()
                        .toList();


        Optional<DecisionPriority> maxPriority =
                recommendations.stream()
                        .map(recommendationResult -> recommendationResult.getPriority())
                        .max(Comparator.comparing(Enum::ordinal));

        DecisionPriority priority =  maxPriority.orElse(DecisionPriority.LOW);

        List<String> recommendationTexts =
                recommendations.stream()
                        .map(recommendationResult -> recommendationResult.getRecommendation())
                        .toList();

        String recommendation =
                recommendationTexts.stream()
                        .collect(Collectors.joining("; "));

        List<String> rationaleTexts =
                recommendations.stream()
                        .map(recommendationResult -> recommendationResult.getRationale())
                        .toList();

        String rationale =
                rationaleTexts.stream()
                        .collect(Collectors.joining("; "));

        List<String> nextActionTexts =
                recommendations.stream()
                        .map(recommendationResult -> recommendationResult.getNextAction())
                        .toList();

        String nextAction =
                nextActionTexts.stream()
                        .collect(Collectors.joining("; "));

        if (recommendations.isEmpty()) {
            return new DecisionSupport(
                    "Legal review required",
                    "No specific recommendation rules were triggered",
                    List.of(ApprovalRole.LEGAL),
                    DecisionPriority.LOW,
                    "Route contract for Legal review"
            );
        }

        return new DecisionSupport(
                recommendation,
                rationale,
                approvalRoles,
                priority,
                nextAction
        );
    }
}
