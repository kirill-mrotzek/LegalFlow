package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AutoRenewalRule implements RiskRule {

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        if (Boolean.TRUE.equals(contract.getAutoRenewal())) {

            return Optional.of(
                    new RiskFactor(
                            "AUTO_RENEWAL",
                            10,
                            "Contract contains automatic renewal"
                    )
            );
        }

        return Optional.empty();
    }
}
