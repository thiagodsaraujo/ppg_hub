package com.ppghub.domain.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CPFValidator.
 */
@DisplayName("CPF Validator Tests")
class CPFValidatorTest {

    private CPFValidator validator;
    private ValidCPF annotation;

    @BeforeEach
    void setUp() {
        validator = new CPFValidator();
        // Mock annotation com nullable=true (default)
        annotation = new ValidCPF() {
            @Override
            public String message() { return "CPF inválido"; }
            @Override
            public Class<?>[] groups() { return new Class[0]; }
            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override
            public boolean nullable() { return true; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidCPF.class; }
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
        ValidCPF notNullableAnnotation = new ValidCPF() {
            @Override
            public String message() { return "CPF inválido"; }
            @Override
            public Class<?>[] groups() { return new Class[0]; }
            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            @Override
            public boolean nullable() { return false; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidCPF.class; }
        };
        validator.initialize(notNullableAnnotation);

        assertFalse(validator.isValid(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "11144477735",      // CPF válido sem formatação
        "111.444.777-35",   // CPF válido com formatação
        "12345678909",      // CPF válido
        "529.982.247-25",   // CPF válido formatado
        "52998224725"       // CPF válido sem formatação
    })
    @DisplayName("Should accept valid CPFs")
    void shouldAcceptValidCPFs(String cpf) {
        assertTrue(validator.isValid(cpf, null),
                "CPF " + cpf + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "00000000000",      // Todos os dígitos iguais
        "11111111111",      // Todos os dígitos iguais
        "22222222222",      // Todos os dígitos iguais
        "33333333333",      // Todos os dígitos iguais
        "44444444444",      // Todos os dígitos iguais
        "55555555555",      // Todos os dígitos iguais
        "66666666666",      // Todos os dígitos iguais
        "77777777777",      // Todos os dígitos iguais
        "88888888888",      // Todos os dígitos iguais
        "99999999999"       // Todos os dígitos iguais
    })
    @DisplayName("Should reject CPFs with all same digits")
    void shouldRejectCPFsWithAllSameDigits(String cpf) {
        assertFalse(validator.isValid(cpf, null),
                "CPF " + cpf + " should be invalid (all same digits)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123",              // Muito curto
        "123456789012345", // Muito longo
        "1234567890",      // 10 dígitos (falta 1)
        "123456789012",    // 12 dígitos (sobra 1)
        ""                  // Vazio
    })
    @DisplayName("Should reject CPFs with invalid length")
    void shouldRejectCPFsWithInvalidLength(String cpf) {
        assertFalse(validator.isValid(cpf, null),
                "CPF " + cpf + " should be invalid (wrong length)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12345678901",      // Dígitos verificadores inválidos
        "11144477736",      // Segundo dígito errado
        "11144477725",      // Primeiro dígito errado
        "123.456.789-00"    // Dígitos verificadores inválidos formatado
    })
    @DisplayName("Should reject CPFs with invalid check digits")
    void shouldRejectCPFsWithInvalidCheckDigits(String cpf) {
        assertFalse(validator.isValid(cpf, null),
                "CPF " + cpf + " should be invalid (wrong check digits)");
    }

    @Test
    @DisplayName("Should handle CPF with mixed formatting correctly")
    void shouldHandleMixedFormatting() {
        assertTrue(validator.isValid("111.444.777-35", null));
        assertTrue(validator.isValid("11144477735", null));
        assertTrue(validator.isValid("111-444-777-35", null)); // Remove todos os não-numéricos
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ABC.DEF.GHI-JK",   // Letras
        "111.444.777-XX",   // Letras no final
        "aaa44477735"       // Letras no início
    })
    @DisplayName("Should reject CPFs with non-numeric characters after cleaning")
    void shouldRejectCPFsWithNonNumericChars(String cpf) {
        assertFalse(validator.isValid(cpf, null),
                "CPF " + cpf + " should be invalid (contains letters)");
    }
}
