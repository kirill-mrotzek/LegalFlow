package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HighContractValueRuleTest {

    private HighContractValueRule rule;

    @BeforeEach
    void setUp() {
        rule = new HighContractValueRule();
    }

    @Test
    void evaluate_shouldReturnRiskFactor_whenContractValueIsHigh() {

        Contract contract = new Contract();
        contract.setContractValue(new BigDecimal("150000"));

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isPresent());

        assertEquals("HIGH_CONTRACT_VALUE", result.get().getCode());
        assertEquals(30, result.get().getPoints());
        assertEquals(
                "High contract value increases potential financial exposure",
                result.get().getRiskExplanation()
        );
    }

    @Test
    void evaluate_shouldReturnEmpty_whenContractValueIsLow() {

        Contract contract = new Contract();
        contract.setContractValue(new BigDecimal("50000"));

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenContractValueIsExactlyThreshold() {

        Contract contract = new Contract();
        contract.setContractValue(new BigDecimal("100000"));

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenContractValueIsNull() {

        Contract contract = new Contract();
        contract.setContractValue(null);

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }
}
