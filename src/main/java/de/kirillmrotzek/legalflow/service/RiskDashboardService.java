package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.dto.RiskDashboardResponse;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskDashboardService {

    private final ContractRepository contractRepository;
    private final UpcomingDeadlineService upcomingDeadlineService;

    @PreAuthorize("hasAuthority('CONTRACT_READ')")
    public RiskDashboardResponse getRiskDashboard(int expiringWithinDays) {

        long totalContracts = contractRepository.count();

        long highRiskContracts =
                contractRepository.countByRiskLevel(RiskLevel.HIGH);

        long mediumRiskContracts =
                contractRepository.countByRiskLevel(RiskLevel.MEDIUM);

        long lowRiskContracts =
                contractRepository.countByRiskLevel(RiskLevel.LOW);

        long pendingReviews =
                contractRepository.countByReviewStatus(ReviewStatus.PENDING);

        long expiringContracts =
                upcomingDeadlineService
                        .findUpcomingDeadlines(expiringWithinDays)
                        .size();

        return new RiskDashboardResponse(
                totalContracts,
                highRiskContracts,
                mediumRiskContracts,
                lowRiskContracts,
                pendingReviews,
                expiringContracts
        );
    }
}
