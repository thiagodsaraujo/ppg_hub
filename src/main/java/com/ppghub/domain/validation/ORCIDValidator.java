package com.ppghub.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador de ORCID iD.
 * Valida formato (XXXX-XXXX-XXXX-XXXX) e dígito verificador usando algoritmo ISO 7064 mod 11-2.
 *
 * Referência: https://support.orcid.org/hc/en-us/articles/360006897674-Structure-of-the-ORCID-Identifier
 */
public class ORCIDValidator implements ConstraintValidator<ValidORCID, String> {

    private static final Pattern ORCID_PATTERN = Pattern.compile("^\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$");
    private boolean nullable;

    @Override
    public void initialize(ValidORCID constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String orcid, ConstraintValidatorContext context) {
        // Se é nullable e o valor é null, é válido
        if (nullable && orcid == null) {
            return true;
        }

        // Se não é nullable e o valor é null, é inválido
        if (orcid == null) {
            return false;
        }

        // Remove espaços em branco
        orcid = orcid.trim();

        // Valida formato básico
        if (!ORCID_PATTERN.matcher(orcid).matches()) {
            return false;
        }

        // Remove hífens para validação do checksum
        String digits = orcid.replace("-", "");

        // Calcula checksum usando ISO 7064 mod 11-2
        int total = 0;
        for (int i = 0; i < digits.length() - 1; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            total = (total + digit) * 2;
        }

        int remainder = total % 11;
        int checkDigit = (12 - remainder) % 11;

        // Último dígito pode ser 0-9 ou X (representa 10)
        char lastChar = digits.charAt(digits.length() - 1);
        int expectedCheckDigit = (lastChar == 'X') ? 10 : Character.getNumericValue(lastChar);

        return checkDigit == expectedCheckDigit;
    }
}
