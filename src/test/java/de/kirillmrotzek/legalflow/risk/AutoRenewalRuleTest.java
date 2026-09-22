package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoRenewalRuleTest {

    private AutoRenewalRule rule;

    @BeforeEach
    void setUp() {

        RiskRuleProperties properties = new RiskRuleProperties();

        RiskRuleProperties.AutoRenewal autoRenewal =
                new RiskRuleProperties.AutoRenewal();

        autoRenewal.setPoints(10);

        properties.setAutoRenewal(autoRenewal);

        rule = new AutoRenewalRule(properties);
    }

    @Test
    void evaluate_shouldReturnRiskFactor_whenAutoRenewalIsEnabled() {

        Contract contract = new Contract();
        contract.setAutoRenewal(true);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isPresent());

        assertEquals("AUTO_RENEWAL", result.get().getCode());
        assertEquals(10, result.get().getPoints());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenAutoRenewalIsDisabled() {

        Contract contract = new Contract();
        contract.setAutoRenewal(false);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenAutoRenewalIsNull() {

        Contract contract = new Contract();
        contract.setAutoRenewal(null);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldUseConfiguredPoints() {

        RiskRuleProperties properties = new RiskRuleProperties();

        RiskRuleProperties.AutoRenewal autoRenewal =
                new RiskRuleProperties.AutoRenewal();

        autoRenewal.setPoints(20);

        properties.setAutoRenewal(autoRenewal);

        AutoRenewalRule configuredRule =
                new AutoRenewalRule(properties);

        Contract contract = new Contract();
        contract.setAutoRenewal(true);

        Optional<RiskFactor> result = configuredRule.evaluate(contract);

        assertTrue(result.isPresent());
        assertEquals("AUTO_RENEWAL", result.get().getCode());
        assertEquals(20, result.get().getPoints());
    }
}
