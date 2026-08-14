package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void save_shouldPersistContract() {

        Contract contract = new Contract();

        contract.setTitle("NDA");
        contract.setContractNumber("NDA-001");
        contract.setContractType(ContractType.NDA);
        contract.setContractStatus(ContractStatus.DRAFT);
        contract.setRiskLevel(RiskLevel.LOW);
        contract.setStartDate(LocalDate.of(2026, 1, 1));
        contract.setEndDate(LocalDate.of(2026, 12, 31));
        contract.setCounterparty("Google");

        Contract savedContract = contractRepository.save(contract);

        assertNotNull(savedContract.getId());

        Optional<Contract> result =
                contractRepository.findById(savedContract.getId());

        assertTrue(result.isPresent());

        assertEquals("NDA", result.get().getTitle());
        assertEquals("NDA-001", result.get().getContractNumber());
        assertEquals(ContractType.NDA, result.get().getContractType());
        assertEquals(ContractStatus.DRAFT, result.get().getContractStatus());
        assertEquals(RiskLevel.LOW, result.get().getRiskLevel());
        assertEquals(LocalDate.of(2026, 1, 1), result.get().getStartDate());
        assertEquals(LocalDate.of(2026, 12, 31), result.get().getEndDate());
        assertEquals("Google", result.get().getCounterparty());
    }

    @Test
    void findAll_shouldReturnContracts() {

        Contract contract1 = new Contract();
        contract1.setTitle("NDA");
        contract1.setContractNumber("NDA-001");
        contract1.setContractType(ContractType.NDA);
        contract1.setContractStatus(ContractStatus.DRAFT);
        contract1.setRiskLevel(RiskLevel.LOW);
        contract1.setStartDate(LocalDate.of(2026, 1, 1));
        contract1.setEndDate(LocalDate.of(2026, 12, 31));
        contract1.setCounterparty("Google");

        Contract contract2 = new Contract();
        contract2.setTitle("Service Agreement");
        contract2.setContractNumber("SA-001");
        contract2.setContractType(ContractType.SERVICE);
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setRiskLevel(RiskLevel.MEDIUM);
        contract2.setStartDate(LocalDate.of(2026, 2, 1));
        contract2.setEndDate(LocalDate.of(2027, 1, 31));
        contract2.setCounterparty("Microsoft");

        contractRepository.save(contract1);
        contractRepository.save(contract2);

        List<Contract> result = contractRepository.findAll();

        assertEquals(2, result.size());
        assertEquals("NDA", result.get(0).getTitle());
        assertEquals("Service Agreement", result.get(1).getTitle());
    }

    @Test
    void save_shouldRejectDuplicateContractNumber() {

        Contract contract1 = new Contract();

        contract1.setTitle("NDA");
        contract1.setContractNumber("NDA-001");
        contract1.setContractType(ContractType.NDA);
        contract1.setContractStatus(ContractStatus.DRAFT);
        contract1.setRiskLevel(RiskLevel.LOW);
        contract1.setStartDate(LocalDate.of(2026, 1, 1));
        contract1.setEndDate(LocalDate.of(2026, 12, 31));
        contract1.setCounterparty("Google");

        Contract contract2 = new Contract();

        contract2.setTitle("Another NDA");
        contract2.setContractNumber("NDA-001");
        contract2.setContractType(ContractType.NDA);
        contract2.setContractStatus(ContractStatus.DRAFT);
        contract2.setRiskLevel(RiskLevel.LOW);
        contract2.setStartDate(LocalDate.of(2026, 2, 1));
        contract2.setEndDate(LocalDate.of(2026, 12, 31));
        contract2.setCounterparty("Microsoft");

        contractRepository.save(contract1);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> contractRepository.saveAndFlush(contract2)
        );
    }

    @Test
    void findByContractStatus_shouldReturnContractsWithGivenStatus() {

        Contract activeContract = new Contract();
        activeContract.setTitle("Active NDA");
        activeContract.setContractNumber("NDA-002");
        activeContract.setContractType(ContractType.NDA);
        activeContract.setContractStatus(ContractStatus.ACTIVE);
        activeContract.setRiskLevel(RiskLevel.LOW);
        activeContract.setStartDate(LocalDate.of(2026, 1, 1));
        activeContract.setEndDate(LocalDate.of(2026, 12, 31));
        activeContract.setCounterparty("Google");

        Contract draftContract = new Contract();
        draftContract.setTitle("Draft Agreement");
        draftContract.setContractNumber("DA-001");
        draftContract.setContractType(ContractType.SERVICE);
        draftContract.setContractStatus(ContractStatus.DRAFT);
        draftContract.setRiskLevel(RiskLevel.MEDIUM);
        draftContract.setStartDate(LocalDate.of(2026, 2, 1));
        draftContract.setEndDate(LocalDate.of(2027, 1, 31));
        draftContract.setCounterparty("Microsoft");

        contractRepository.save(activeContract);
        contractRepository.save(draftContract);

        List<Contract> result =
                contractRepository.findByContractStatus(ContractStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals("Active NDA", result.get(0).getTitle());
        assertEquals(ContractStatus.ACTIVE, result.get(0).getContractStatus());
    }

    @Test
    void findByCounterpartyContainingIgnoreCase_shouldReturnMatchingContracts() {

        Contract contract1 = new Contract();
        contract1.setTitle("NDA");
        contract1.setContractNumber("NDA-003");
        contract1.setContractType(ContractType.NDA);
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setRiskLevel(RiskLevel.LOW);
        contract1.setStartDate(LocalDate.of(2026, 1, 1));
        contract1.setEndDate(LocalDate.of(2026, 12, 31));
        contract1.setCounterparty("Google");

        Contract contract2 = new Contract();
        contract2.setTitle("Service Agreement");
        contract2.setContractNumber("SA-002");
        contract2.setContractType(ContractType.SERVICE);
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setRiskLevel(RiskLevel.MEDIUM);
        contract2.setStartDate(LocalDate.of(2026, 2, 1));
        contract2.setEndDate(LocalDate.of(2027, 1, 31));
        contract2.setCounterparty("Microsoft");

        contractRepository.save(contract1);
        contractRepository.save(contract2);

        List<Contract> result =
                contractRepository.findByCounterpartyContainingIgnoreCase("google");

        assertEquals(1, result.size());
        assertEquals("Google", result.get(0).getCounterparty());
        assertEquals("NDA", result.get(0).getTitle());
    }
}
