package de.kirillmrotzek.legalflow.exception;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;

public class InvalidReviewStatusTransitionException extends RuntimeException {

    public InvalidReviewStatusTransitionException(
            ReviewStatus currentStatus,
            ReviewStatus targetStatus) {

        super("Invalid review status transition: "
                + currentStatus + " -> " + targetStatus);
    }
}