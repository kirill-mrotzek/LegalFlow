package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.ReviewStatus;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.specification.ContractSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final RiskAssessmentService riskAssessmentService;

    public Contract save(Contract contract) {

        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setReviewStatus(ReviewStatus.PENDING);

        RiskAssessment assessment =
                riskAssessmentService.assess(contract);

        contract.setRiskLevel(assessment.getRiskLevel());

        return contractRepository.save(contract);
    }

    public Page<Contract> search(
            ContractStatus status,
            ContractType type,
            RiskLevel riskLevel,
            String counterparty,
            BigDecimal minValue,
            BigDecimal maxValue,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            LocalDate endDateFrom,
            LocalDate endDateTo,
            Pageable pageable) {

        Specification<Contract> specification =
                Specification.allOf();

        if (status != null) {
            specification = specification.and(
                    ContractSpecification.hasStatus(status)
            );
        }

        if (type != null) {
            specification = specification.and(
                    ContractSpecification.hasType(type)
            );
        }

        if (riskLevel != null) {
            specification = specification.and(
                    ContractSpecification.hasRiskLevel(riskLevel)
            );
        }

        if (counterparty != null && !counterparty.isBlank()) {
            specification = specification.and(
                    ContractSpecification.counterpartyContains(counterparty)
            );
        }

        if (minValue != null) {
            specification = specification.and(
                    ContractSpecification.contractValueGreaterThanOrEqualTo(minValue)
            );
        }

        if (maxValue != null) {
            specification = specification.and(
                    ContractSpecification.contractValueLessThanOrEqualTo(maxValue)
            );
        }

        if (startDateFrom != null) {
            specification = specification.and(
                    ContractSpecification.startDateGreaterThanOrEqualTo(startDateFrom)
            );
        }

        if (startDateTo != null) {
            specification = specification.and(
                    ContractSpecification.startDateLessThanOrEqualTo(startDateTo)
            );
        }

        if (endDateFrom != null) {
            specification = specification.and(
                    ContractSpecification.endDateGreaterThanOrEqualTo(endDateFrom)
            );
        }

        if (endDateTo != null) {
            specification = specification.and(
                    ContractSpecification.endDateLessThanOrEqualTo(endDateTo)
            );
        }

        return contractRepository.findAll(
                specification,
                pageable
        );
    }

    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));
    }

    public Contract update(Long id, Contract contract) {
        Contract existingContract = findById(id);

        existingContract.setTitle(contract.getTitle());
        existingContract.setContractNumber(contract.getContractNumber());
        existingContract.setCounterparty(contract.getCounterparty());
        existingContract.setContractType(contract.getContractType());
        existingContract.setUnlimitedLiability(
                contract.getUnlimitedLiability()
        );
        existingContract.setStartDate(contract.getStartDate());
        existingContract.setEndDate(contract.getEndDate());
        existingContract.setGoverningLaw(contract.getGoverningLaw());
        existingContract.setContractValue(contract.getContractValue());
        existingContract.setAutoRenewal(contract.getAutoRenewal());

        RiskAssessment assessment =
                riskAssessmentService.assess(existingContract);

        existingContract.setRiskLevel(assessment.getRiskLevel());

        return contractRepository.save(existingContract);
    }

    public void delete(Long id) {
        Contract existingContract = findById(id);
        contractRepository.delete(existingContract);
    }
}
