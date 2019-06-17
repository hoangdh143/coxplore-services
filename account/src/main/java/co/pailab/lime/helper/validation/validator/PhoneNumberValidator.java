package co.pailab.lime.helper.validation.validator;

import co.pailab.lime.helper.validation.constraint.PhoneValidationConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements
        ConstraintValidator<PhoneValidationConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.matches("(^$|[0-9]{10,12})");
    }
}
