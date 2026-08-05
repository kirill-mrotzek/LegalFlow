package de.kirillmrotzek.legalflow.dto;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractResponse {
    private Long  id;
    private String title;
    private String contractNumber;
    private String counterparty;
    private ContractType contractType;
    private ContractStatus contractStatus;
    private RiskLevel riskLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String governingLaw;
    private BigDecimal contractValue;
    private Boolean autoRenewal;
}
