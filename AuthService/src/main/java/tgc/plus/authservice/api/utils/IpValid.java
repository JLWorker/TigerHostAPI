package tgc.plus.authservice.api.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = IpAddressValidate.class)
public @interface IpValid {

    String message() default "Invalid ip address";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
