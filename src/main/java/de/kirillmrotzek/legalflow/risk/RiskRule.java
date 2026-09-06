package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.model.Contract;

import java.util.Optional;

public interface RiskRule {

    Optional<RiskFactor> evaluate(Contract contract);
}
