package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractPageResponse;
import de.kirillmrotzek.legalflow.dto.ContractRequest;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.InvalidDateRangeException;
import de.kirillmrotzek.legalflow.exception.InvalidValueRangeException;
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

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    @GetMapping
    public ContractPageResponse getAllContracts(
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) ContractType type,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) String counterparty,
            @RequestParam(required = false) BigDecimal minValue,
            @RequestParam(required = false) BigDecimal maxValue,
            @RequestParam(required = false) LocalDate startDateFrom,
            @RequestParam(required = false) LocalDate startDateTo,
            @RequestParam(required = false) LocalDate endDateFrom,
            @RequestParam(required = false) LocalDate endDateTo,
            Pageable pageable) {

        if (startDateFrom != null
                && startDateTo != null
                && startDateFrom.isAfter(startDateTo)) {
            throw new InvalidDateRangeException(
                    "startDateFrom must be before or equal to startDateTo"
            );
        }

        if (endDateFrom != null
                && endDateTo != null
                && endDateFrom.isAfter(endDateTo)) {
            throw new InvalidDateRangeException(
                    "endDateFrom must be before or equal to endDateTo"
            );
        }

        if (minValue != null
                && maxValue != null
                && minValue.compareTo(maxValue) > 0) {

            throw new InvalidValueRangeException(
                    "minValue must be less than or equal to maxValue"
            );
        }

        Page<Contract> contracts = contractService.search(
                status,
                type,
                riskLevel,
                counterparty,
                minValue,
                maxValue,
                startDateFrom,
                startDateTo,
                endDateFrom,
                endDateTo,
                pageable
        );

        Page<ContractResponse> responsePage =
                contracts.map(contractMapper::toResponse);

        return new ContractPageResponse(
                responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages()
        );
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