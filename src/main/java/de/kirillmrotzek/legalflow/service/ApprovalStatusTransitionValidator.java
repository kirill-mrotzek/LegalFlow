package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ApprovalStatus;
import org.springframework.stereotype.Component;

@Component
public class ApprovalStatusTransitionValidator {

    public boolean isAllowed(
            ApprovalStatus currentStatus,
            ApprovalStatus targetStatus) {

        return switch (currentStatus) {
            case PENDING ->
                    targetStatus == ApprovalStatus.APPROVED
                            || targetStatus == ApprovalStatus.REJECTED;

            case APPROVED, REJECTED ->
                    false;
        };
    }
}
