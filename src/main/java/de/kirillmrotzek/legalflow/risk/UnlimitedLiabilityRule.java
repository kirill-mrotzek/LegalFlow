package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnlimitedLiabilityRule implements RiskRule {

    private final RiskRuleProperties properties;

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        if (Boolean.TRUE.equals(contract.getUnlimitedLiability())) {

            return Optional.of(
                    new RiskFactor(
                            "UNLIMITED_LIABILITY",
                            properties.getUnlimitedLiability().getPoints(),
                            "Contract contains unlimited liability",
                            "Unlimited liability increases potential financial exposure"
                    )
            );
        }

        return Optional.empty();
    }
}
