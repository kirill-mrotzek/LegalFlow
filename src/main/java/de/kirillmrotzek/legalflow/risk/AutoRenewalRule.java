package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AutoRenewalRule implements RiskRule {

    private final RiskRuleProperties properties;

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        if (Boolean.TRUE.equals(contract.getAutoRenewal())) {

            return Optional.of(
                    new RiskFactor(
                            "AUTO_RENEWAL",
                            properties.getAutoRenewal().getPoints(),
                            "Contract contains automatic renewal",
                            "Automatic renewal can extend contractual obligations if termination deadlines are missed"
                    )
            );
        }

        return Optional.empty();
    }
}
