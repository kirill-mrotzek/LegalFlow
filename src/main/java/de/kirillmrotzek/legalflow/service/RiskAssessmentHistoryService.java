package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.model.RiskAssessment;
import de.kirillmrotzek.legalflow.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RiskAssessmentHistoryService {

    private final RiskAssessmentRepository riskAssessmentRepository;

    public void record(
            Contract contract,
            RiskLevel riskLevel
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        RiskAssessment assessment = new RiskAssessment();

        assessment.setContract(contract);
        assessment.setRiskLevel(riskLevel);
        assessment.setAssessedAt(LocalDateTime.now());
        assessment.setAssessedBy(authentication.getName());

        riskAssessmentRepository.save(assessment);
    }

    public void deleteByContractId(Long contractId) {
        riskAssessmentRepository.deleteByContractId(contractId);
    }
}
