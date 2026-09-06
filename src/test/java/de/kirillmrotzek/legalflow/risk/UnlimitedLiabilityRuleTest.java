package de.kirillmrotzek.legalflow.risk;

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
        rule = new UnlimitedLiabilityRule();
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
}
