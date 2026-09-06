package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class LongTermContractRule implements RiskRule {

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        LocalDate startDate = contract.getStartDate();
        LocalDate endDate = contract.getEndDate();

        if (startDate == null || endDate == null) {
            return Optional.empty();
        }

        LocalDate thresholdDate = startDate.plusYears(3);

        if (endDate.isAfter(thresholdDate)) {

            return Optional.of(
                    new RiskFactor(
                            "LONG_TERM_CONTRACT",
                            15,
                            "Contract term exceeds 3 years"
                    )
            );
        }

        return Optional.empty();
    }
}
