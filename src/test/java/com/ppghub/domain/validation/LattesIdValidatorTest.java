package com.ppghub.domain.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para LattesIdValidator.
 * Testa validação de Lattes CV ID (16 dígitos numéricos).
 */
@DisplayName("Lattes ID Validator Tests")
class LattesIdValidatorTest {

    private LattesIdValidator validator;
    private ValidLattesId annotation;

    @BeforeEach
    void setUp() {
        validator = new LattesIdValidator();
        // Mock annotation com nullable=true (default)
        annotation = new ValidLattesId() {
            @Override
            public String message() { return "Lattes ID inválido"; }
            @Override
            public Class<?>[] groups() { return new Class[0]; }
            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override
            public boolean nullable() { return true; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidLattesId.class; }
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
        ValidLattesId notNullableAnnotation = new ValidLattesId() {
            @Override
            public String message() { return "Lattes ID inválido"; }
            @Override
            public Class<?>[] groups() { return new Class[0]; }
            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override
            public boolean nullable() { return false; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidLattesId.class; }
        };
        validator.initialize(notNullableAnnotation);

        assertFalse(validator.isValid(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234567890123456",  // 16 dígitos válidos
        "0000000000000000",  // Zeros também são válidos
        "9999999999999999",  // Noves também são válidos
        "1234567812345678",  // Padrão repetido
        "0123456789012345"   // Sequência numérica
    })
    @DisplayName("Should accept valid Lattes IDs")
    void shouldAcceptValidLattesIds(String lattesId) {
        assertTrue(validator.isValid(lattesId, null),
                "Lattes ID " + lattesId + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789012345",     // 15 dígitos (falta 1)
        "12345678901234567",   // 17 dígitos (sobra 1)
        "123456789012",        // 12 dígitos
        "12345",               // 5 dígitos
        ""                      // Vazio
    })
    @DisplayName("Should reject Lattes IDs with invalid length")
    void shouldRejectLattesIdsWithInvalidLength(String lattesId) {
        assertFalse(validator.isValid(lattesId, null),
                "Lattes ID " + lattesId + " should be invalid (wrong length)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789012345A",    // Letra no final
        "A234567890123456",    // Letra no início
        "123456789O123456",    // Letra no meio (O em vez de 0)
        "ABCDEFGHIJKLMNOP",    // Apenas letras
        "123456-7890-12345",   // Com hífen
        "123 456 789 012 345"  // Com espaços
    })
    @DisplayName("Should reject Lattes IDs with non-numeric characters")
    void shouldRejectLattesIdsWithNonNumericChars(String lattesId) {
        assertFalse(validator.isValid(lattesId, null),
                "Lattes ID " + lattesId + " should be invalid (contains non-numeric)");
    }

    @Test
    @DisplayName("Should handle whitespace in Lattes ID")
    void shouldHandleWhitespace() {
        assertTrue(validator.isValid("  1234567890123456  ", null),
                "Should trim whitespace and validate");
        assertTrue(validator.isValid("1234567890123456 ", null),
                "Should trim trailing whitespace");
        assertTrue(validator.isValid(" 1234567890123456", null),
                "Should trim leading whitespace");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12345678901234567890",  // Muito longo
        "1",                      // Um dígito
        "12",                     // Dois dígitos
        "123",                    // Três dígitos
        "1234567890"              // Dez dígitos
    })
    @DisplayName("Should reject Lattes IDs of various invalid lengths")
    void shouldRejectVariousInvalidLengths(String lattesId) {
        assertFalse(validator.isValid(lattesId, null),
                "Lattes ID " + lattesId + " should be invalid (length " + lattesId.trim().length() + ")");
    }
}
