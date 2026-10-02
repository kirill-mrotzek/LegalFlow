package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidReviewStatusTransitionException;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ReviewWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewWorkflowService reviewWorkflowService;

    @MockitoBean
    private ContractMapper contractMapper;

    @Test
    void changeReviewStatus_shouldReturnBadRequestForInvalidTransition()
            throws Exception {

        when(reviewWorkflowService.changeStatus(
                eq(1L),
                eq(ReviewStatus.APPROVED)
        )).thenThrow(
                new InvalidReviewStatusTransitionException(
                        ReviewStatus.PENDING,
                        ReviewStatus.APPROVED
                )
        );

        mockMvc.perform(
                        patch("/contracts/1/review-status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "status": "APPROVED"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid review status transition: PENDING -> APPROVED"
                                )
                );

        verify(reviewWorkflowService)
                .changeStatus(1L, ReviewStatus.APPROVED);
    }

    @Test
    void changeReviewStatus_shouldReturnNotFoundWhenContractDoesNotExist()
            throws Exception {

        when(reviewWorkflowService.changeStatus(
                eq(999L),
                eq(ReviewStatus.IN_REVIEW)
        )).thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(
                        patch("/contracts/999/review-status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                        {
                            "status": "IN_REVIEW"
                        }
                        """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Contract with id 999 not found")
                );

        verify(reviewWorkflowService)
                .changeStatus(999L, ReviewStatus.IN_REVIEW);
    }

    @Test
    void rejectReview_shouldReturnNotFoundWhenContractDoesNotExist()
            throws Exception {

        when(reviewWorkflowService.rejectReview(999L))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(
                        post("/contracts/999/review/reject")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Contract with id 999 not found")
                );

        verify(reviewWorkflowService)
                .rejectReview(999L);
    }

    @Test
    void changeReviewStatus_shouldChangeStatusSuccessfully() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setReviewStatus(ReviewStatus.IN_REVIEW);

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setReviewStatus(ReviewStatus.IN_REVIEW);

        when(reviewWorkflowService.changeStatus(
                eq(1L),
                eq(ReviewStatus.IN_REVIEW)
        )).thenReturn(contract);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/contracts/1/review-status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "status": "IN_REVIEW"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reviewStatus")
                        .value("IN_REVIEW"));

        verify(reviewWorkflowService)
                .changeStatus(1L, ReviewStatus.IN_REVIEW);

        verify(contractMapper)
                .toResponse(contract);
    }
}
