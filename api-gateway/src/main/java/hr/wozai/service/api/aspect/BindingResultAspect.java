package hr.wozai.service.api.aspect;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.review.ReviewTemplateVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import hr.wozai.service.servicecommons.utils.validator.StringLengthValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin on 16/8/3.
 */
@Component
@Aspect
public class BindingResultAspect {

    public static final Logger LOGGER = LoggerFactory.getLogger(BindingResultAspect.class);
    @Around("@annotation( hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.debug("BindingResultAspect正在工作中");
        BindingResult bindingResult = null;
        Result result = new Result();
        Object[] args = joinPoint.getArgs();

        for (Object obj : args) {
            if (obj instanceof BindingResult) {
                bindingResult = (BindingResult) obj;
            }
        }

        if (bindingResult != null && bindingResult.hasErrors()) {
            LOGGER.debug("发现数据未通过校验,正在处理中");
            List<ObjectError> errors = bindingResult.getAllErrors();
            if (errors.size() > 0) {
                StringBuilder msg = new StringBuilder();
                if (errors.size() > 1) {
                    for (int i = 0; i < errors.size(); i++) {
                        msg.append((i + 1) + ": " + errors.get(i).getDefaultMessage() + " ");
                    }
                } else {
                    msg.append(errors.get(0).getDefaultMessage());
                }
                LOGGER.debug("校验结果为:" + msg.toString());
                result.setErrorInfo(msg.toString());
                result.setCodeAndMsg(ServiceStatus.COMMON_STRING_VALIDATE_FAIL);
                return result;
            }
        } else {
            Signature signature = joinPoint.getSignature();
            LOGGER.debug("该接口" + signature.getName() + "的请求数据整体通过校验:");

        }

        return joinPoint.proceed();
    }

}
