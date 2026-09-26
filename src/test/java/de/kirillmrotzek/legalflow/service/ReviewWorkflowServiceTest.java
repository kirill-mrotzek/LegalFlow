package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidReviewStatusTransitionException;
import de.kirillmrotzek.legalflow.lifecycle.ReviewStatusTransitionValidator;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewWorkflowServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ReviewStatusTransitionValidator transitionValidator;

    private ReviewWorkflowService reviewWorkflowService;

    @BeforeEach
    void setUp() {
        reviewWorkflowService = new ReviewWorkflowService(
                contractRepository,
                transitionValidator
        );
    }

    @Test
    void shouldChangeStatusFromPendingToInReview() {

        Contract contract = new Contract();
        contract.setReviewStatus(ReviewStatus.PENDING);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ReviewStatus.PENDING,
                ReviewStatus.IN_REVIEW
        )).thenReturn(true);

        when(contractRepository.save(contract))
                .thenReturn(contract);

        Contract result = reviewWorkflowService.changeStatus(
                1L,
                ReviewStatus.IN_REVIEW
        );

        assertEquals(
                ReviewStatus.IN_REVIEW,
                result.getReviewStatus()
        );

        verify(contractRepository).save(contract);
        verify(transitionValidator).isAllowed(
                ReviewStatus.PENDING,
                ReviewStatus.IN_REVIEW
        );
    }

    @Test
    void shouldChangeStatusFromInReviewToApproved() {

        Contract contract = new Contract();
        contract.setReviewStatus(ReviewStatus.IN_REVIEW);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ReviewStatus.IN_REVIEW,
                ReviewStatus.APPROVED
        )).thenReturn(true);

        when(contractRepository.save(contract))
                .thenReturn(contract);

        Contract result = reviewWorkflowService.changeStatus(
                1L,
                ReviewStatus.APPROVED
        );

        assertEquals(
                ReviewStatus.APPROVED,
                result.getReviewStatus()
        );

        verify(contractRepository).save(contract);
        verify(transitionValidator).isAllowed(
                ReviewStatus.IN_REVIEW,
                ReviewStatus.APPROVED
        );
    }

    @Test
    void shouldChangeStatusFromInReviewToRejected() {

        Contract contract = new Contract();
        contract.setReviewStatus(ReviewStatus.IN_REVIEW);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ReviewStatus.IN_REVIEW,
                ReviewStatus.REJECTED
        )).thenReturn(true);

        when(contractRepository.save(contract))
                .thenReturn(contract);

        Contract result = reviewWorkflowService.changeStatus(
                1L,
                ReviewStatus.REJECTED
        );

        assertEquals(
                ReviewStatus.REJECTED,
                result.getReviewStatus()
        );

        verify(contractRepository).save(contract);
        verify(transitionValidator).isAllowed(
                ReviewStatus.IN_REVIEW,
                ReviewStatus.REJECTED
        );
    }

    @Test
    void shouldRejectPendingToApproved() {

        Contract contract = new Contract();
        contract.setReviewStatus(ReviewStatus.PENDING);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(transitionValidator.isAllowed(
                ReviewStatus.PENDING,
                ReviewStatus.APPROVED
        )).thenReturn(false);

        assertThrows(
                InvalidReviewStatusTransitionException.class,
                () -> reviewWorkflowService.changeStatus(
                        1L,
                        ReviewStatus.APPROVED
                )
        );
        verify(contractRepository, never()).save(contract);
    }

    @Test
    void shouldThrowContractNotFoundExceptionWhenContractDoesNotExist() {

        when(contractRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContractNotFoundException.class,
                () -> reviewWorkflowService.changeStatus(
                        1L,
                        ReviewStatus.PENDING
                )
        );
        verify(transitionValidator, never()).isAllowed(
                any(ReviewStatus.class),
                any(ReviewStatus.class)
        );
    }
}
