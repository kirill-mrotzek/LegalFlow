package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LongTermContractRuleTest {

    private LongTermContractRule rule;

    @BeforeEach
    void setUp() {
        rule = new LongTermContractRule();
    }

    @Test
    void evaluate_shouldReturnRiskFactor_whenContractTermExceedsThreeYears() {

        Contract contract = new Contract();
        contract.setStartDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2026, 1, 2));

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isPresent());

        assertEquals("LONG_TERM_CONTRACT", result.get().getCode());
        assertEquals(15, result.get().getPoints());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenContractTermIsExactlyThreeYears() {

        Contract contract = new Contract();
        contract.setStartDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2026, 1, 1));

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenContractTermIsLessThanThreeYears() {

        Contract contract = new Contract();
        contract.setStartDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2025, 12, 31));

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }

    @Test
    void evaluate_shouldReturnEmpty_whenDatesAreNull() {

        Contract contract = new Contract();

        Optional<RiskFactor> result = rule.evaluate(contract);

        assertTrue(result.isEmpty());
    }
}