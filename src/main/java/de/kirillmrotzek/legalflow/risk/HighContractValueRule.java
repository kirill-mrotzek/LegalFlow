package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HighContractValueRule implements RiskRule {

    private final RiskRuleProperties properties;

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        if (contract.getContractValue() != null &&
                contract.getContractValue()
                        .compareTo(properties.getHighContractValue().getThreshold()) > 0) {

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
            String formattedThreshold =
                    numberFormat.format(
                            properties.getHighContractValue().getThreshold()
                    );

            return Optional.of(
                    new RiskFactor(
                            "HIGH_CONTRACT_VALUE",
                            properties.getHighContractValue().getPoints(),
                            "Contract value exceeds € " + formattedThreshold,
                            "High contract value increases potential financial exposure"
                    )
            );
        }

        return Optional.empty();
    }
}
