package hr.wozai.service.servicecommons.utils.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Created by wangbin on 16/8/2.
 */
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringLengthValidator.class)
public @interface StringLengthConstraint {
    int lengthConstraint() default 140;
    Class<?>[] groups() default {};
    String message() default "字数超出限制";
    Class<? extends Payload>[] payload() default {};
}
