package de.kirillmrotzek.legalflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskDashboardResponse {

    private long totalContracts;
    private long highRiskContracts;
    private long mediumRiskContracts;
    private long lowRiskContracts;
    private long pendingReviews;
    private long expiringContracts;
}
