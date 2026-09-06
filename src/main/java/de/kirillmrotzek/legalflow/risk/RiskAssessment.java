package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RiskAssessment {

    private final int score;

    private final RiskLevel riskLevel;

    private final List<RiskFactor> factors;
}
