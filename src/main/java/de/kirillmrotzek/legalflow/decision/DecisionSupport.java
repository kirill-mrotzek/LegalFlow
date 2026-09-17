package de.kirillmrotzek.legalflow.decision;

import lombok.Getter;

import java.util.List;

@Getter
public class DecisionSupport {

    private final String recommendation;

    private final String rationale;

    private final List<ApprovalRole> requiredApprovals;

    private final DecisionPriority priority;

    private final String nextAction;

    public DecisionSupport(
            String recommendation,
            String rationale,
            List<ApprovalRole> requiredApprovals,
            DecisionPriority priority,
            String nextAction) {
        this.recommendation = recommendation;
        this.rationale = rationale;
        this.requiredApprovals = List.copyOf(requiredApprovals);
        this.priority = priority;
        this.nextAction = nextAction;
    }
}
