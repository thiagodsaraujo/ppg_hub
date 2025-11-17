package com.ppghub.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotação para validação de ORCID iD.
 * Valida formato (XXXX-XXXX-XXXX-XXXX) e dígito verificador.
 *
 * ORCID iD format: 0000-0002-1825-0097
 * - 16 dígitos separados por hífens
 * - Último dígito é um checksum (pode ser X)
 *
 * Uso:
 * <pre>
 * {@code
 * @ValidORCID
 * private String orcid;
 * }
 * </pre>
 */
@Documented
@Constraint(validatedBy = ORCIDValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidORCID {

    String message() default "ORCID iD inválido. Formato esperado: XXXX-XXXX-XXXX-XXXX";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Se true, aceita null como válido (use com @NotNull separadamente se necessário).
     */
    boolean nullable() default true;
}
