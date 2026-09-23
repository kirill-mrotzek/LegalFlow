package de.kirillmrotzek.legalflow.dto;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeContractStatusRequest {

    @NotNull
    private ContractStatus status;
}