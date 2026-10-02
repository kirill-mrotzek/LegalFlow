package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.dto.DecisionSupportResponse;
import de.kirillmrotzek.legalflow.exception.ErrorResponse;
import de.kirillmrotzek.legalflow.mapper.DecisionSupportMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.DecisionSupportService;
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
        name = "Decision Support",
        description = "API for contract decision support"
)
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class DecisionSupportController {

    private final ContractService contractService;
    private final RiskAssessmentService riskAssessmentService;
    private final DecisionSupportService decisionSupportService;
    private final DecisionSupportMapper decisionSupportMapper;

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
                                    schema = @Schema(
                                            implementation = ErrorResponse.class
                                    )
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

        DecisionSupportResponse response =
                decisionSupportMapper.toDecisionSupportResponse(
                        decisionSupport
                );

        return ResponseEntity.ok(response);
    }
}
