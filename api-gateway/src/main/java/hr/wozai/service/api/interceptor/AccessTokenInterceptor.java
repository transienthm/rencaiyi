// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.interceptor;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/16
 */
public class AccessTokenInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenInterceptor.class);

  public AccessTokenInterceptor() {
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
          throws Exception {


    if (AuthenticationInterceptor.orgId.get() == null && AuthenticationInterceptor.actorUserId.get() == null) {
      LOGGER.error("AccessTokenInterceptor preHandle(): fail, INVALID_TOKEN");
      writeStatusCodeAndMessageInResponse(response, ServiceStatus.AS_INVALID_TOKEN);
      return false;
    }

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) throws Exception {
    AuthenticationInterceptor.actorUserId.set(0L);
    AuthenticationInterceptor.adminUserId.set(0L);
    AuthenticationInterceptor.orgId.set(0L);
  }





  private void writeStatusCodeAndMessageInResponse(HttpServletResponse response, ServiceStatus serviceStatus) {

    if (null == response) {
      return;
    }

    if (null == serviceStatus) {
      serviceStatus = ServiceStatus.COMMON_BAD_REQUEST;
    }

    JSONObject responseBody = new JSONObject();
    responseBody.put("code", serviceStatus.getCode());
    responseBody.put("msg", serviceStatus.getMsg());
    PrintWriter pw = null;
    try {
      pw = response.getWriter();
      pw.print(responseBody);
      pw.flush();
    } catch (Exception e) {
      LOGGER.info("writeStatusCodeAndMessageInResponse(): fail to write response");
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }
}
