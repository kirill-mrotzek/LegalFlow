package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractPageResponse;
import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.dto.RiskAssessmentResponse;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.ErrorResponse;
import de.kirillmrotzek.legalflow.exception.InvalidDateRangeException;
import de.kirillmrotzek.legalflow.exception.InvalidValueRangeException;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.mapper.RiskAssessmentMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.service.ContractService;
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

    @GetMapping
    @Operation(
            summary = "Search contracts",
            description = "Returns a paginated list of contracts with optional filtering by status, type, risk level, counterparty, contract value, and contract dates. The minValue must be less than or equal to maxValue.",
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
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) ContractType type,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) String counterparty,
            @Parameter(
                    description = "Minimum contract value"
            )
            @RequestParam(required = false) BigDecimal minValue,
            @Parameter(
                    description = "Maximum contract value"
            )
            @RequestParam(required = false) BigDecimal maxValue,
            @RequestParam(required = false) LocalDate startDateFrom,
            @RequestParam(required = false) LocalDate startDateTo,
            @RequestParam(required = false) LocalDate endDateFrom,
            @RequestParam(required = false) LocalDate endDateTo,
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
            description = "Returns the risk assessment of a contract, including its risk score, risk level, and contributing risk factors.",
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
                                    schema = @Schema(implementation = ErrorResponse.class)
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
                                    schema = @Schema(implementation = ErrorResponse.class)
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
}