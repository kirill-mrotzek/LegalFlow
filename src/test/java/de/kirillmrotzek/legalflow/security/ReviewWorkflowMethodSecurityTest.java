package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.service.ReviewWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ReviewWorkflowMethodSecurityTest {

    @Autowired
    private ReviewWorkflowService reviewWorkflowService;

    @Test
    @WithMockUser(authorities = "REVIEW_START")
    void userWithoutReviewApproveAuthority_shouldNotApproveReview() {
        assertThatThrownBy(() ->
                reviewWorkflowService.approveReview(999L)
        ).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "REVIEW_APPROVE")
    void userWithoutReviewStartAuthority_shouldNotStartReview() {
        assertThatThrownBy(() ->
                reviewWorkflowService.startReview(999L)
        ).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "REVIEW_START")
    void userWithoutReviewRejectAuthority_shouldNotRejectReview() {
        assertThatThrownBy(() ->
                reviewWorkflowService.rejectReview(999L)
        ).isInstanceOf(AccessDeniedException.class);
    }
}
