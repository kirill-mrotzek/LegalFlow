package de.kirillmrotzek.legalflow.exception;

import de.kirillmrotzek.legalflow.enums.ContractStatus;

public class InvalidContractStatusTransitionException
        extends RuntimeException {

    public InvalidContractStatusTransitionException(
            ContractStatus currentStatus,
            ContractStatus targetStatus
    ) {
        super(
                "Invalid contract status transition: "
                        + currentStatus
                        + " -> "
                        + targetStatus
        );
    }
}
