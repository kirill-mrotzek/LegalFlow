package de.kirillmrotzek.legalflow.mapper;

import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.dto.DecisionSupportResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DecisionSupportMapper {

    DecisionSupportResponse toDecisionSupportResponse(DecisionSupport decisionSupport);
}
