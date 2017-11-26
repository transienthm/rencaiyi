package hr.wozai.service.servicecommons.utils.validator;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wangbin on 16/8/2.
 */
public class StringLengthValidator implements ConstraintValidator<StringLengthConstraint, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringLengthValidator.class);

    private int lengthConstraint;

    private StringLengthConstraint stringLengthConstraint;

    @Override
    public void initialize(StringLengthConstraint constraintAnnotation) {

        LOGGER.info("校验类StringLengthValidator初始化中");
        lengthConstraint = constraintAnnotation.lengthConstraint();
        stringLengthConstraint = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean isValid = false;
        String value = null;
        LOGGER.info("StringLengthValidator.isValid()");
        if (null == object) {
            return true;
        }
        if (object instanceof String ){
            value = (String) object;
            LOGGER.info("校验参数为String,校验值为:" + value);
            isValid = validate(value, lengthConstraint);
        } else if (object instanceof Collection) {
            LOGGER.info("校验参数为Collection");
            List strList = new ArrayList((Collection)object);
            for (Object obj : strList) {
                isValid = true;
                if (obj instanceof String) {
                    value = (String) obj;
                    LOGGER.info("校验值为:" + value);
                    if (!validate(value, lengthConstraint)) {
                        isValid = false;
                        break;
                    }
                }
            }
        } else if (object instanceof JSONObject) {
            LOGGER.info("检验参数为JSONObject");
            JSONObject jsonObject = (JSONObject) object;
            value = (String) jsonObject.get("content");
            isValid = validate(value, lengthConstraint);
        } else {
            LOGGER.info("新的校验类型:" + object.getClass().getName());

        }

        if (!isValid) {
            LOGGER.info("isValid()-发现违反限制字段");
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("字数超限，不能超过" + lengthConstraint + "个汉字").addConstraintViolation();
        }
        return isValid;
    }

    public static Boolean validate(String value, int lengthConstraint) {
        boolean isValid;
        LOGGER.info("须校验的字段为:" + value);
        int result = countLengthOfString(value);
        if (result <= lengthConstraint) {
            LOGGER.info("字数为:" + result + "个汉字,符合限制字数:" + lengthConstraint);
            LOGGER.info("验证通过");
            isValid = true;
        } else {
            LOGGER.info("字数为:" + result + "个汉字,超出限制字数:" + lengthConstraint);
            LOGGER.info("验证失败");
            isValid = false;
        }
        LOGGER.info("验证结果:" + isValid);
        return isValid;
    }

    public static int countLengthOfString(String value) {
        //字符数
        int count = 0;
        //换算为多少个汉字
        int result;
        for (int i = 0; i < value.length(); i++) {
            char codePoint = value.charAt(i);
            if (codePoint >= 0 && codePoint <= 255) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        int remainder = count % 2;
        result = count / 2;
        if (remainder != 0) {
            result = result + 1;
        }
        return result;
    }
}
