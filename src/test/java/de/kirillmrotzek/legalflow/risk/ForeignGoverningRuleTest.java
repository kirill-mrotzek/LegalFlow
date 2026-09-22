package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.reference.EUCountryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ForeignGoverningRuleTest {

    private ForeignGoverningRule rule;

    @BeforeEach
    void setUp() {

        RiskRuleProperties properties = new RiskRuleProperties();

        RiskRuleProperties.ForeignGoverningLaw foreignGoverningLaw =
                new RiskRuleProperties.ForeignGoverningLaw();

        foreignGoverningLaw.setEuPoints(10);
        foreignGoverningLaw.setNonEuPoints(20);

        properties.setForeignGoverningLaw(foreignGoverningLaw);

        rule = new ForeignGoverningRule(
                new GoverningLawClassifier(
                        new EUCountryRegistry()
                ),
                properties
        );
    }

    @Test
    void evaluate_shouldReturnEuRiskFactor_whenGoverningLawIsFrench() {

        Contract contract = new Contract();
        contract.setGoverningLaw("French law");

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isPresent());
        assertEquals("FOREIGN_GOVERNING_LAW_EU", result.get().getCode());
        assertEquals(10, result.get().getPoints());
    }

    @Test
    void evaluate_shouldReturnNonEuRiskFactor_whenGoverningLawIsSwiss() {

        Contract contract = new Contract();
        contract.setGoverningLaw("Swiss law");

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isPresent());
        assertEquals("FOREIGN_GOVERNING_LAW_NON_EU", result.get().getCode());
        assertEquals(20, result.get().getPoints());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenGoverningLawIsGerman() {

        Contract contract = new Contract();
        contract.setGoverningLaw("German law");

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenGoverningLawIsNull() {

        Contract contract = new Contract();
        contract.setGoverningLaw(null);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenGoverningLawIsBlank() {

        Contract contract = new Contract();
        contract.setGoverningLaw("   ");

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }
}