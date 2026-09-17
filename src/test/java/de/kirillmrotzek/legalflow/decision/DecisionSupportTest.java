package de.kirillmrotzek.legalflow.decision;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionSupportTest {

    @Test
    void shouldCreateDecisionSupport() {

        DecisionSupport decisionSupport = new DecisionSupport(
                "Legal review required",
                "Contract contains unlimited liability.",
                List.of(ApprovalRole.LEGAL),
                DecisionPriority.HIGH,
                "Assign contract to Legal for review"
        );

        assertEquals(
                "Legal review required",
                decisionSupport.getRecommendation()
        );
        assertEquals(
                "Contract contains unlimited liability.",
                decisionSupport.getRationale()
        );
        assertEquals(
                List.of(ApprovalRole.LEGAL),
                decisionSupport.getRequiredApprovals()
        );
        assertEquals(
                DecisionPriority.HIGH,
                decisionSupport.getPriority()
        );
        assertEquals(
                "Assign contract to Legal for review",
                decisionSupport.getNextAction()
        );
    }

    @Test
    void shouldProtectRequiredApprovalsFromExternalModification(){

        List<ApprovalRole> approvals = new ArrayList<>();
        approvals.add(ApprovalRole.LEGAL);

        DecisionSupport decisionSupport = new DecisionSupport(
                "Legal review required",
                "Contract contains unlimited liability.",
                approvals,
                DecisionPriority.HIGH,
                "Assign contract to Legal for review"
        );

        approvals.add(ApprovalRole.FINANCE);

        assertEquals(
                List.of(ApprovalRole.LEGAL),
                decisionSupport.getRequiredApprovals()
        );
    }

    @Test
    void shouldReturnUnmodifiableRequiredApprovals(){

        List<ApprovalRole> approvals = new ArrayList<>();
        approvals.add(ApprovalRole.LEGAL);

        DecisionSupport decisionSupport = new DecisionSupport(
                "Legal review required",
                "Contract contains unlimited liability.",
                approvals,
                DecisionPriority.HIGH,
                "Assign contract to Legal for review"
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> decisionSupport.getRequiredApprovals().add(ApprovalRole.FINANCE)
        );


    }
}
