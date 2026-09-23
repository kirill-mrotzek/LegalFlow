package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.service.ContractLifecycleService;
import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.dto.*;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.ErrorResponse;
import de.kirillmrotzek.legalflow.exception.InvalidDateRangeException;
import de.kirillmrotzek.legalflow.exception.InvalidValueRangeException;
import de.kirillmrotzek.legalflow.exception.ValidationErrorResponse;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.mapper.DecisionSupportMapper;
import de.kirillmrotzek.legalflow.mapper.RiskAssessmentMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.DecisionSupportService;
import de.kirillmrotzek.legalflow.service.RiskAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Tag(
        name = "Contracts",
        description = "API for managing contracts"
)

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;
    private final RiskAssessmentService riskAssessmentService;
    private final RiskAssessmentMapper riskAssessmentMapper;
    private final DecisionSupportService decisionSupportService;
    private final DecisionSupportMapper decisionSupportMapper;
    private final ContractLifecycleService contractLifecycleService;

    @GetMapping
    @Operation(
            summary = "Search contracts",
            description =
                    "Returns a paginated list of contracts with optional filtering by " +
                            "status, type, risk level, counterparty, contract value, and contract dates. " +
                            "If both values are provided, minValue must be less than or equal to maxValue, " +
                            "startDateFrom must be before or equal to startDateTo, and endDateFrom must be before or equal to endDateTo.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contracts found"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ContractPageResponse getAllContracts(
            @Parameter(
                    description = "Filter contracts by contract status"
            )
            @RequestParam(required = false) ContractStatus status,
            @Parameter(
                    description = "Filter contracts by contract type"
            )
            @RequestParam(required = false) ContractType type,
            @Parameter(
                    description = "Filter contracts by risk level"
            )
            @RequestParam(required = false) RiskLevel riskLevel,
            @Parameter(
                    description = "Counterparty name to filter by"
            )
            @RequestParam(required = false) String counterparty,
            @Parameter(
                    description = "Minimum contract value"
            )
            @RequestParam(required = false) BigDecimal minValue,
            @Parameter(
                    description = "Maximum contract value"
            )
            @RequestParam(required = false) BigDecimal maxValue,
            @Parameter(
                    description = "Minimum contract start date (inclusive)"
            )
            @RequestParam(required = false) LocalDate startDateFrom,
            @Parameter(
                    description = "Maximum contract start date (inclusive)"
            )
            @RequestParam(required = false) LocalDate startDateTo,
            @Parameter(
                    description = "Minimum contract end date (inclusive)"
            )
            @RequestParam(required = false) LocalDate endDateFrom,
            @Parameter(
                    description = "Maximum contract end date (inclusive)"
            )
            @RequestParam(required = false) LocalDate endDateTo,
            @ParameterObject
            Pageable pageable) {

        if (startDateFrom != null
                && startDateTo != null
                && startDateFrom.isAfter(startDateTo)) {
            throw new InvalidDateRangeException(
                    "startDateFrom must be before or equal to startDateTo"
            );
        }

        if (endDateFrom != null
                && endDateTo != null
                && endDateFrom.isAfter(endDateTo)) {
            throw new InvalidDateRangeException(
                    "endDateFrom must be before or equal to endDateTo"
            );
        }

        if (minValue != null
                && maxValue != null
                && minValue.compareTo(maxValue) > 0) {

            throw new InvalidValueRangeException(
                    "minValue must be less than or equal to maxValue"
            );
        }

        Page<Contract> contracts = contractService.search(
                status,
                type,
                riskLevel,
                counterparty,
                minValue,
                maxValue,
                startDateFrom,
                startDateTo,
                endDateFrom,
                endDateTo,
                pageable
        );

        Page<ContractResponse> responsePage =
                contracts.map(contractMapper::toResponse);

        return new ContractPageResponse(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get contract by ID",
            description = "Returns a contract identified by its unique ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract found"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> getContractById(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {

        Contract contract = contractService.findById(id);

        return ResponseEntity.ok(
                contractMapper.toResponse(contract)
        );
    }

    @GetMapping("/{id}/risk-assessment")
    @Operation(
            summary = "Get contract risk assessment",
            description = "Returns the risk assessment of a contract, " +
                    "including its risk score, risk level, and contributing risk factors.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Risk assessment calculated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<RiskAssessmentResponse> getRiskAssessment(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id){

        Contract contract = contractService.findById(id);

        RiskAssessment assessment =
                riskAssessmentService.assess(contract);

        RiskAssessmentResponse response =
                riskAssessmentMapper.toResponse(assessment);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a new contract",
            description = "Creates a new contract.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Contract created successfully",
                            headers = @Header(
                                    name = "Location",
                                    description = "URI of the newly created contract"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            oneOf = {
                                                    ErrorResponse.class,
                                                    ValidationErrorResponse.class
                                            }
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract savedContract = contractService.save(contract);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(
                        "Location",
                        "/contracts/" + savedContract.getId()
                )
                .body(contractMapper.toResponse(savedContract));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a contract",
            description = "Updates a contract.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contract updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
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
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<ContractResponse> updateContract(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id,
            @Valid @RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract updatedContract =
                contractService.update(id, contract);

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }

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

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a contract",
            description = "Deletes a contract.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Contract deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteContract(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {

        contractService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/decision-support")
    @Operation(
            summary = "Get decision support",
            description = "Returns decision support for the contract, " +
                    "including the recommendation, rationale, required approvals, " +
                    "priority, and next action.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Decision support generated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Contract not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<DecisionSupportResponse> getDecisionSupport(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {
        Contract contract = contractService.findById(id);

        RiskAssessment assessment =
                riskAssessmentService.assess(contract);

        DecisionSupport decisionSupport =
                decisionSupportService.generate(assessment);

        DecisionSupportResponse decisionSupportResponse =
                decisionSupportMapper.toDecisionSupportResponse(decisionSupport);

        return ResponseEntity.ok(decisionSupportResponse);
    }
}