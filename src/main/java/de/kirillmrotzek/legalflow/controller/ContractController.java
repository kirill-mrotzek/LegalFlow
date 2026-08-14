package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import de.kirillmrotzek.legalflow.enums.ContractStatus;

import java.util.List;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    @GetMapping
    public List<ContractResponse> getAllContracts(
            @RequestParam(required = false) ContractStatus status) {

        List<Contract> contracts;

        if (status != null) {
            contracts = contractService.findByStatus(status);
        } else {
            contracts = contractService.findAll();
        }

        return contracts.stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(@PathVariable Long id) {

        Contract contract = contractService.findById(id);

        return ResponseEntity.ok(
                contractMapper.toResponse(contract));
    }

    @PostMapping
    public ResponseEntity<ContractResponse> createContract(@Valid @RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract savedContract = contractService.save(contract);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(
                        "Location",
                        "/contracts/" + savedContract.getId()
                )
                .body(contractMapper.toResponse(savedContract));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractResponse> updateContract(@PathVariable Long id,
                                                           @Valid @RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract updatedContract = contractService.update(id, contract);

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
