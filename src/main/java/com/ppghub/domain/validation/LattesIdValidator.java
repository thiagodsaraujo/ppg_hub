package com.ppghub.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador de Lattes ID (Currículo Lattes).
 * Valida formato do ID numérico de 16 dígitos.
 */
public class LattesIdValidator implements ConstraintValidator<ValidLattesId, String> {

    private static final Pattern LATTES_ID_PATTERN = Pattern.compile("^\\d{16}$");
    private boolean nullable;

    @Override
    public void initialize(ValidLattesId constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String lattesId, ConstraintValidatorContext context) {
        // Se é nullable e o valor é null, é válido
        if (nullable && lattesId == null) {
            return true;
        }

        // Se não é nullable e o valor é null, é inválido
        if (lattesId == null) {
            return false;
        }

        // Remove espaços em branco
        lattesId = lattesId.trim();

        // Valida formato (16 dígitos numéricos)
        return LATTES_ID_PATTERN.matcher(lattesId).matches();
    }
}
