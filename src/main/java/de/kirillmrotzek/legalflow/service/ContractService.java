package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }
}
