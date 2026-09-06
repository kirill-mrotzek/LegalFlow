package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.enums.GoverningLawCategory;
import de.kirillmrotzek.legalflow.reference.EUCountryRegistry;
import org.springframework.stereotype.Component;

@Component
public class GoverningLawClassifier {

    private final EUCountryRegistry euCountryRegistry;

    public GoverningLawClassifier(EUCountryRegistry euCountryRegistry) {
        this.euCountryRegistry = euCountryRegistry;
    }

    public GoverningLawCategory classify(String governingLaw) {

        if (governingLaw == null || governingLaw.isBlank()) {
            return GoverningLawCategory.UNKNOWN;
        }

        String countryVariant = governingLaw
                .replaceAll("(?i)\\s+law$", "")
                .trim();

        if (countryVariant.equals("Germany")
                || countryVariant.equals("German")) {
            return GoverningLawCategory.GERMAN;
        }

        if (euCountryRegistry.isEuMember(countryVariant)) {
            return GoverningLawCategory.EU;
        }

        return GoverningLawCategory.NON_EU;
    }
}