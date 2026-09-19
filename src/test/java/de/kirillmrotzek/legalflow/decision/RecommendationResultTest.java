package de.kirillmrotzek.legalflow.decision;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecommendationResultTest {

    @Test
    void shouldProtectApprovalRolesFromSourceListMutation() {

        List<ApprovalRole> approvalRoles = new ArrayList<>(
                List.of(ApprovalRole.LEGAL)
        );

        RecommendationResult result = new RecommendationResult(
                "Test recommendation",
                "Test rationale",
                approvalRoles,
                DecisionPriority.MEDIUM,
                "Test action"
        );

        approvalRoles.add(ApprovalRole.FINANCE);

        assertEquals(
                List.of(ApprovalRole.LEGAL),
                result.getApprovalRoles()
);
    }

    @Test
    void shouldReturnUnmodifiableApprovalRoles() {

        List<ApprovalRole> approvalRoles = new ArrayList<>(
                List.of(ApprovalRole.LEGAL)
        );

        RecommendationResult result = new RecommendationResult(
                "Test recommendation",
                "Test rationale",
                approvalRoles,
                DecisionPriority.MEDIUM,
                "Test action"
        );

        List<ApprovalRole> roles = result.getApprovalRoles();

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    roles.add(ApprovalRole.FINANCE);
                }
        );
    }
}
