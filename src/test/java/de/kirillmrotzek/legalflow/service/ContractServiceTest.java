package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import de.kirillmrotzek.legalflow.enums.RiskLevel;

@SpringBootTest
class ContractServiceTest {

    @Autowired
    private ContractService contractService;

    @MockitoBean
    private ContractRepository contractRepository;

    @Test
    void save_shouldReturnSavedContract() {

        Contract contract = new Contract();
        contract.setTitle("NDA");

        Contract savedContract = new Contract();
        savedContract.setId(1L);
        savedContract.setTitle("NDA");

        when(contractRepository.save(contract))
                .thenReturn(savedContract);

        Contract result = contractService.save(contract);

        assertSame(savedContract, result);
    }

    @Test
    void findById_shouldReturnContract() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        Contract result = contractService.findById(1L);

        assertSame(contract, result);
    }

    @Test
    void findById_shouldThrowExceptionWhenContractNotFound() {

        when(contractRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContractNotFoundException.class,
                () -> contractService.findById(999L)
        );
    }

    @Test
    void search_withoutFilters_shouldReturnAllContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("NDA");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Service Agreement");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byStatus_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Active NDA");
        contract1.setContractStatus(ContractStatus.ACTIVE);

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Active Service Agreement");
        contract2.setContractStatus(ContractStatus.ACTIVE);

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        ContractStatus.ACTIVE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byCounterparty_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("NDA");
        contract1.setCounterparty("Google");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Service Agreement");
        contract2.setCounterparty("Google Cloud");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        "google",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byType_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Google NDA");
        contract1.setContractType(ContractType.NDA);

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Microsoft NDA");
        contract2.setContractType(ContractType.NDA);

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        ContractType.NDA,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byRiskLevel_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("High Risk NDA");
        contract1.setRiskLevel(RiskLevel.HIGH);

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("High Risk Service Agreement");
        contract2.setRiskLevel(RiskLevel.HIGH);

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        RiskLevel.HIGH,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byStatusAndCounterparty_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Google NDA");
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setCounterparty("Google");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Google Service Agreement");
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setCounterparty("Google Cloud");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        ContractStatus.ACTIVE,
                        null,
                        null,
                        "google",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_withAllFilters_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Google NDA");
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setContractType(ContractType.NDA);
        contract.setRiskLevel(RiskLevel.HIGH);
        contract.setCounterparty("Google");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        ContractStatus.ACTIVE,
                        ContractType.NDA,
                        RiskLevel.HIGH,
                        "google",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void update_shouldUpdateContractAndReturnSavedContract() {

        Contract existingContract = new Contract();
        existingContract.setId(1L);
        existingContract.setTitle("Old NDA");
        existingContract.setContractNumber("NDA-001");
        existingContract.setCounterparty("Google");
        existingContract.setContractType(ContractType.NDA);
        existingContract.setContractStatus(ContractStatus.DRAFT);
        existingContract.setRiskLevel(RiskLevel.LOW);
        existingContract.setStartDate(LocalDate.of(2026, 1, 1));
        existingContract.setEndDate(LocalDate.of(2027, 1, 1));
        existingContract.setGoverningLaw("German Law");
        existingContract.setContractValue(new BigDecimal("10000"));
        existingContract.setAutoRenewal(false);

        Contract newContract = new Contract();
        newContract.setTitle("Updated Service Agreement");
        newContract.setContractNumber("SERVICE-002");
        newContract.setCounterparty("Microsoft");
        newContract.setContractType(ContractType.SERVICE);
        newContract.setContractStatus(ContractStatus.ACTIVE);
        newContract.setRiskLevel(RiskLevel.HIGH);
        newContract.setStartDate(LocalDate.of(2026, 9, 1));
        newContract.setEndDate(LocalDate.of(2027, 9, 1));
        newContract.setGoverningLaw("Austrian Law");
        newContract.setContractValue(new BigDecimal("25000"));
        newContract.setAutoRenewal(true);

        Contract savedContract = new Contract();
        savedContract.setId(1L);
        savedContract.setTitle("Updated Service Agreement");
        savedContract.setContractNumber("SERVICE-002");
        savedContract.setCounterparty("Microsoft");
        savedContract.setContractType(ContractType.SERVICE);
        savedContract.setContractStatus(ContractStatus.ACTIVE);
        savedContract.setRiskLevel(RiskLevel.HIGH);
        savedContract.setStartDate(LocalDate.of(2026, 9, 1));
        savedContract.setEndDate(LocalDate.of(2027, 9, 1));
        savedContract.setGoverningLaw("Austrian Law");
        savedContract.setContractValue(new BigDecimal("25000"));
        savedContract.setAutoRenewal(true);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(existingContract));

        when(contractRepository.save(existingContract))
                .thenReturn(savedContract);

        Contract result =
                contractService.update(1L, newContract);

        assertSame(savedContract, result);

        assertEquals(
                "Updated Service Agreement",
                existingContract.getTitle()
        );

        assertEquals(
                "SERVICE-002",
                existingContract.getContractNumber()
        );

        assertEquals(
                "Microsoft",
                existingContract.getCounterparty()
        );

        assertEquals(
                ContractType.SERVICE,
                existingContract.getContractType()
        );

        assertEquals(
                ContractStatus.ACTIVE,
                existingContract.getContractStatus()
        );

        assertEquals(
                RiskLevel.HIGH,
                existingContract.getRiskLevel()
        );

        assertEquals(
                LocalDate.of(2026, 9, 1),
                existingContract.getStartDate()
        );

        assertEquals(
                LocalDate.of(2027, 9, 1),
                existingContract.getEndDate()
        );

        assertEquals(
                "Austrian Law",
                existingContract.getGoverningLaw()
        );

        assertEquals(
                new BigDecimal("25000"),
                existingContract.getContractValue()
        );

        assertTrue(
                existingContract.getAutoRenewal()
        );
    }

    @Test
    void update_shouldThrowExceptionWhenContractNotFound() {

        Contract contract = new Contract();
        contract.setTitle("Updated NDA");

        when(contractRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContractNotFoundException.class,
                () -> contractService.update(999L, contract)
        );
    }

    @Test
    void delete_shouldDeleteContract() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        contractService.delete(1L);

        verify(contractRepository).delete(contract);
    }

    @Test
    void delete_shouldThrowExceptionWhenContractNotFound() {

        when(contractRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ContractNotFoundException.class,
                () -> contractService.delete(999L)
        );
    }

    @Test
    void search_byMinValue_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Small Service Agreement");
        contract1.setContractValue(new BigDecimal("15000"));

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Large Service Agreement");
        contract2.setContractValue(new BigDecimal("50000"));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        new BigDecimal("10000"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byMaxValue_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Small Service Agreement");
        contract1.setContractValue(new BigDecimal("15000"));

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Medium Service Agreement");
        contract2.setContractValue(new BigDecimal("30000"));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        new BigDecimal("50000"),
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(2, result.getContent().size());

        assertSame(
                contract1,
                result.getContent().get(0)
        );

        assertSame(
                contract2,
                result.getContent().get(1)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byMinAndMaxValue_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Enterprise Service Agreement");
        contract.setContractValue(new BigDecimal("30000"));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        new BigDecimal("10000"),
                        new BigDecimal("50000"),
                        null,
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byStartDateFrom_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setStartDate(LocalDate.of(2026, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.of(2026, 1, 1),
                        null,
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byStartDateTo_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setStartDate(LocalDate.of(2026, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.of(2026, 12, 31),
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byEndDateFrom_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.of(2027, 1, 1),
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byEndDateTo_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.of(2027, 12, 31),
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byStartDateRange_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setStartDate(LocalDate.of(2026, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31),
                        null,
                        null,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_byEndDateRange_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDate.of(2027, 1, 1),
                        LocalDate.of(2027, 12, 31),
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void search_withAllFiltersIncludingDates_shouldReturnMatchingContracts() {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Google NDA");
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setContractType(ContractType.NDA);
        contract.setRiskLevel(RiskLevel.HIGH);
        contract.setCounterparty("Google");
        contract.setContractValue(new BigDecimal("25000"));
        contract.setStartDate(LocalDate.of(2026, 6, 1));
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(contractPage);

        Page<Contract> result =
                contractService.search(
                        ContractStatus.ACTIVE,
                        ContractType.NDA,
                        RiskLevel.HIGH,
                        "google",
                        new BigDecimal("10000"),
                        new BigDecimal("50000"),
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31),
                        LocalDate.of(2027, 1, 1),
                        LocalDate.of(2027, 12, 31),
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertSame(
                contract,
                result.getContent().get(0)
        );

        verify(contractRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }
}