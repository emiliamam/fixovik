package ru.rutmiit.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "Дата начала должна быть раньше даты окончания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startDateField() default "startDate";

    String endDateField() default "endDate";
}