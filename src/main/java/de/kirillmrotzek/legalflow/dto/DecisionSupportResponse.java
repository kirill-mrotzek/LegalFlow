package de.kirillmrotzek.legalflow.dto;

import de.kirillmrotzek.legalflow.decision.ApprovalRole;
import de.kirillmrotzek.legalflow.decision.DecisionPriority;
import lombok.Data;

import java.util.List;

@Data
public class DecisionSupportResponse {

    private String recommendation;

    private String rationale;

    private List<ApprovalRole> requiredApprovals;

    private DecisionPriority priority;

    private String nextAction;
}
