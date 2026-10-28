package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.RiskDashboardResponse;
import de.kirillmrotzek.legalflow.service.RiskDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskDashboardController.class)
class RiskDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskDashboardService riskDashboardService;

    @Test
    @WithMockUser(
            username = "kirill",
            authorities = {"CONTRACT_READ"}
    )
    void getRiskDashboard_shouldReturnDashboard() throws Exception {

        RiskDashboardResponse response =
                new RiskDashboardResponse(
                        10L,
                        2L,
                        4L,
                        4L,
                        3L,
                        2L
                );

        when(riskDashboardService.getRiskDashboard(7))
                .thenReturn(response);

        mockMvc.perform(get("/dashboard/risk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalContracts").value(10));

        verify(riskDashboardService)
                .getRiskDashboard(7);
    }

    @Test
    @WithMockUser(
            username = "kirill",
            authorities = {"CONTRACT_READ"}
    )
    void getRiskDashboard_shouldPassCustomExpiringWindow()
            throws Exception {

        RiskDashboardResponse response =
                new RiskDashboardResponse(
                        10L,
                        2L,
                        4L,
                        4L,
                        3L,
                        5L
                );

        when(riskDashboardService.getRiskDashboard(30))
                .thenReturn(response);

        mockMvc.perform(
                        get("/dashboard/risk")
                                .param("expiringWithinDays", "30")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiringContracts").value(5));

        verify(riskDashboardService)
                .getRiskDashboard(30);
    }
}