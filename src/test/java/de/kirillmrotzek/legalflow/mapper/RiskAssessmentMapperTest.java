package de.kirillmrotzek.legalflow.mapper;

import de.kirillmrotzek.legalflow.dto.RiskAssessmentResponse;
import de.kirillmrotzek.legalflow.dto.RiskFactorResponse;
import de.kirillmrotzek.legalflow.enums.RiskFactorCode;
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
                RiskFactorCode.HIGH_CONTRACT_VALUE,
                30,
                "Contract value exceeds €100.000",
                "High contract value increases potential financial exposure"
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
        assertEquals(
                "High contract value increases potential financial exposure",
                result.getRiskExplanation()
        );
    }
}