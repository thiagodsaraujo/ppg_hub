package com.ppghub.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotação para validação de CPF brasileiro.
 * Valida formato e dígitos verificadores.
 *
 * Uso:
 * <pre>
 * {@code
 * @ValidCPF
 * private String cpf;
 * }
 * </pre>
 */
@Documented
@Constraint(validatedBy = CPFValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCPF {

    String message() default "CPF inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Se true, aceita null como válido (use com @NotNull separadamente se necessário).
     */
    boolean nullable() default true;
}
