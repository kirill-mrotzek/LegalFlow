package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ChangeReviewStatusRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.exception.ErrorResponse;
import de.kirillmrotzek.legalflow.exception.ValidationErrorResponse;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ReviewWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Review Workflow",
        description = "API for managing contract review workflow"
)
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewWorkflowService reviewWorkflowService;
    private final ContractMapper contractMapper;

    @PatchMapping("/{id}/review-status")
    @Operation(
            summary = "Change contract review status",
            description =
                    "Changes the review workflow status of a contract. " +
                            "The requested transition must be allowed by the review workflow rules.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract review status changed successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid review status transition or request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            oneOf = {
                                                    ErrorResponse.class,
                                                    ValidationErrorResponse.class
                                            }
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> changeReviewStatus(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id,
            @Valid @RequestBody ChangeReviewStatusRequest request) {

        Contract updatedContract =
                reviewWorkflowService.changeStatus(
                        id,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }

    @PostMapping("/{id}/review/start")
    @Operation(
            summary = "Start contract review",
            description =
                    "Starts the legal review of a contract. " +
                            "The review must currently be in PENDING status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract review started successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid review status transition",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> startReview(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {

        Contract updatedContract =
                reviewWorkflowService.startReview(id);

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }

    @PostMapping("/{id}/review/approve")
    @Operation(
            summary = "Approve contract review",
            description =
                    "Approves the legal review of a contract. " +
                            "The review must currently be in IN_REVIEW status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract review approved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid review status transition",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> approveReview(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {

        Contract updatedContract =
                reviewWorkflowService.approveReview(id);

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }

    @PostMapping("/{id}/review/reject")
    @Operation(
            summary = "Reject contract review",
            description =
                    "Rejects the legal review of a contract. " +
                            "The review must currently be in IN_REVIEW status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract review rejected successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid review status transition",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> rejectReview(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {

        Contract updatedContract =
                reviewWorkflowService.rejectReview(id);

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }
}
