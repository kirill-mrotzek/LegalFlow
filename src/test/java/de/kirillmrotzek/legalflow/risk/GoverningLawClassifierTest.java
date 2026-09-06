package de.kirillmrotzek.legalflow.risk;

import de.kirillmrotzek.legalflow.enums.GoverningLawCategory;
import de.kirillmrotzek.legalflow.reference.EUCountryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoverningLawClassifierTest {

    private GoverningLawClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new GoverningLawClassifier(
                new EUCountryRegistry()
        );
    }

    @Test
    void classify_shouldReturnUnknown_whenGoverningLawIsNull() {

        GoverningLawCategory result = classifier.classify(null);

        assertEquals(GoverningLawCategory.UNKNOWN, result);
    }

    @Test
    void classify_shouldReturnUnknown_whenGoverningLawIsBlank() {

        GoverningLawCategory result = classifier.classify("   ");

        assertEquals(GoverningLawCategory.UNKNOWN, result);
    }

    @Test
    void classify_shouldReturnGerman_whenGoverningLawIsGerman() {

        GoverningLawCategory result = classifier.classify("German law");

        assertEquals(GoverningLawCategory.GERMAN, result);
    }

    @Test
    void classify_shouldReturnGerman_whenGoverningLawIsGermany() {

        GoverningLawCategory result = classifier.classify("Germany");

        assertEquals(GoverningLawCategory.GERMAN, result);
    }

    @Test
    void classify_shouldReturnEu_whenGoverningLawIsFrench() {

        GoverningLawCategory result = classifier.classify("French law");

        assertEquals(GoverningLawCategory.EU, result);
    }

    @Test
    void classify_shouldReturnNonEu_whenGoverningLawIsSwiss() {

        GoverningLawCategory result = classifier.classify("Swiss law");

        assertEquals(GoverningLawCategory.NON_EU, result);
    }

}
