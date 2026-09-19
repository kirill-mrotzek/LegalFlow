package de.kirillmrotzek.legalflow.decision;

import lombok.Getter;

import java.util.List;

@Getter
public class RecommendationResult {

    private final String recommendation;
    private final String rationale;
    private final List<ApprovalRole> approvalRoles;
    private final DecisionPriority priority;
    private final String nextAction;

    public RecommendationResult(
            String recommendation,
            String rationale,
            List<ApprovalRole> approvalRoles,
            DecisionPriority priority,
            String nextAction) {

        this.recommendation = recommendation;
        this.rationale = rationale;
        this.approvalRoles = List.copyOf(approvalRoles);
        this.priority = priority;
        this.nextAction = nextAction;
    }
}
