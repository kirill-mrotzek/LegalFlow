package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ChangeContractStatusRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.exception.ErrorResponse;
import de.kirillmrotzek.legalflow.exception.ValidationErrorResponse;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ContractLifecycleService;
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
        name = "Contract Lifecycle",
        description = "API for managing contract lifecycle status"
)
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractLifecycleController {

    private final ContractLifecycleService contractLifecycleService;
    private final ContractMapper contractMapper;

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Change contract status",
            description =
                    "Changes the lifecycle status of a contract. " +
                            "The requested transition must be allowed by the contract lifecycle rules.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract status changed successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid status transition or request",
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
    public ResponseEntity<ContractResponse> changeContractStatus(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id,
            @Valid @RequestBody ChangeContractStatusRequest request) {

        Contract updatedContract =
                contractLifecycleService.changeStatus(
                        id,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }
}
