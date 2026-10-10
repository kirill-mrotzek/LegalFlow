package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ApprovalStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApprovalStatusTransitionValidatorTest {

    private final ApprovalStatusTransitionValidator validator =
            new ApprovalStatusTransitionValidator();

    @Test
    void pendingToApproved_shouldBeAllowed() {
        assertThat(
                validator.isAllowed(
                        ApprovalStatus.PENDING,
                        ApprovalStatus.APPROVED
                )
        ).isTrue();
    }

    @Test
    void pendingToRejected_shouldBeAllowed() {
        assertThat(
                validator.isAllowed(
                        ApprovalStatus.PENDING,
                        ApprovalStatus.REJECTED
                )
        ).isTrue();
    }

    @Test
    void approvedToRejected_shouldNotBeAllowed() {
        assertThat(
                validator.isAllowed(
                        ApprovalStatus.APPROVED,
                        ApprovalStatus.REJECTED
                )
        ).isFalse();
    }

    @Test
    void rejectedToApproved_shouldNotBeAllowed() {
        assertThat(
                validator.isAllowed(
                        ApprovalStatus.REJECTED,
                        ApprovalStatus.APPROVED
                )
        ).isFalse();
    }
}
