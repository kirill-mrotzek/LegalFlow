package de.kirillmrotzek.legalflow.specification;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContractSpecificationTest {

    @Test
    void hasStatus_shouldCreateEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<Object> statusPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("contractStatus"))
                .thenReturn(statusPath);

        when(criteriaBuilder.equal(
                statusPath,
                ContractStatus.ACTIVE
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.hasStatus(
                        ContractStatus.ACTIVE
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("contractStatus");

        verify(criteriaBuilder).equal(
                statusPath,
                ContractStatus.ACTIVE
        );
    }

    @Test
    void hasType_shouldCreateEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<Object> typePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("contractType"))
                .thenReturn(typePath);

        when(criteriaBuilder.equal(
                typePath,
                ContractType.NDA
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.hasType(
                        ContractType.NDA
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("contractType");

        verify(criteriaBuilder).equal(
                typePath,
                ContractType.NDA
        );
    }

    @Test
    void hasRiskLevel_shouldCreateEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<Object> riskLevelPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("riskLevel"))
                .thenReturn(riskLevelPath);

        when(criteriaBuilder.equal(
                riskLevelPath,
                RiskLevel.HIGH
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.hasRiskLevel(
                        RiskLevel.HIGH
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("riskLevel");

        verify(criteriaBuilder).equal(
                riskLevelPath,
                RiskLevel.HIGH
        );
    }

    @Test
    void counterpartyContains_shouldCreateLikePredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<String> counterpartyPath = mock(Path.class);
        Expression<String> lowerExpression = mock(Expression.class);
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get("counterparty"))
                .thenReturn(counterpartyPath);

        when(criteriaBuilder.lower(counterpartyPath))
                .thenReturn(lowerExpression);

        when(criteriaBuilder.like(
                lowerExpression,
                "%acme%"
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.counterpartyContains("ACME");

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("counterparty");

        verify(criteriaBuilder).lower(counterpartyPath);

        verify(criteriaBuilder).like(
                lowerExpression,
                "%acme%"
        );
    }

    @Test
    void contractValueGreaterThanOrEqualTo_shouldCreateGreaterThanOrEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<BigDecimal> contractValuePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        BigDecimal minValue = new BigDecimal("10000.00");

        when(root.<BigDecimal>get("contractValue"))
                .thenReturn(contractValuePath);

        when(criteriaBuilder.greaterThanOrEqualTo(
                contractValuePath,
                minValue
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.contractValueGreaterThanOrEqualTo(
                        minValue
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("contractValue");

        verify(criteriaBuilder).greaterThanOrEqualTo(
                contractValuePath,
                minValue
        );
    }

    @Test
    void contractValueLessThanOrEqualTo_shouldCreateLessThanOrEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<BigDecimal> contractValuePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        BigDecimal maxValue = new BigDecimal("50000.00");

        when(root.<BigDecimal>get("contractValue"))
                .thenReturn(contractValuePath);

        when(criteriaBuilder.lessThanOrEqualTo(
                contractValuePath,
                maxValue
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.contractValueLessThanOrEqualTo(
                        maxValue
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("contractValue");

        verify(criteriaBuilder).lessThanOrEqualTo(
                contractValuePath,
                maxValue
        );
    }

    @Test
    void startDateGreaterThanOrEqualTo_shouldCreateGreaterThanOrEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<LocalDate> startDatePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        LocalDate startDateFrom =
                LocalDate.of(2026, 1, 1);

        when(root.<LocalDate>get("startDate"))
                .thenReturn(startDatePath);

        when(criteriaBuilder.greaterThanOrEqualTo(
                startDatePath,
                startDateFrom
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.startDateGreaterThanOrEqualTo(
                        startDateFrom
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("startDate");

        verify(criteriaBuilder).greaterThanOrEqualTo(
                startDatePath,
                startDateFrom
        );
    }

    @Test
    void startDateLessThanOrEqualTo_shouldCreateLessThanOrEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<LocalDate> startDatePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        LocalDate startDateTo =
                LocalDate.of(2026, 12, 31);

        when(root.<LocalDate>get("startDate"))
                .thenReturn(startDatePath);

        when(criteriaBuilder.lessThanOrEqualTo(
                startDatePath,
                startDateTo
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.startDateLessThanOrEqualTo(
                        startDateTo
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("startDate");

        verify(criteriaBuilder).lessThanOrEqualTo(
                startDatePath,
                startDateTo
        );
    }

    @Test
    void endDateGreaterThanOrEqualTo_shouldCreateGreaterThanOrEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<LocalDate> endDatePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        LocalDate endDateFrom =
                LocalDate.of(2026, 1, 1);

        when(root.<LocalDate>get("endDate"))
                .thenReturn(endDatePath);

        when(criteriaBuilder.greaterThanOrEqualTo(
                endDatePath,
                endDateFrom
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.endDateGreaterThanOrEqualTo(
                        endDateFrom
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("endDate");

        verify(criteriaBuilder).greaterThanOrEqualTo(
                endDatePath,
                endDateFrom
        );
    }

    @Test
    void endDateLessThanOrEqualTo_shouldCreateLessThanOrEqualPredicate() {

        Root<Contract> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        Path<LocalDate> endDatePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        LocalDate endDateTo =
                LocalDate.of(2026, 12, 31);

        when(root.<LocalDate>get("endDate"))
                .thenReturn(endDatePath);

        when(criteriaBuilder.lessThanOrEqualTo(
                endDatePath,
                endDateTo
        )).thenReturn(predicate);

        Specification<Contract> specification =
                ContractSpecification.endDateLessThanOrEqualTo(
                        endDateTo
                );

        Predicate result =
                specification.toPredicate(
                        root,
                        query,
                        criteriaBuilder
                );

        assertNotNull(result);

        verify(root).get("endDate");

        verify(criteriaBuilder).lessThanOrEqualTo(
                endDatePath,
                endDateTo
        );
    }
}