package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    @GetMapping
    public Page<ContractResponse> getAllContracts(
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) String counterparty,
            Pageable pageable) {

        Page<Contract> contracts;

        if (status != null && counterparty != null) {
            contracts = contractService.findByStatusAndCounterparty(
                    status,
                    counterparty,
                    pageable
            );
        } else if (status != null) {
            contracts = contractService.findByStatus(
                    status,
                    pageable
            );
        } else if (counterparty != null) {
            contracts = contractService.findByCounterparty(
                    counterparty,
                    pageable
            );
        } else {
            contracts = contractService.findAll(pageable);
        }

        return contracts.map(contractMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(
            @PathVariable Long id) {

        Contract contract = contractService.findById(id);

        return ResponseEntity.ok(
                contractMapper.toResponse(contract)
        );
    }

    @PostMapping
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody ContractRequest request) {

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
    public ResponseEntity<ContractResponse> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody ContractRequest request) {

        Contract contract = contractMapper.toEntity(request);

        Contract updatedContract =
                contractService.update(id, contract);

        return ResponseEntity.ok(
                contractMapper.toResponse(updatedContract)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(
            @PathVariable Long id) {

        contractService.delete(id);

        return ResponseEntity.noContent().build();
    }
}