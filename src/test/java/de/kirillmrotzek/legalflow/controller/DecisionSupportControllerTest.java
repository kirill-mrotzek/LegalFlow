package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.dto.DecisionSupportResponse;
import de.kirillmrotzek.legalflow.enums.ApprovalRole;
import de.kirillmrotzek.legalflow.enums.DecisionPriority;
import de.kirillmrotzek.legalflow.enums.RiskFactorCode;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.mapper.DecisionSupportMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.DecisionSupportService;
import de.kirillmrotzek.legalflow.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DecisionSupportController.class)
class DecisionSupportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskAssessmentService riskAssessmentService;

    @MockitoBean
    private ContractService contractService;

    @MockitoBean
    private DecisionSupportService decisionSupportService;

    @MockitoBean
    private DecisionSupportMapper decisionSupportMapper;

    @Test
    void getDecisionSupport_shouldReturnDecisionSupport() throws Exception {

        RiskFactor factor = new RiskFactor(
                RiskFactorCode.HIGH_CONTRACT_VALUE,
                30,
                "Contract value exceeds € 100.000",
                "High contract value increases potential financial exposure"
        );

        List<RiskFactor> factors = List.of(factor);

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.HIGH,
                factors
        );

        when(riskAssessmentService.assess(any(Contract.class)))
                .thenReturn(assessment);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("High Value Contract");

        when(contractService.findById(1L))
                .thenReturn(contract);

        DecisionSupport decisionSupport = new DecisionSupport(
                "Finance review required",
                "Contract value exceeds € 100.000",
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.MANAGEMENT
                ),
                DecisionPriority.MEDIUM,
                "Route contract for Legal, Finance, Management approval"
        );

        when(decisionSupportService.generate(assessment))
                .thenReturn(decisionSupport);

        DecisionSupportResponse response = new DecisionSupportResponse();
        response.setRecommendation("Finance review required");
        response.setRationale("Contract value exceeds € 100.000");
        response.setRequiredApprovals(List.of(
                ApprovalRole.LEGAL,
                ApprovalRole.FINANCE,
                ApprovalRole.MANAGEMENT
        ));
        response.setPriority(DecisionPriority.MEDIUM);
        response.setNextAction("Route contract for Legal, Finance, Management approval");

        when(decisionSupportMapper.toDecisionSupportResponse(decisionSupport))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts/1/decision-support")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendation").value("Finance review required"))
                .andExpect(jsonPath("$.rationale").value("Contract value exceeds € 100.000"))
                .andExpect(jsonPath("$.requiredApprovals[0]").value("LEGAL"))
                .andExpect(jsonPath("$.requiredApprovals[1]").value("FINANCE"))
                .andExpect(jsonPath("$.requiredApprovals[2]").value("MANAGEMENT"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.nextAction").value("Route contract for Legal, Finance, Management approval"));

        verify(contractService).findById(1L);
        verify(riskAssessmentService).assess(contract);
        verify(decisionSupportService).generate(assessment);
        verify(decisionSupportMapper).toDecisionSupportResponse(decisionSupport);
    }

    @Test
    void getDecisionSupport_shouldReturn404() throws Exception {

        when(contractService.findById(999L))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(get("/contracts/999/decision-support"))
                .andExpect(status().isNotFound());

        verify(contractService).findById(999L);
        verify(riskAssessmentService, never())
                .assess(any(Contract.class));
        verify(decisionSupportService, never())
                .generate(any(RiskAssessment.class));
    }
}
