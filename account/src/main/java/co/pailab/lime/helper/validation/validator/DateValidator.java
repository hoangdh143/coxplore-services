package co.pailab.lime.helper.validation.validator;

import co.pailab.lime.helper.validation.constraint.DateValidationConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;

public class DateValidator implements
        ConstraintValidator<DateValidationConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            sdf.parse(value);

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }
}
