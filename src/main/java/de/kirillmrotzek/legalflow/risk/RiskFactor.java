package de.kirillmrotzek.legalflow.risk;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskFactor {

    private final String code;

    private final int points;

    private final String reason;

    private final String riskExplanation;
}
