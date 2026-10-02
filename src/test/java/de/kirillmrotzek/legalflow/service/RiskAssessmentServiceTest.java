package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.config.RiskProperties;
import de.kirillmrotzek.legalflow.config.RiskRuleProperties;
import de.kirillmrotzek.legalflow.enums.RiskFactorCode;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.reference.EUCountryRegistry;
import de.kirillmrotzek.legalflow.risk.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RiskAssessmentServiceTest {

    @Test
    void assess_shouldCalculateScoreAndRiskLevel() {

        RiskRule rule = mock(RiskRule.class);

        RiskFactor factor = new RiskFactor(
                RiskFactorCode.HIGH_CONTRACT_VALUE,
                30,
                "Test risk factor",
                "Test risk explanation"
        );

        when(rule.evaluate(any(Contract.class)))
                .thenReturn(Optional.of(factor));

        RiskLevelCalculator riskLevelCalculator =
                mock(RiskLevelCalculator.class);

        when(riskLevelCalculator.calculate(30))
                .thenReturn(RiskLevel.MEDIUM);

        RiskAssessmentService service =
                new RiskAssessmentService(
                        List.of(rule),
                        riskLevelCalculator
                );

        Contract contract = new Contract();

        RiskAssessment result = service.assess(contract);

        assertEquals(30, result.getScore());
        assertEquals(RiskLevel.MEDIUM, result.getRiskLevel());
        assertEquals(1, result.getFactors().size());
        assertEquals(
                RiskFactorCode.HIGH_CONTRACT_VALUE,
                result.getFactors().get(0).getRiskFactorCode()
        );

        verify(riskLevelCalculator).calculate(30);
    }

    @Test
    void assess_shouldReturnZeroScore_whenNoRiskFactorsFound() {

        RiskRule rule = mock(RiskRule.class);

        when(rule.evaluate(any(Contract.class)))
                .thenReturn(Optional.empty());

        RiskLevelCalculator riskLevelCalculator =
                mock(RiskLevelCalculator.class);

        when(riskLevelCalculator.calculate(0))
                .thenReturn(RiskLevel.LOW);

        RiskAssessmentService service =
                new RiskAssessmentService(
                        List.of(rule),
                        riskLevelCalculator
                );

        Contract contract = new Contract();

        RiskAssessment result = service.assess(contract);

        assertEquals(0, result.getScore());
        assertEquals(RiskLevel.LOW, result.getRiskLevel());

        assertTrue(result.getFactors().isEmpty());

        verify(riskLevelCalculator).calculate(0);
    }

    @Test
    void assess_shouldSumScoresFromMultipleRules() {

        RiskRule firstRule = mock(RiskRule.class);
        RiskRule secondRule = mock(RiskRule.class);

        RiskFactor firstFactor = new RiskFactor(
                RiskFactorCode.HIGH_CONTRACT_VALUE,
                30,
                "First risk factor",
                "First risk explanation"
        );

        RiskFactor secondFactor = new RiskFactor(
                RiskFactorCode.AUTO_RENEWAL,
                20,
                "Second risk factor",
                "Second risk explanation"
        );

        when(firstRule.evaluate(any(Contract.class)))
                .thenReturn(Optional.of(firstFactor));

        when(secondRule.evaluate(any(Contract.class)))
                .thenReturn(Optional.of(secondFactor));

        RiskLevelCalculator riskLevelCalculator =
                mock(RiskLevelCalculator.class);

        when(riskLevelCalculator.calculate(50))
                .thenReturn(RiskLevel.MEDIUM);

        RiskAssessmentService service =
                new RiskAssessmentService(
                        List.of(firstRule, secondRule),
                        riskLevelCalculator
                );

        Contract contract = new Contract();

        RiskAssessment result = service.assess(contract);

        assertEquals(50, result.getScore());
        assertEquals(RiskLevel.MEDIUM, result.getRiskLevel());
        assertEquals(2, result.getFactors().size());
        assertEquals(
                RiskFactorCode.HIGH_CONTRACT_VALUE,
                result.getFactors().get(0).getRiskFactorCode()
        );
        assertEquals(
                RiskFactorCode.AUTO_RENEWAL,
                result.getFactors().get(1).getRiskFactorCode()
        );

        verify(riskLevelCalculator).calculate(50);
    }

    @Test
    void assess_shouldCombineMultipleRealRiskRules() {

        Contract contract = new Contract();
        contract.setContractValue(new BigDecimal("150000"));
        contract.setAutoRenewal(true);
        contract.setUnlimitedLiability(true);
        contract.setStartDate(LocalDate.of(2023, 1, 1));
        contract.setEndDate(LocalDate.of(2026, 1, 2));
        contract.setGoverningLaw("Swiss law");

        RiskRuleProperties ruleProperties = new RiskRuleProperties();

        RiskRuleProperties.HighContractValue highContractValue =
                new RiskRuleProperties.HighContractValue();
        highContractValue.setThreshold(new BigDecimal("100000"));
        highContractValue.setPoints(30);
        ruleProperties.setHighContractValue(highContractValue);

        RiskRuleProperties.AutoRenewal autoRenewal =
                new RiskRuleProperties.AutoRenewal();
        autoRenewal.setPoints(10);
        ruleProperties.setAutoRenewal(autoRenewal);

        RiskRuleProperties.LongTermContract longTermContract =
                new RiskRuleProperties.LongTermContract();
        longTermContract.setYears(3);
        longTermContract.setPoints(15);
        ruleProperties.setLongTermContract(longTermContract);

        RiskRuleProperties.ForeignGoverningLaw foreignGoverningLaw =
                new RiskRuleProperties.ForeignGoverningLaw();
        foreignGoverningLaw.setEuPoints(10);
        foreignGoverningLaw.setNonEuPoints(20);
        ruleProperties.setForeignGoverningLaw(foreignGoverningLaw);

        RiskRuleProperties.UnlimitedLiability unlimitedLiability =
                new RiskRuleProperties.UnlimitedLiability();
        unlimitedLiability.setPoints(25);
        ruleProperties.setUnlimitedLiability(unlimitedLiability);

        HighContractValueRule highContractValueRule =
                new HighContractValueRule(ruleProperties);

        AutoRenewalRule autoRenewalRule =
                new AutoRenewalRule(ruleProperties);

        LongTermContractRule longTermContractRule =
                new LongTermContractRule(ruleProperties);

        ForeignGoverningRule foreignGoverningRule =
                new ForeignGoverningRule(
                        new GoverningLawClassifier(
                                new EUCountryRegistry()
                        ),
                        ruleProperties
                );

        UnlimitedLiabilityRule unlimitedLiabilityRule =
                new UnlimitedLiabilityRule(ruleProperties);

        List<RiskRule> rules = List.of(
                highContractValueRule,
                autoRenewalRule,
                longTermContractRule,
                foreignGoverningRule,
                unlimitedLiabilityRule
        );

        RiskProperties properties = new RiskProperties();
        properties.setMedium(30);
        properties.setHigh(60);

        RiskLevelCalculator riskLevelCalculator =
                new RiskLevelCalculator(properties);

        RiskAssessmentService service =
                new RiskAssessmentService(
                        rules,
                        riskLevelCalculator
                );

        RiskAssessment result = service.assess(contract);

        assertEquals(100, result.getScore());
        assertEquals(RiskLevel.HIGH, result.getRiskLevel());
        assertEquals(5, result.getFactors().size());

        assertTrue(
                result.getFactors().stream()
                        .anyMatch(factor ->
                                factor.getRiskFactorCode() == RiskFactorCode.HIGH_CONTRACT_VALUE)
        );

        assertTrue(
                result.getFactors().stream()
                        .anyMatch(factor ->
                                factor.getRiskFactorCode() == RiskFactorCode.AUTO_RENEWAL)
        );

        assertTrue(
                result.getFactors().stream()
                        .anyMatch(factor ->
                                factor.getRiskFactorCode() == RiskFactorCode.LONG_TERM_CONTRACT)
        );

        assertTrue(
                result.getFactors().stream()
                        .anyMatch(factor ->
                                factor.getRiskFactorCode() == RiskFactorCode.FOREIGN_GOVERNING_LAW_NON_EU)
        );

        assertTrue(
                result.getFactors().stream()
                        .anyMatch(factor ->
                                factor.getRiskFactorCode() == RiskFactorCode.UNLIMITED_LIABILITY)
        );
    }
}