package de.kirillmrotzek.legalflow.dto;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String contractNumber;

    @NotBlank
    private String counterparty;

    @NotNull
    private ContractType contractType;

    @NotNull
    private ContractStatus contractStatus;

    @NotNull
    private RiskLevel riskLevel;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String governingLaw;

    @NotNull
    @Positive
    private BigDecimal contractValue;

    @NotNull
    private Boolean autoRenewal;
}
