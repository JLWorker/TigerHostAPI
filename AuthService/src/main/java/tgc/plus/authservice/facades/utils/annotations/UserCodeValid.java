package tgc.plus.authservice.facades.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import java.lang.annotation.*;


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UserCodeValidate.class)
public @interface UserCodeValid {
    String message() default "Invalid user code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
