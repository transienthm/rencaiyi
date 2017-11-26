// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.api.resolver;

import com.alibaba.fastjson.JSON;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.exceptions.SimpleException;
import hr.wozai.service.api.result.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常结果统一处理类
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-9-12 下午4:29
 */
public class ExceptionResolver implements HandlerExceptionResolver {

  private static Logger LOGGER = LoggerFactory.getLogger(ExceptionResolver.class);

  @Override
  public ModelAndView resolveException(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    LOGGER.error("request " + request.getContextPath()
                 + " exception, request param:" + JSON.toJSONString(request.getParameterMap()), ex);
    try (PrintWriter writer = response.getWriter()) {
      Result<String> failResult = new Result();
      //规范异常信息，如果是普通的run time exception，返回message "系统异常"
      if (ex instanceof SimpleException) {
        failResult.setMsg(ex.getMessage());
        failResult.setCode(ServiceStatus.COMMON_BAD_REQUEST.getCode());
      } else if (ex instanceof ServiceStatusException) {
        ServiceStatusException statusCodeException = (ServiceStatusException) ex;
        failResult.setMsg(statusCodeException.getServiceStatus().getMsg());
        failResult.setCode(statusCodeException.getServiceStatus().getCode());
      } else {
        failResult.setMsg(ServiceStatus.COMMON_BAD_REQUEST.getMsg());
        failResult.setCode(ServiceStatus.COMMON_BAD_REQUEST.getCode());
      }
      response.setHeader("Content-type", "application/json;charset=UTF-8");
      writer.write(failResult.toJsonString());
      writer.flush();
    } catch (IOException e) {
      LOGGER.error("io exception.", e);
    }
    return null;
  }
}
