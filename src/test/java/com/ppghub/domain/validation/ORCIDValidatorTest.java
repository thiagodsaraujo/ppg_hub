package com.ppghub.domain.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ORCIDValidator.
 * Testa validação de ORCID iD usando o algoritmo ISO 7064 mod 11-2.
 */
@DisplayName("ORCID Validator Tests")
class ORCIDValidatorTest {

    private ORCIDValidator validator;
    private ValidORCID annotation;

    @BeforeEach
    void setUp() {
        validator = new ORCIDValidator();
        // Mock annotation com nullable=true (default)
        annotation = new ValidORCID() {
            @Override
            public String message() { return "ORCID iD inválido"; }
            @Override
            public Class<?>[] groups() { return new Class[0]; }
            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override
            public boolean nullable() { return true; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidORCID.class; }
        };
        validator.initialize(annotation);
    }

    @Test
    @DisplayName("Should accept null when nullable is true")
    void shouldAcceptNullWhenNullable() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    @DisplayName("Should reject null when nullable is false")
    void shouldRejectNullWhenNotNullable() {
        ValidORCID notNullableAnnotation = new ValidORCID() {
            @Override
            public String message() { return "ORCID iD inválido"; }
            @Override
            public Class<?>[] groups() { return new Class[0]; }
            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override
            public boolean nullable() { return false; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidORCID.class; }
        };
        validator.initialize(notNullableAnnotation);

        assertFalse(validator.isValid(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0000-0002-1825-0097",  // ORCID válido (exemplo oficial)
        "0000-0001-5109-3700",  // ORCID válido
        "0000-0002-9227-8514",  // ORCID válido
        "0000-0002-1694-233X",  // ORCID válido com X
        "0000-0003-1527-0030",  // ORCID válido
        "0000-0001-5000-0007"   // ORCID válido
    })
    @DisplayName("Should accept valid ORCIDs")
    void shouldAcceptValidORCIDs(String orcid) {
        assertTrue(validator.isValid(orcid, null),
                "ORCID " + orcid + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0000-0002-1825-0096",  // Checksum incorreto (deveria ser 7)
        "0000-0001-5109-3701",  // Checksum incorreto
        "0000-0002-9227-8515",  // Checksum incorreto
        "0000-0002-1694-2330",  // Checksum incorreto (deveria ser X)
        "0000-0003-1527-0031"   // Checksum incorreto
    })
    @DisplayName("Should reject ORCIDs with invalid checksum")
    void shouldRejectORCIDsWithInvalidChecksum(String orcid) {
        assertFalse(validator.isValid(orcid, null),
                "ORCID " + orcid + " should be invalid (wrong checksum)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0000-0002-1825",       // Muito curto
        "0000-0002-1825-00971", // Muito longo
        "00000002182500097",    // Sem hífens
        "0000-00-1825-0097",    // Formato incorreto
        "000-0002-1825-0097",   // Primeiro bloco com 3 dígitos
        "0000-0002-182-0097",   // Terceiro bloco com 3 dígitos
        ""                       // Vazio
    })
    @DisplayName("Should reject ORCIDs with invalid format")
    void shouldRejectORCIDsWithInvalidFormat(String orcid) {
        assertFalse(validator.isValid(orcid, null),
                "ORCID " + orcid + " should be invalid (wrong format)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "XXXX-XXXX-XXXX-XXXX",  // Apenas X
        "ABCD-EFGH-IJKL-MNOP",  // Letras
        "0000-000A-1825-0097",  // Letra no meio
        "000X-0002-1825-0097"   // X não no final
    })
    @DisplayName("Should reject ORCIDs with invalid characters")
    void shouldRejectORCIDsWithInvalidCharacters(String orcid) {
        assertFalse(validator.isValid(orcid, null),
                "ORCID " + orcid + " should be invalid (invalid characters)");
    }

    @Test
    @DisplayName("Should handle whitespace in ORCID")
    void shouldHandleWhitespace() {
        assertTrue(validator.isValid("  0000-0002-1825-0097  ", null),
                "Should trim whitespace and validate");
        assertTrue(validator.isValid("0000-0002-1825-0097 ", null),
                "Should trim trailing whitespace");
        assertTrue(validator.isValid(" 0000-0002-1825-0097", null),
                "Should trim leading whitespace");
    }

    @Test
    @DisplayName("Should accept valid ORCID ending with X (checksum 10)")
    void shouldAcceptORCIDWithXChecksum() {
        // Este ORCID deve ter checksum X (10)
        assertTrue(validator.isValid("0000-0002-1694-233X", null),
                "ORCID with X checksum should be valid");
    }

    @Test
    @DisplayName("Should reject ORCID with lowercase x")
    void shouldRejectLowercaseX() {
        assertFalse(validator.isValid("0000-0002-1694-233x", null),
                "ORCID with lowercase x should be invalid");
    }
}
