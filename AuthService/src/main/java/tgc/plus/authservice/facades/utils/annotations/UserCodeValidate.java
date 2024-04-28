package tgc.plus.authservice.facades.utils.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserCodeValidate implements ConstraintValidator<UserCodeValid, String> {

    private final String userCodeRegex = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(userCodeRegex);
    }
}
