package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UnlimitedLiabilityRule implements RiskRule {

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        if (Boolean.TRUE.equals(contract.getUnlimitedLiability())) {
            return Optional.of(
                    new RiskFactor(
                            "UNLIMITED_LIABILITY",
                            25,
                            "Contract contains unlimited liability"
                    )
            );
        }

        return Optional.empty();
    }
}
