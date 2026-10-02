package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.enums.RiskFactorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskFactor {

    private final RiskFactorCode riskFactorCode;

    private final int points;

    private final String reason;

    private final String riskExplanation;
}