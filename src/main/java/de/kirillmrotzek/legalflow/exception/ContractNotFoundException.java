package de.kirillmrotzek.legalflow.exception;

public class ContractNotFoundException extends RuntimeException{
    public ContractNotFoundException(Long id) {
        super("Contract with id " + id + " not found");
    }
}
