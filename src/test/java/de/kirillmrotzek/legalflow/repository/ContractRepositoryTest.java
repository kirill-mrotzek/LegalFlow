package de.kirillmrotzek.legalflow.repository;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.specification.ContractSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(
                LocalDate.of(2026, 1, 1),
                result.get().getStartDate()
        );
        assertEquals(
                LocalDate.of(2026, 12, 31),
                result.get().getEndDate()
        );
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
    void findAll_withStatusSpecification_shouldReturnMatchingContracts() {

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

        Specification<Contract> specification =
                ContractSpecification.hasStatus(
                        ContractStatus.ACTIVE
                );

        Page<Contract> result =
                contractRepository.findAll(
                        specification,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertEquals(
                "Active NDA",
                result.getContent().get(0).getTitle()
        );

        assertEquals(
                ContractStatus.ACTIVE,
                result.getContent().get(0).getContractStatus()
        );
    }

    @Test
    void findAll_withCounterpartySpecification_shouldReturnMatchingContracts() {

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

        Specification<Contract> specification =
                ContractSpecification.counterpartyContains(
                        "google"
                );

        Page<Contract> result =
                contractRepository.findAll(
                        specification,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertEquals(
                "Google",
                result.getContent().get(0).getCounterparty()
        );

        assertEquals(
                "NDA",
                result.getContent().get(0).getTitle()
        );
    }

    @Test
    void findAll_withTypeSpecification_shouldReturnMatchingContracts() {

        Contract ndaContract = new Contract();
        ndaContract.setTitle("NDA");
        ndaContract.setContractNumber("NDA-004");
        ndaContract.setContractType(ContractType.NDA);
        ndaContract.setContractStatus(ContractStatus.ACTIVE);
        ndaContract.setRiskLevel(RiskLevel.LOW);
        ndaContract.setStartDate(LocalDate.of(2026, 1, 1));
        ndaContract.setEndDate(LocalDate.of(2026, 12, 31));
        ndaContract.setCounterparty("Google");

        Contract serviceContract = new Contract();
        serviceContract.setTitle("Service Agreement");
        serviceContract.setContractNumber("SA-003");
        serviceContract.setContractType(ContractType.SERVICE);
        serviceContract.setContractStatus(ContractStatus.ACTIVE);
        serviceContract.setRiskLevel(RiskLevel.MEDIUM);
        serviceContract.setStartDate(LocalDate.of(2026, 2, 1));
        serviceContract.setEndDate(LocalDate.of(2027, 1, 31));
        serviceContract.setCounterparty("Microsoft");

        contractRepository.save(ndaContract);
        contractRepository.save(serviceContract);

        Specification<Contract> specification =
                ContractSpecification.hasType(
                        ContractType.NDA
                );

        Page<Contract> result =
                contractRepository.findAll(
                        specification,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        assertEquals(
                "NDA",
                result.getContent().get(0).getTitle()
        );

        assertEquals(
                ContractType.NDA,
                result.getContent().get(0).getContractType()
        );
    }

    @Test
    void findAll_withCombinedSpecifications_shouldReturnMatchingContracts() {

        Contract googleActiveNda = new Contract();
        googleActiveNda.setTitle("Google NDA");
        googleActiveNda.setContractNumber("NDA-005");
        googleActiveNda.setContractType(ContractType.NDA);
        googleActiveNda.setContractStatus(ContractStatus.ACTIVE);
        googleActiveNda.setRiskLevel(RiskLevel.LOW);
        googleActiveNda.setStartDate(LocalDate.of(2026, 1, 1));
        googleActiveNda.setEndDate(LocalDate.of(2026, 12, 31));
        googleActiveNda.setCounterparty("Google");

        Contract googleDraftNda = new Contract();
        googleDraftNda.setTitle("Google Draft NDA");
        googleDraftNda.setContractNumber("NDA-006");
        googleDraftNda.setContractType(ContractType.NDA);
        googleDraftNda.setContractStatus(ContractStatus.DRAFT);
        googleDraftNda.setRiskLevel(RiskLevel.LOW);
        googleDraftNda.setStartDate(LocalDate.of(2026, 1, 1));
        googleDraftNda.setEndDate(LocalDate.of(2026, 12, 31));
        googleDraftNda.setCounterparty("Google");

        Contract microsoftActiveNda = new Contract();
        microsoftActiveNda.setTitle("Microsoft NDA");
        microsoftActiveNda.setContractNumber("NDA-007");
        microsoftActiveNda.setContractType(ContractType.NDA);
        microsoftActiveNda.setContractStatus(ContractStatus.ACTIVE);
        microsoftActiveNda.setRiskLevel(RiskLevel.MEDIUM);
        microsoftActiveNda.setStartDate(LocalDate.of(2026, 1, 1));
        microsoftActiveNda.setEndDate(LocalDate.of(2026, 12, 31));
        microsoftActiveNda.setCounterparty("Microsoft");

        contractRepository.save(googleActiveNda);
        contractRepository.save(googleDraftNda);
        contractRepository.save(microsoftActiveNda);

        Specification<Contract> specification =
                Specification.allOf(
                        ContractSpecification.hasStatus(
                                ContractStatus.ACTIVE
                        ),
                        ContractSpecification.hasType(
                                ContractType.NDA
                        ),
                        ContractSpecification.counterpartyContains(
                                "google"
                        )
                );

        Page<Contract> result =
                contractRepository.findAll(
                        specification,
                        Pageable.unpaged()
                );

        assertEquals(1, result.getContent().size());

        Contract found = result.getContent().get(0);

        assertEquals("Google NDA", found.getTitle());
        assertEquals("Google", found.getCounterparty());
        assertEquals(ContractType.NDA, found.getContractType());
        assertEquals(ContractStatus.ACTIVE, found.getContractStatus());
    }
}
