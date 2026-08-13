package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
    void findAll_shouldReturnContracts() {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("NDA");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Service Agreement");

        when(contractRepository.findAll())
                .thenReturn(List.of(contract1, contract2));

        List<Contract> result = contractService.findAll();

        assertEquals(2, result.size());
        assertSame(contract1, result.get(0));
        assertSame(contract2, result.get(1));
    }

    @Test
    void update_shouldUpdateContractAndReturnSavedContract() {

        Contract existingContract = new Contract();
        existingContract.setId(1L);
        existingContract.setTitle("Old NDA");
        existingContract.setContractNumber("NDA-001");
        existingContract.setCounterparty("Google");

        Contract newContract = new Contract();
        newContract.setTitle("Updated NDA");
        newContract.setContractNumber("NDA-002");
        newContract.setCounterparty("Microsoft");

        Contract savedContract = new Contract();
        savedContract.setId(1L);
        savedContract.setTitle("Updated NDA");
        savedContract.setContractNumber("NDA-002");
        savedContract.setCounterparty("Microsoft");

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(existingContract));

        when(contractRepository.save(existingContract))
                .thenReturn(savedContract);

        Contract result = contractService.update(1L, newContract);

        assertSame(savedContract, result);

        assertEquals("Updated NDA", existingContract.getTitle());
        assertEquals("NDA-002", existingContract.getContractNumber());
        assertEquals("Microsoft", existingContract.getCounterparty());
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
}