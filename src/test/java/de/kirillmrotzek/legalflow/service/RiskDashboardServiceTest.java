package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.dto.RiskDashboardResponse;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskDashboardServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UpcomingDeadlineService upcomingDeadlineService;

    @InjectMocks
    private RiskDashboardService riskDashboardService;

    @Test
    void getRiskDashboard_shouldReturnAggregatedValues() {

        when(contractRepository.count()).thenReturn(10L);
        when(contractRepository.countByRiskLevel(RiskLevel.HIGH))
                .thenReturn(2L);
        when(contractRepository.countByRiskLevel(RiskLevel.MEDIUM))
                .thenReturn(4L);
        when(contractRepository.countByRiskLevel(RiskLevel.LOW))
                .thenReturn(4L);
        when(contractRepository.countByReviewStatus(ReviewStatus.PENDING))
                .thenReturn(3L);

        when(upcomingDeadlineService.findUpcomingDeadlines(30))
                .thenReturn(List.of(
                        new Contract(),
                        new Contract(),
                        new Contract(),
                        new Contract(),
                        new Contract()
                ));

        RiskDashboardResponse response =
                riskDashboardService.getRiskDashboard(30);

        assertEquals(10L, response.getTotalContracts());
        assertEquals(2L, response.getHighRiskContracts());
        assertEquals(4L, response.getMediumRiskContracts());
        assertEquals(4L, response.getLowRiskContracts());
        assertEquals(3L, response.getPendingReviews());
        assertEquals(5L, response.getExpiringContracts());
    }

    @Test
    void getRiskDashboard_shouldPassExpiringWindowToUpcomingDeadlineService() {

        when(contractRepository.count()).thenReturn(0L);
        when(contractRepository.countByRiskLevel(RiskLevel.HIGH))
                .thenReturn(0L);
        when(contractRepository.countByRiskLevel(RiskLevel.MEDIUM))
                .thenReturn(0L);
        when(contractRepository.countByRiskLevel(RiskLevel.LOW))
                .thenReturn(0L);
        when(contractRepository.countByReviewStatus(ReviewStatus.PENDING))
                .thenReturn(0L);

        when(upcomingDeadlineService.findUpcomingDeadlines(7))
                .thenReturn(List.of());

        riskDashboardService.getRiskDashboard(7);

        verify(upcomingDeadlineService)
                .findUpcomingDeadlines(7);
    }

    @Test
    void getRiskDashboard_shouldUseExpectedRepositoryAggregations() {

        when(contractRepository.count()).thenReturn(10L);
        when(contractRepository.countByRiskLevel(RiskLevel.HIGH))
                .thenReturn(2L);
        when(contractRepository.countByRiskLevel(RiskLevel.MEDIUM))
                .thenReturn(4L);
        when(contractRepository.countByRiskLevel(RiskLevel.LOW))
                .thenReturn(4L);
        when(contractRepository.countByReviewStatus(ReviewStatus.PENDING))
                .thenReturn(3L);

        when(upcomingDeadlineService.findUpcomingDeadlines(30))
                .thenReturn(List.of());

        riskDashboardService.getRiskDashboard(30);

        verify(contractRepository).count();
        verify(contractRepository).countByRiskLevel(RiskLevel.HIGH);
        verify(contractRepository).countByRiskLevel(RiskLevel.MEDIUM);
        verify(contractRepository).countByRiskLevel(RiskLevel.LOW);
        verify(contractRepository)
                .countByReviewStatus(ReviewStatus.PENDING);

        verifyNoMoreInteractions(contractRepository);
    }
}
