package de.kirillmrotzek.legalflow.reference;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EUCountryRegistry {

    private static final Set<String> EU_COUNTRIES = Set.of(
            "Austria",
            "Austrian",
            "Belgium",
            "Belgian",
            "Bulgaria",
            "Bulgarian",
            "Croatia",
            "Croatian",
            "Cyprus",
            "Cypriot",
            "Czech Republic",
            "Czech",
            "Denmark",
            "Danish",
            "Estonia",
            "Estonian",
            "Finland",
            "Finnish",
            "France",
            "French",
            "Germany",
            "German",
            "Greece",
            "Greek",
            "Hungary",
            "Hungarian",
            "Ireland",
            "Irish",
            "Italy",
            "Italian",
            "Latvia",
            "Latvian",
            "Lithuania",
            "Lithuanian",
            "Luxembourg",
            "Luxembourgish",
            "Malta",
            "Maltese",
            "Netherlands",
            "Dutch",
            "Poland",
            "Polish",
            "Portugal",
            "Portuguese",
            "Romania",
            "Romanian",
            "Slovakia",
            "Slovak",
            "Slovenia",
            "Slovenian",
            "Spain",
            "Spanish",
            "Sweden",
            "Swedish"
    );

    public boolean isEuMember(String countryVariant) {
        return EU_COUNTRIES.contains(countryVariant);
    }
}
