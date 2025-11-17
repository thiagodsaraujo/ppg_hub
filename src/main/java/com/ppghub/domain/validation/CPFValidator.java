package com.ppghub.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador de CPF brasileiro.
 * Valida formato (11 dígitos) e dígitos verificadores.
 */
public class CPFValidator implements ConstraintValidator<ValidCPF, String> {

    private boolean nullable;

    @Override
    public void initialize(ValidCPF constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Se é nullable e o valor é null, é válido
        if (nullable && cpf == null) {
            return true;
        }

        // Se não é nullable e o valor é null, é inválido
        if (cpf == null) {
            return false;
        }

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Valida tamanho
        if (cpf.length() != 11) {
            return false;
        }

        // Valida CPFs conhecidos como inválidos (todos os dígitos iguais)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Valida primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }

        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
            return false;
        }

        // Valida segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }

        return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }
}
