package br.edu.ppg.hub.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador de código de instituição.
 *
 * Valida se o código contém apenas caracteres alfanuméricos, underscore e hífen.
 */
public class CodigoValidator implements ConstraintValidator<ValidCodigo, String> {

    private static final String CODIGO_PATTERN = "^[A-Za-z0-9_-]+$";

    @Override
    public void initialize(ValidCodigo constraintAnnotation) {
        // Inicialização não necessária
    }

    @Override
    public boolean isValid(String codigo, ConstraintValidatorContext context) {
        // Null é válido (use @NotNull se quiser obrigatoriedade)
        if (codigo == null || codigo.trim().isEmpty()) {
            return true;
        }

        // Remove espaços
        String codigoLimpo = codigo.trim();

        // Valida padrão
        return codigoLimpo.matches(CODIGO_PATTERN);
    }
}
