package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpcomingDeadlineServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private UpcomingDeadlineService upcomingDeadlineService;

    @Test
    void findUpcomingDeadlines_shouldReturnContractsWithinRange() {

        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(30);

        Contract contract = new Contract();

        when(contractRepository.findByContractStatusAndEndDateBetween(
                de.kirillmrotzek.legalflow.enums.ContractStatus.ACTIVE,
                today,
                deadline
        )).thenReturn(List.of(contract));

        List<Contract> result =
                upcomingDeadlineService.findUpcomingDeadlines(30);

        assertEquals(1, result.size());
        assertSame(contract, result.get(0));

        verify(contractRepository).findByContractStatusAndEndDateBetween(
                de.kirillmrotzek.legalflow.enums.ContractStatus.ACTIVE,
                today,
                deadline
        );
    }

    @Test
    void findUpcomingDeadlines_withZeroDays_shouldSearchTodayOnly() {

        LocalDate today = LocalDate.now();

        when(contractRepository.findByContractStatusAndEndDateBetween(
                de.kirillmrotzek.legalflow.enums.ContractStatus.ACTIVE,
                today,
                today
        )).thenReturn(List.of());

        List<Contract> result =
                upcomingDeadlineService.findUpcomingDeadlines(0);

        assertTrue(result.isEmpty());

        verify(contractRepository).findByContractStatusAndEndDateBetween(
                de.kirillmrotzek.legalflow.enums.ContractStatus.ACTIVE,
                today,
                today
        );
    }

    @Test
    void findUpcomingDeadlines_withNegativeDays_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> upcomingDeadlineService.findUpcomingDeadlines(-1)
        );

        verifyNoInteractions(contractRepository);
    }
}
