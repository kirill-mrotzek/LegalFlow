package de.kirillmrotzek.legalflow.mapper;

import de.kirillmrotzek.legalflow.decision.ApprovalRole;
import de.kirillmrotzek.legalflow.decision.DecisionPriority;
import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.dto.DecisionSupportResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionSupportMapperTest {

    private DecisionSupportMapper decisionSupportMapper =
            Mappers.getMapper(DecisionSupportMapper.class);

    @Test
    void shouldMapDecisionSupportToResponse() {

        DecisionSupport decisionSupport = new DecisionSupport(
                "Legal review required",
                "Contract contains unlimited liability",
                List.of(ApprovalRole.LEGAL),
                DecisionPriority.HIGH,
                "Assign contract to Legal for review"
        );

        DecisionSupportResponse result =
                decisionSupportMapper.toDecisionSupportResponse(decisionSupport);

        assertEquals(
                "Legal review required",
                result.getRecommendation()
        );

        assertEquals(
                "Contract contains unlimited liability",
                result.getRationale()
        );

        assertEquals(
                List.of(ApprovalRole.LEGAL),
                result.getRequiredApprovals()
        );

        assertEquals(
                DecisionPriority.HIGH,
                result.getPriority()
        );

        assertEquals(
                "Assign contract to Legal for review",
                result.getNextAction()
        );


    }



}
