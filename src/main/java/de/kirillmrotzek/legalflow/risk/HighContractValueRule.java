package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class HighContractValueRule implements RiskRule {

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {
        if (contract.getContractValue() != null &&
                contract.getContractValue().compareTo(new BigDecimal("100000")) > 0) {

            return Optional.of(
                    new RiskFactor(
                            "HIGH_CONTRACT_VALUE",
                            30,
                            "Contract value exceeds € 100.000",
                            "High contract value increases potential financial exposure"
                    )
            );
        }
        return Optional.empty();
    }
}
