package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    public List<Contract> getAllContracts() {
        return contractService.findAll();
    }

    @PostMapping
    public Contract createContract(@RequestBody Contract contract) {
        return contractService.save(contract);
    }
}
