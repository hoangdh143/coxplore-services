package co.pailab.lime.helper.validation.validator;

import co.pailab.lime.helper.validation.constraint.XssValidationConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class XssValidator implements
        ConstraintValidator<XssValidationConstraint, String> {
    @Override
    public void initialize(XssValidationConstraint validationString) {
    }

    @Override
    public boolean isValid(String validationString, ConstraintValidatorContext cxt) {
        if (validationString == null) return true;
//        String value1 = validationString.replaceAll("<", "& lt;");
//        String value11 = validationString.replaceAll(">", "& gt;");

        String value2 = validationString.replaceAll("\\(", "& #40;");
        String value12 = value2.replaceAll("\\)", "& #41;");
        String value3 = value12.replaceAll("'", "& #39;");
        String value4 = value3.replaceAll("eval\\((.*)\\)", "");
        String value5 = value4.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");

        String value6 = value5.replaceAll("(?i)<script.*?>.*?<script.*?>", "");
        String value7 = value6.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
        String sanitizedValue = value7.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
//        String sanitizedValue = value8.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");

        return sanitizedValue.equals(validationString);
    }
}
