package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    public List<ContractResponse> getAllContracts() {

        List<Contract> contracts = contractService.findAll();

        List<ContractResponse> responses = new ArrayList<>();

        for (Contract contract : contracts) {
            responses.add(toResponse(contract));
        }
        return responses;
    }

    @GetMapping("/{id}")
    public ContractResponse getContractById(@PathVariable Long id) {

        Contract contract = contractService.findById(id);

        return toResponse(contract);
    }

    @PostMapping
    public ContractResponse createContract(@RequestBody ContractRequest request) {

        Contract contract = toEntity(request);

        Contract savedContract = contractService.save(contract);

        return toResponse(savedContract);
    }

    @PutMapping("/{id}")
    public Contract updateContract(@PathVariable Long id,
                                   @RequestBody Contract contract) {
        return contractService.update(id, contract);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable Long id) {
        contractService.delete(id);

    }

    private ContractResponse toResponse(Contract contract) {

        ContractResponse response = new ContractResponse();

        response.setId(contract.getId());
        response.setTitle(contract.getTitle());
        response.setContractNumber(contract.getContractNumber());
        response.setCounterparty(contract.getCounterparty());
        response.setContractType(contract.getContractType());
        response.setContractStatus(contract.getContractStatus());
        response.setRiskLevel(contract.getRiskLevel());
        response.setStartDate(contract.getStartDate());
        response.setEndDate(contract.getEndDate());
        response.setGoverningLaw(contract.getGoverningLaw());
        response.setContractValue(contract.getContractValue());
        response.setAutoRenewal(contract.getAutoRenewal());

        return response;
    }

    private Contract toEntity(ContractRequest request) {

        Contract contract = new Contract();

        contract.setTitle(request.getTitle());
        contract.setContractNumber(request.getContractNumber());
        contract.setCounterparty(request.getCounterparty());
        contract.setContractType(request.getContractType());
        contract.setContractStatus(request.getContractStatus());
        contract.setRiskLevel(request.getRiskLevel());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setGoverningLaw(request.getGoverningLaw());
        contract.setContractValue(request.getContractValue());
        contract.setAutoRenewal(request.getAutoRenewal());

        return contract;
    }
}
