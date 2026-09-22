package de.kirillmrotzek.legalflow.dto;

import lombok.Data;

@Data
public class RiskFactorResponse {

    private String code;

    private int points;

    private String reason;

    private String riskExplanation;
}
