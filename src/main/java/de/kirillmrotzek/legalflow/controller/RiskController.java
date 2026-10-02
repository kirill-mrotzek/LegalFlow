package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.RiskAssessmentResponse;
import de.kirillmrotzek.legalflow.exception.ErrorResponse;
import de.kirillmrotzek.legalflow.mapper.RiskAssessmentMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.RiskAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Risk",
        description = "API for contract risk assessment"
)
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class RiskController {

    private final ContractService contractService;
    private final RiskAssessmentService riskAssessmentService;
    private final RiskAssessmentMapper riskAssessmentMapper;

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
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<RiskAssessmentResponse> getRiskAssessment(
            @Parameter(description = "Unique contract ID")
            @PathVariable Long id) {

        Contract contract = contractService.findById(id);

        RiskAssessment assessment =
                riskAssessmentService.assess(contract);

        RiskAssessmentResponse response =
                riskAssessmentMapper.toResponse(assessment);

        return ResponseEntity.ok(response);
    }
}
