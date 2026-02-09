package ru.rutmiit.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startDateField;
    private String endDateField;
    private String message;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);

        LocalDate startDate = (LocalDate) wrapper.getPropertyValue(startDateField);
        LocalDate endDate = (LocalDate) wrapper.getPropertyValue(endDateField);

        if (startDate == null && endDate == null) {
            return true;
        }

        if (startDate == null || endDate == null) {
            return true;
        }

        boolean isValid = !startDate.isAfter(endDate);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(endDateField)
                    .addConstraintViolation();

        }
        return isValid;
    }
}
