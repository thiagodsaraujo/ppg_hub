package br.edu.ppg.hub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador de CNPJ.
 *
 * Valida o formato e os dígitos verificadores do CNPJ.
 */
public class CNPJValidator implements ConstraintValidator<ValidCNPJ, String> {

    @Override
    public void initialize(ValidCNPJ constraintAnnotation) {
        // Inicialização não necessária
    }

    @Override
    public boolean isValid(String cnpj, ConstraintValidatorContext context) {
        // Null é válido (use @NotNull se quiser obrigatoriedade)
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return true;
        }

        // Remove formatação
        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");

        // Valida tamanho
        if (cnpjLimpo.length() != 14) {
            return false;
        }

        // Valida se não são todos dígitos iguais
        if (cnpjLimpo.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Valida dígitos verificadores
        try {
            // Primeiro dígito verificador
            int soma = 0;
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos1[i];
            }

            int resto = soma % 11;
            int digito1 = resto < 2 ? 0 : 11 - resto;

            if (Character.getNumericValue(cnpjLimpo.charAt(12)) != digito1) {
                return false;
            }

            // Segundo dígito verificador
            soma = 0;
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos2[i];
            }

            resto = soma % 11;
            int digito2 = resto < 2 ? 0 : 11 - resto;

            return Character.getNumericValue(cnpjLimpo.charAt(13)) == digito2;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formata CNPJ para o padrão XX.XXX.XXX/XXXX-XX
     */
    public static String formatarCNPJ(String cnpj) {
        if (cnpj == null) {
            return null;
        }

        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");

        if (cnpjLimpo.length() != 14) {
            return cnpj; // Retorna original se inválido
        }

        return String.format("%s.%s.%s/%s-%s",
                cnpjLimpo.substring(0, 2),
                cnpjLimpo.substring(2, 5),
                cnpjLimpo.substring(5, 8),
                cnpjLimpo.substring(8, 12),
                cnpjLimpo.substring(12, 14));
    }
}
