package de.kirillmrotzek.legalflow.lifecycle;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReviewStatusTransitionValidatorTest {

    private  ReviewStatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ReviewStatusTransitionValidator();
    }

    @Test
    void shouldAllowPendingToInReview() {
        assertTrue(
                validator.isAllowed(
                        ReviewStatus.PENDING,
                        ReviewStatus.IN_REVIEW
                )
        );
    }

    @Test
    void shouldAllowInReviewToApproved() {
        assertTrue(
                validator.isAllowed(
                        ReviewStatus.IN_REVIEW,
                        ReviewStatus.APPROVED
                )
        );
    }

    @Test
    void shouldAllowInReviewToRejected() {
        assertTrue(
                validator.isAllowed(
                        ReviewStatus.IN_REVIEW,
                        ReviewStatus.REJECTED
                )
        );
    }

    @Test
    void shouldRejectPendingToApproved() {
        assertFalse(
                validator.isAllowed(
                        ReviewStatus.PENDING,
                        ReviewStatus.APPROVED
                )
        );
    }

    @Test
    void shouldRejectPendingToRejected() {
        assertFalse(
                validator.isAllowed(
                        ReviewStatus.PENDING,
                        ReviewStatus.REJECTED
                )
        );
    }

    @Test
    void shouldRejectInReviewToPending() {
        assertFalse(
                validator.isAllowed(
                        ReviewStatus.IN_REVIEW,
                        ReviewStatus.PENDING
                )
        );
    }

    @Test
    void shouldRejectApprovedToInReview() {
        assertFalse(
                validator.isAllowed(
                        ReviewStatus.APPROVED,
                        ReviewStatus.IN_REVIEW
                )
        );
    }

    @Test
    void shouldRejectRejectedToInReview() {
        assertFalse(
                validator.isAllowed(
                        ReviewStatus.REJECTED,
                        ReviewStatus.IN_REVIEW
                )
        );
    }

    @Test
    void shouldRejectNotRequiredToInReview() {
        assertFalse(
                validator.isAllowed(
                        ReviewStatus.NOT_REQUIRED,
                        ReviewStatus.IN_REVIEW
                )
        );
    }
}