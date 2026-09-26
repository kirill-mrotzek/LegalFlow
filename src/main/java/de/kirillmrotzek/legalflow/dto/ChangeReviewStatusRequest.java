package de.kirillmrotzek.legalflow.dto;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeReviewStatusRequest {

    @NotNull
    private ReviewStatus status;
}
