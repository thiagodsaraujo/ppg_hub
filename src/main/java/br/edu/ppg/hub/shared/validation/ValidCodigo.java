package br.edu.ppg.hub.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation para validação de código de instituição.
 *
 * Valida se o código contém apenas caracteres alfanuméricos, underscore e hífen.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodigoValidator.class)
@Documented
public @interface ValidCodigo {

    String message() default "Código deve conter apenas letras, números, _ ou -";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
