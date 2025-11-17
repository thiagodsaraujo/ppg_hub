package com.ppghub.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotação para validação de Lattes ID (Currículo Lattes).
 * Valida formato do ID numérico de 16 dígitos.
 *
 * Formato: 1234567890123456
 *
 * Uso:
 * <pre>
 * {@code
 * @ValidLattesId
 * private String lattesId;
 * }
 * </pre>
 */
@Documented
@Constraint(validatedBy = LattesIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLattesId {

    String message() default "Lattes ID inválido. Deve conter 16 dígitos numéricos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Se true, aceita null como válido (use com @NotNull separadamente se necessário).
     */
    boolean nullable() default true;
}
