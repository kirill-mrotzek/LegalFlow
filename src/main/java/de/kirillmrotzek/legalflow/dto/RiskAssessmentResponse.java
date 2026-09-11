package de.kirillmrotzek.legalflow.dto;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import lombok.Data;

import java.util.List;

@Data
public class RiskAssessmentResponse {

private int score;

private RiskLevel riskLevel;

private List<RiskFactorResponse> factors;
}
