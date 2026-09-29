package de.kirillmrotzek.legalflow.decision;

import de.kirillmrotzek.legalflow.enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LegalReviewDecision {

    private final ReviewType reviewType;
    private final String reason;
}
