package de.kirillmrotzek.legalflow.lifecycle;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class ReviewStatusTransitionValidator {

    private final Map<ReviewStatus, Set<ReviewStatus>> allowedTransitions =
            new EnumMap<>(ReviewStatus.class);

    public ReviewStatusTransitionValidator() {

        allowedTransitions.put(
                ReviewStatus.PENDING,
                EnumSet.of(ReviewStatus.IN_REVIEW)
        );

        allowedTransitions.put(
                ReviewStatus.IN_REVIEW,
                EnumSet.of(
                        ReviewStatus.APPROVED,
                        ReviewStatus.REJECTED
                )
        );
    }

    public boolean isAllowed(
            ReviewStatus currentStatus,
            ReviewStatus targetStatus
    ) {
        return allowedTransitions
                .getOrDefault(currentStatus, Set.of())
                .contains(targetStatus);
    }
}