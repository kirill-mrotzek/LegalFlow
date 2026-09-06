package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskProperties;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiskLevelCalculator {

    private final RiskProperties properties;

    public RiskLevel calculate(int score) {
        if (score >= properties.getHigh()) {
            return RiskLevel.HIGH;
        }

        if (score >= properties.getMedium()) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }
}
