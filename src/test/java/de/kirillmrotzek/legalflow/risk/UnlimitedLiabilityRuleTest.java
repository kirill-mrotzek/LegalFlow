package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnlimitedLiabilityRuleTest {

    private UnlimitedLiabilityRule rule;

    @BeforeEach
    void setUp() {

        RiskRuleProperties properties = new RiskRuleProperties();

        RiskRuleProperties.UnlimitedLiability unlimitedLiability =
                new RiskRuleProperties.UnlimitedLiability();

        unlimitedLiability.setPoints(25);

        properties.setUnlimitedLiability(unlimitedLiability);

        rule = new UnlimitedLiabilityRule(properties);
    }

    @Test
    void evaluate_shouldReturnRiskFactor_whenUnlimitedLiabilityIsTrue() {

        Contract contract = new Contract();
        contract.setUnlimitedLiability(true);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isPresent());
        assertEquals("UNLIMITED_LIABILITY", result.get().getCode());
        assertEquals(25, result.get().getPoints());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenUnlimitedLiabilityIsFalse() {

        Contract contract = new Contract();
        contract.setUnlimitedLiability(false);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenUnlimitedLiabilityIsNull() {

        Contract contract = new Contract();
        contract.setUnlimitedLiability(null);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldUseConfiguredPoints() {

        RiskRuleProperties properties = new RiskRuleProperties();

        RiskRuleProperties.UnlimitedLiability unlimitedLiability =
                new RiskRuleProperties.UnlimitedLiability();

        unlimitedLiability.setPoints(40);

        properties.setUnlimitedLiability(unlimitedLiability);

        UnlimitedLiabilityRule configuredRule =
                new UnlimitedLiabilityRule(properties);

        Contract contract = new Contract();
        contract.setUnlimitedLiability(true);

        Optional<RiskFactor> result = configuredRule.evaluate(contract);

        assertTrue(result.isPresent());
        assertEquals(40, result.get().getPoints());
    }
}
