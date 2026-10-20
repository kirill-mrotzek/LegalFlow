package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpcomingDeadlineService {

    private final ContractRepository contractRepository;

    @PreAuthorize("hasAuthority('CONTRACT_READ')")
    public List<Contract> findUpcomingDeadlines(int days) {

        if (days < 0) {
            throw new IllegalArgumentException(
                    "Days must not be negative"
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(days);

        return contractRepository
                .findByContractStatusAndEndDateBetween(
                        ContractStatus.ACTIVE,
                        today,
                        deadline
                );
    }
}
