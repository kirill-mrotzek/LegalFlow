package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.config.RiskProperties;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RiskLevelCalculatorTest {

    private RiskLevelCalculator calculator;

    @BeforeEach
    void setUp() {
        RiskProperties properties = new RiskProperties();
        properties.setMedium(30);
        properties.setHigh(60);

        calculator = new RiskLevelCalculator(properties);
    }

    @Test
    void calculate_shouldReturnLow_whenScoreIsBelowMediumThreshold() {

        RiskLevel result = calculator.calculate(29);

        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    void calculate_shouldReturnMedium_whenScoreEqualsMediumThreshold() {

        RiskLevel result = calculator.calculate(30);

        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    void calculate_shouldReturnMedium_whenScoreIsBetweenThresholds() {

        RiskLevel result = calculator.calculate(59);

        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    void calculate_shouldReturnHigh_whenScoreEqualsHighThreshold() {

        RiskLevel result = calculator.calculate(60);

        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    void calculate_shouldReturnHigh_whenScoreIsAboveHighThreshold() {

        RiskLevel result = calculator.calculate(100);

        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    void calculate_shouldReturnLow_whenScoreIsZero() {

        RiskLevel result = calculator.calculate(0);

        assertEquals(RiskLevel.LOW, result);
    }
}
