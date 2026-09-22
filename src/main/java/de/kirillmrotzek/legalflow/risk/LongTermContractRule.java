package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LongTermContractRule implements RiskRule {

    private final RiskRuleProperties properties;

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        LocalDate startDate = contract.getStartDate();
        LocalDate endDate = contract.getEndDate();

        if (startDate == null || endDate == null) {
            return Optional.empty();
        }

        LocalDate thresholdDate =
                startDate.plusYears(properties.getLongTermContract().getYears());

        if (endDate.isAfter(thresholdDate)) {

            return Optional.of(
                    new RiskFactor(
                            "LONG_TERM_CONTRACT",
                            properties.getLongTermContract().getPoints(),
                            "Contract term exceeds "
                                    + properties.getLongTermContract().getYears()
                                    + " years",
                            "Long contract terms increase the duration of legal and financial exposure"
                    )
            );
        }

        return Optional.empty();
    }
}
