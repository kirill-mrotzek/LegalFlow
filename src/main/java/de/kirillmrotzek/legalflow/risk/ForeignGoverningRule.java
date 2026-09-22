package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.enums.GoverningLawCategory;
import de.kirillmrotzek.legalflow.model.Contract;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ForeignGoverningRule implements RiskRule {

    private final GoverningLawClassifier governingLawClassifier;

    public ForeignGoverningRule(GoverningLawClassifier governingLawClassifier) {
        this.governingLawClassifier = governingLawClassifier;
    }

    @Override
    public Optional<RiskFactor> evaluate(Contract contract) {

        GoverningLawCategory category =
                governingLawClassifier.classify(contract.getGoverningLaw());

        return switch (category) {

            case GERMAN, UNKNOWN ->
                    Optional.empty();

            case EU ->
                    Optional.of(
                            new RiskFactor(
                                    "FOREIGN_GOVERNING_LAW_EU",
                                    10,
                                    "Contract is governed by the law of an EU Member State",
                                    "EU governing law may require additional legal review of applicable foreign law"
                            )
                    );

            case NON_EU ->
                    Optional.of(
                            new RiskFactor(
                                    "FOREIGN_GOVERNING_LAW_NON_EU",
                                    20,
                                    "Contract is governed by the law of a non-EU country",
                                    "Non-EU governing law increases legal complexity and potential enforcement risk"
                            )
                    );
        };
    }
}
