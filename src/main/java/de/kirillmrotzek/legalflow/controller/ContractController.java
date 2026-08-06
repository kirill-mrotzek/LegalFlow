package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
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
    private final ContractMapper contractMapper;

    @GetMapping
    public List<ContractResponse> getAllContracts() {

        return contractService.findAll()
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ContractResponse getContractById(@PathVariable Long id) {

        Contract contract = contractService.findById(id);

        return contractMapper.toResponse(contract);
    }

    @PostMapping
    public ContractResponse createContract(@RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract savedContract = contractService.save(contract);

        return contractMapper.toResponse(savedContract);
    }

    @PutMapping("/{id}")
    public ContractResponse updateContract(@PathVariable Long id,
                                           @RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract updatedContract = contractService.update(id, contract);

        return contractMapper.toResponse(updatedContract);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable Long id) {
        contractService.delete(id);
    }
}
