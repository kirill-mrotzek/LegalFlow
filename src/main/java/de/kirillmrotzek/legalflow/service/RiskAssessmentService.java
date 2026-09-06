package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import de.kirillmrotzek.legalflow.risk.RiskLevelCalculator;
import de.kirillmrotzek.legalflow.risk.RiskRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final List<RiskRule> rules;

    private final RiskLevelCalculator riskLevelCalculator;

    public RiskAssessment assess(Contract contract) {

        List<RiskFactor> factors = rules.stream()
                .map(rule -> rule.evaluate(contract))
                .flatMap(Optional::stream)
                .toList();

        int score = factors.stream()
                .mapToInt(RiskFactor::getPoints)
                .sum();

        RiskLevel riskLevel = riskLevelCalculator.calculate(score);

        return new RiskAssessment(score, riskLevel, factors);
    }
}



