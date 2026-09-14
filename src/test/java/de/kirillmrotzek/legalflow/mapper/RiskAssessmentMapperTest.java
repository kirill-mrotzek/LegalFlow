package de.kirillmrotzek.legalflow.mapper;

import de.kirillmrotzek.legalflow.dto.RiskAssessmentResponse;
import de.kirillmrotzek.legalflow.dto.RiskFactorResponse;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskAssessmentMapperTest {

    private RiskAssessmentMapper mapper;


    @BeforeEach
    void setUp() {
        mapper = RiskAssessmentMapper.INSTANCE;
    }

    @Test
    void toResponse_shouldMapRiskFactor() {

        RiskFactor factor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds €100.000"
        );

        RiskFactorResponse result = mapper.toResponse(factor);

        assertEquals(
                "HIGH_CONTRACT_VALUE",
                result.getCode()
        );

        assertEquals(
                30,
                result.getPoints()
        );

        assertEquals(
                "Contract value exceeds €100.000",
                result.getReason()
        );
    }

    @Test
    void toResponse_shouldMapRiskAssessment() {

        RiskFactor firstFactor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds €100.000"
        );

        RiskFactor secondFactor = new RiskFactor(
                "AUTO_RENEWAL",
                10,
                "Contract contains automatic renewal"
        );

        RiskAssessment assessment = new RiskAssessment(
                40,
                RiskLevel.HIGH,
                List.of(firstFactor, secondFactor)
        );

        RiskAssessmentResponse result = mapper.toResponse(assessment);

        assertEquals(40, result.getScore());

        assertEquals(
                RiskLevel.HIGH,
                result.getRiskLevel()
        );

        assertEquals(2, result.getFactors().size());

        assertEquals(
                "HIGH_CONTRACT_VALUE",
                result.getFactors().get(0).getCode()
        );

        assertEquals(
                30,
                result.getFactors().get(0).getPoints()
        );

        assertEquals(
                "Contract value exceeds €100.000",
                result.getFactors().get(0).getReason()
        );

        assertEquals(
                "AUTO_RENEWAL",
                result.getFactors().get(1).getCode()
        );

        assertEquals(
                10,
                result.getFactors().get(1).getPoints()
        );

        assertEquals(
                "Contract contains automatic renewal",
                result.getFactors().get(1).getReason()
        );
    }
}
