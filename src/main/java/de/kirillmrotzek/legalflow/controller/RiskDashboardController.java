package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.RiskDashboardResponse;
import de.kirillmrotzek.legalflow.service.RiskDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class RiskDashboardController {

    private final RiskDashboardService riskDashboardService;

    @GetMapping("/risk")
    public RiskDashboardResponse getRiskDashboard(
            @RequestParam(defaultValue = "7") int expiringWithinDays
    ) {
        return riskDashboardService.getRiskDashboard(expiringWithinDays);
    }
}
