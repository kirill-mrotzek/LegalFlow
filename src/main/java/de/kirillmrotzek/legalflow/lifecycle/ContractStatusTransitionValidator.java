package de.kirillmrotzek.legalflow.lifecycle;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class ContractStatusTransitionValidator {

    private final Map<ContractStatus, Set<ContractStatus>> allowedTransitions =
            new EnumMap<>(ContractStatus.class);

    public ContractStatusTransitionValidator() {

        allowedTransitions.put(
                ContractStatus.DRAFT,
                EnumSet.of(ContractStatus.SIGNED)
        );

        allowedTransitions.put(
                ContractStatus.SIGNED,
                EnumSet.of(ContractStatus.ACTIVE)
        );

        allowedTransitions.put(
                ContractStatus.ACTIVE,
                EnumSet.of(
                        ContractStatus.EXPIRED,
                        ContractStatus.TERMINATED
                )
        );

        allowedTransitions.put(
                ContractStatus.EXPIRED,
                EnumSet.of(ContractStatus.ARCHIVED)
        );

        allowedTransitions.put(
                ContractStatus.TERMINATED,
                EnumSet.of(ContractStatus.ARCHIVED)
        );

        allowedTransitions.put(
                ContractStatus.ARCHIVED,
                EnumSet.noneOf(ContractStatus.class)
        );
    }

    public boolean isAllowed(
            ContractStatus currentStatus,
            ContractStatus targetStatus
    ) {
        return allowedTransitions
                .getOrDefault(currentStatus, Set.of())
                .contains(targetStatus);
    }
}
