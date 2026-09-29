package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.decision.LegalReviewDecision;
import de.kirillmrotzek.legalflow.enums.ReviewType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegalReviewDecisionServiceTest {

    private final LegalReviewDecisionService service =
            new LegalReviewDecisionService();

    @Test
    void shouldReturnStandardReviewWhenNoEnhancedFactorsExist() {

        RiskAssessment assessment = new RiskAssessment(
                0,
                RiskLevel.LOW,
                List.of()
        );
        LegalReviewDecision decision = service.decide(assessment);

        assertEquals(ReviewType.STANDARD, decision.getReviewType());

    }

    @Test
    void shouldReturnEnhancedReviewWhenForeignGoverningLawFactorExist() {

        RiskAssessment assessment = new RiskAssessment(
                80,
                RiskLevel.HIGH,
                List.of(new RiskFactor(
                        "FOREIGN_GOVERNING_LAW_NON_EU",
                        20,
                        "Foreign governing law outside EU",
                        "Enhanced legal review required"
                ))
        );
        LegalReviewDecision decision = service.decide(assessment);

        assertEquals(ReviewType.ENHANCED, decision.getReviewType());
        assertEquals(
                "Foreign governing law outside EU",
                decision.getReason()
        );
    }

    @Test
    void shouldReturnEnhancedReviewWhenUnlimitedLiabilityFactorExists() {

        RiskAssessment assessment = new RiskAssessment(
                80,
                RiskLevel.HIGH,
                List.of(new RiskFactor(
                        "UNLIMITED_LIABILITY",
                        20,
                        "Unlimited liability",
                        "Enhanced legal review required"
                ))
        );
        LegalReviewDecision decision = service.decide(assessment);

        assertEquals(ReviewType.ENHANCED, decision.getReviewType());
    }

    @Test
    void shouldCombineReasonsWhenMultipleEnhancedFactorsExist() {

        RiskAssessment assessment = new RiskAssessment(
                100,
                RiskLevel.HIGH,
                List.of(new RiskFactor(
                        "FOREIGN_GOVERNING_LAW_NON_EU",
                        20,
                        "Foreign governing law outside EU",
                        "Enhanced legal review required"
                ),
                        new RiskFactor(
                                "UNLIMITED_LIABILITY",
                                20,
                                "Unlimited liability",
                                "Enhanced legal review required"
                        ))

        );

        LegalReviewDecision decision = service.decide(assessment);

        assertEquals(ReviewType.ENHANCED, decision.getReviewType());
        assertEquals(
                "Foreign governing law outside EU; Unlimited liability",
                decision.getReason()
        );
    }





}
