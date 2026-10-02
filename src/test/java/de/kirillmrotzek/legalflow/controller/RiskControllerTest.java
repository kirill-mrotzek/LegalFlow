package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.RiskAssessmentResponse;
import de.kirillmrotzek.legalflow.dto.RiskFactorResponse;
import de.kirillmrotzek.legalflow.enums.RiskFactorCode;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.mapper.RiskAssessmentMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskController.class)
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService contractService;

    @MockitoBean
    private RiskAssessmentService riskAssessmentService;

    @MockitoBean
    private RiskAssessmentMapper riskAssessmentMapper;

    @Test
    void getRiskAssessment_shouldReturnRiskAssessment() throws Exception {

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

        RiskFactorResponse factorResponse = new RiskFactorResponse();
        factorResponse.setCode("HIGH_CONTRACT_VALUE");
        factorResponse.setPoints(30);
        factorResponse.setReason("Contract value exceeds € 100.000");
        factorResponse.setRiskExplanation(
                "High contract value increases potential financial exposure"
        );

        RiskAssessmentResponse response = new RiskAssessmentResponse();
        response.setScore(30);
        response.setRiskLevel(RiskLevel.HIGH);
        response.setFactors(List.of(factorResponse));

        when(riskAssessmentMapper.toResponse(assessment))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts/1/risk-assessment")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(30))
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.factors[0].code").value("HIGH_CONTRACT_VALUE"))
                .andExpect(jsonPath("$.factors[0].points").value(30))
                .andExpect(jsonPath("$.factors[0].reason")
                        .value("Contract value exceeds € 100.000"))
                .andExpect(jsonPath("$.factors[0].riskExplanation")
                        .value("High contract value increases potential financial exposure"));

        verify(contractService).findById(1L);
        verify(riskAssessmentService).assess(contract);
        verify(riskAssessmentMapper).toResponse(assessment);
    }

    @Test
    void getRiskAssessment_shouldReturn404() throws Exception {

        when(contractService.findById(999L))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(get("/contracts/999/risk-assessment"))
                .andExpect(status().isNotFound());

        verify(contractService).findById(999L);
        verify(riskAssessmentService, never()).assess(any(Contract.class));
    }
}
