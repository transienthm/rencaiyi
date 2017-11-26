// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.logging;

import com.alibaba.fastjson.JSONObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.uuid.UUIDGenerator;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-16
 */
@Aspect
@Component
public class LoggingAspect {

  private enum LogPoint {

    REQUEST(1, "REQUEST"),
    RESPONSE(2, "RESPONSE"),
    EXCEPTION(3, "EXCEPTION"),
    ;

    private int code;
    private String desc;

    private LogPoint(int code, String desc) {
      this.code = code;
      this.desc = desc;
    }
  }

  private static String LOG_APPTIME = "apptime";
  private static String LOG_UUID = "uuid";
  private static String LOG_HOST = "host";
  private static String LOG_SERVICE = "service";
  private static String LOG_CLASS = "class";
  private static String LOG_METHOD = "method";
  private static String LOG_POINT = "point";
  private static String LOG_PARAMS = "params";

  private static String PARAMS_INTERVAL = "interval";
  private static String PARAMS_RETURN = "return";
  private static String PARAMS_EXCEPTION = "exception";
  private static String PARAMS_STACKTRACE = "stacktrace";
  private static String PARAMS_RAW_STACKTRACE = "raw_stacktrace";


  private String localIpAddress = null;

  private SimpleDateFormat sdf = null;

  @Value("${service.name}")
  private String serviceName;

  @PostConstruct
  public void init() throws UnknownHostException {
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    InetAddress inetAddress = InetAddress.getLocalHost();
    localIpAddress = inetAddress.getHostAddress();
  }

  /**
   * Include:
   *  1) arg list
   *  2) any exception
   *  3) return value
   *  4) interval
   *
   * @param joinPoint
   * @return
   */
  @Around("@annotation(hr.wozai.service.servicecommons.utils.logging.LogAround)")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

    Logger LOGGER = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
    String logUUID = UUIDGenerator.generateCanonicalRandomKey();
    long entryTime = TimeUtils.getNowTimestmapInMillis();

    // before
    JSONObject requestLog = new JSONObject();
    Signature signature = joinPoint.getSignature();
    String methodName = signature.getName();
    setCommonFieldsInLog(requestLog, logUUID, localIpAddress, serviceName,
                         joinPoint.getTarget().getClass(), methodName, LogPoint.REQUEST.code);
    Object[] args = joinPoint.getArgs();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    List<String> paramNames = getParameterNames(method);
    Map<String, Object> requestParams = new HashMap<>();
    for (int i = 0; i < args.length; i++) {
      requestParams.put(paramNames.get(i), args[i] == null?"NULL":args[i].toString());
    }
    requestLog.put(LOG_PARAMS, requestParams);
    LOGGER.info(requestLog.toString());

    // proceed
    Object ret = null;
    try {
      ret = joinPoint.proceed();
    } catch (Throwable throwable) {
      String throwableToString = throwable.toString();
      String rawStacktrace = null;
      StackTraceElement[] stackTraceElements = null;
      if (throwable instanceof ServiceStatusException) {
        ServiceStatusException sse = (ServiceStatusException) throwable;
        stackTraceElements = sse.getStackTrace();
        throwableToString = sse.toString();
        StringWriter errors = new StringWriter();
        sse.printStackTrace(new PrintWriter(errors));
        rawStacktrace = errors.toString();
      }
      JSONObject exceptionLog = new JSONObject();
      setCommonFieldsInLog(exceptionLog, logUUID, localIpAddress, serviceName,
                           joinPoint.getTarget().getClass(), methodName, LogPoint.EXCEPTION.code);
      Map<String, Object> exceptionParams = new HashMap<>();
      exceptionParams.put(PARAMS_EXCEPTION, throwableToString);
      exceptionParams.put(PARAMS_STACKTRACE, stackTraceElements);
      exceptionParams.put(PARAMS_RAW_STACKTRACE, rawStacktrace);
      exceptionLog.put(LOG_PARAMS, exceptionParams);
      LOGGER.error(exceptionLog.toString());
      throw throwable;
    }

    // after
    JSONObject responseLog = new JSONObject();
    setCommonFieldsInLog(responseLog, logUUID, localIpAddress, serviceName,
                         joinPoint.getTarget().getClass(), methodName, LogPoint.RESPONSE.code);
    Map<String, Object> responseParams = new HashMap<>();
    responseParams.put(PARAMS_INTERVAL, TimeUtils.getNowTimestmapInMillis() - entryTime);

    responseParams.put(PARAMS_RETURN, ret == null ? "null" : ret.toString());

    responseLog.put(LOG_PARAMS, responseParams);
    LOGGER.info(responseLog.toString());

    return ret;
  }

  public static List<String> getParameterNames(Method method) {
    Parameter[] parameters = method.getParameters();
    List<String> parameterNames = new ArrayList<>();

    for (Parameter parameter : parameters) {
      // TODO: validate
//      if(!parameter.isNamePresent()) {
//        throw new IllegalArgumentException("Parameter names are not present!");
//      }
      String parameterName = parameter.getName();
      parameterNames.add(parameterName);
    }

    return parameterNames;
  }

  public void setCommonFieldsInLog(
      JSONObject jsonObject, String logUUID, String localIpAddress,
      String serviceName, Class theClass, String methodName, int logPoint) {
    if (null == jsonObject) {
      return;
    }
    jsonObject.put(LOG_APPTIME, sdf.format(TimeUtils.getNowTimestmapInMillis()));
    jsonObject.put(LOG_UUID, logUUID);
    jsonObject.put(LOG_HOST, localIpAddress);
    jsonObject.put(LOG_SERVICE, serviceName);
    jsonObject.put(LOG_CLASS, theClass);
    jsonObject.put(LOG_METHOD, methodName);
    jsonObject.put(LOG_POINT, logPoint);
  }
}

