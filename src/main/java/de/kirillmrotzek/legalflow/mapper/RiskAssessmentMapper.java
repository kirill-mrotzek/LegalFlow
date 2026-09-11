package de.kirillmrotzek.legalflow.mapper;

import de.kirillmrotzek.legalflow.dto.RiskAssessmentResponse;
import de.kirillmrotzek.legalflow.dto.RiskFactorResponse;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RiskAssessmentMapper {

    RiskAssessmentResponse toResponse(RiskAssessment assessment);

    RiskFactorResponse toResponse(RiskFactor factor);

    RiskAssessmentMapper INSTANCE =
            Mappers.getMapper(RiskAssessmentMapper.class);
}
