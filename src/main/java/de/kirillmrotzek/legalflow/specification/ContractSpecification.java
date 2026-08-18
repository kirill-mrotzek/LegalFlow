package de.kirillmrotzek.legalflow.specification;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ContractSpecification {

    private ContractSpecification() {
    }

    public static Specification<Contract> hasStatus(
            ContractStatus status
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("contractStatus"),
                        status
                );
    }

    public static Specification<Contract> hasType(
            ContractType type
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("contractType"),
                        type
                );
    }

    public static Specification<Contract> hasRiskLevel(
            RiskLevel riskLevel
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("riskLevel"),
                        riskLevel
                );
    }

    public static Specification<Contract> counterpartyContains(
            String counterparty
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("counterparty")
                        ),
                        "%" + counterparty.toLowerCase() + "%"
                );
    }

    public static Specification<Contract> contractValueGreaterThanOrEqualTo(BigDecimal minValue)
    {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("contractValue"),
                        minValue
                );
    }

    public static Specification<Contract> contractValueLessThanOrEqualTo(BigDecimal maxValue)
    {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("contractValue"),
                        maxValue
                );
    }

    public static Specification<Contract> startDateGreaterThanOrEqualTo(LocalDate startDateFrom)
    {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startDate"),
                        startDateFrom
                );
    }

    public static Specification<Contract> startDateLessThanOrEqualTo(LocalDate startDateTo)
    {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("startDate"),
                        startDateTo
                );
    }

    public static Specification<Contract> endDateGreaterThanOrEqualTo(LocalDate endDateFrom)
    {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("endDate"),
                        endDateFrom
                );
    }

    public static Specification<Contract> endDateLessThanOrEqualTo(LocalDate endDateTo)
    {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("endDate"),
                        endDateTo
                );
    }
}
