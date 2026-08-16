package de.kirillmrotzek.legalflow.service;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public Page<Contract> findAll(Pageable pageable) {
        return contractRepository.findAll(pageable);
    }

    public Page<Contract> findByStatus(
            ContractStatus status,
            Pageable pageable) {

        return contractRepository.findByContractStatus(
                status,
                pageable
        );
    }

    public Page<Contract> findByCounterparty(
            String counterparty,
            Pageable pageable) {

        return contractRepository.findByCounterpartyContainingIgnoreCase(
                counterparty,
                pageable
        );
    }

    public Page<Contract> findByStatusAndCounterparty(
            ContractStatus status,
            String counterparty,
            Pageable pageable) {

        return contractRepository
                .findByContractStatusAndCounterpartyContainingIgnoreCase(
                        status,
                        counterparty,
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
        existingContract.setContractStatus(contract.getContractStatus());
        existingContract.setRiskLevel(contract.getRiskLevel());
        existingContract.setStartDate(contract.getStartDate());
        existingContract.setEndDate(contract.getEndDate());
        existingContract.setGoverningLaw(contract.getGoverningLaw());
        existingContract.setContractValue(contract.getContractValue());
        existingContract.setAutoRenewal(contract.getAutoRenewal());

        return contractRepository.save(existingContract);
    }

    public void delete(Long id) {
        Contract existingContract = findById(id);
        contractRepository.delete(existingContract);
    }
}
