package de.kirillmrotzek.legalflow.mapper;

import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.model.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "reviewStatus", source = "reviewStatus")
    ContractResponse toResponse(Contract contract);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "riskLevel", ignore = true)
    @Mapping(target = "contractStatus", ignore = true)
    @Mapping(target = "reviewStatus", ignore = true)
    Contract toEntity(ContractRequest request);
}
