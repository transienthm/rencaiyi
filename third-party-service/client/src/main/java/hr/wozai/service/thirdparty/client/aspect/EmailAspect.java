package hr.wozai.service.thirdparty.client.aspect;

import com.alibaba.fastjson.JSON;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.client.bean.EmailContent;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.utils.ParseEmailTempXMLUtils;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import org.apache.ibatis.javassist.*;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/10/26.
 */
@Aspect
@Component
public class EmailAspect {

  @Autowired
  ParseEmailTempXMLUtils parseEmailTempXMLUtils;

 /* @Autowired
  SqsProducer sqsProducer;
*/

  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Around("@annotation(hr.wozai.service.servicecommons.utils.emailtemplate.EmailTemplateApi)")
  public void emailAround(ProceedingJoinPoint joinPoint) {

    Logger LOGGER = LoggerFactory.getLogger(joinPoint.getClass());

    LOGGER.info("emailAround-begin");
    try {
      EmailTemplate emailTemplate = null;
      //形参值
      Object[] args = joinPoint.getArgs();
      for (Object arg : args) {
        if (arg instanceof EmailTemplate) {
          emailTemplate = (EmailTemplate) arg;
        }
      }

      Map<String, Object> xmlResult = parseEmailTempXMLUtils.getParseResult();

      //得到当前emailTemplate 模板信息
      Map<String, Object> emailTemplateMap = (Map) xmlResult.get(emailTemplate.getEmailType());

      String methodName = (String) emailTemplateMap.get("Method");

      //获取形参名
      ClassPool pool = ClassPool.getDefault();
      pool.insertClassPath(new ClassClassPath(this.getClass()));
      CtClass ctClass = pool.getCtClass("hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper");
      CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
      MethodInfo methodInfo = ctMethod.getMethodInfo();
      CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
      LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
      if (attribute == null) {
        ServiceStatusException serviceStatusException = new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        serviceStatusException.setErrInfo("EmailAspect-emailAround()获取形参名错误");
        throw serviceStatusException;
      }

      //形参名数组
      String[] paramNames = new String[ctMethod.getParameterTypes().length];

      int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
      for (int i = 0; i < paramNames.length; i++) {
        paramNames[i] = attribute.variableName(i + pos);
        LOGGER.debug("EmailAspect:paramNames[" + i + "]=" + paramNames[i]);
      }

      String html = (String) emailTemplateMap.get("HTML");
      String subject = (String) emailTemplateMap.get("Subject");
      int posOfDstEmailAddress = -1;

      //替换html及subject中的参数
      for (int i = 0; i < paramNames.length; i++) {
        if (paramNames[i].equals("dstEmailAddress")) {
          posOfDstEmailAddress = i;
        }
        if (!(args[i] instanceof EmailTemplate)) {
          html = html.replaceAll("%" + paramNames[i] + "%", (String) args[i]);
          subject = subject.replaceAll("%" + paramNames[i] + "%", (String) args[i]);
        }
      }

      EmailContent emailContent = new EmailContent();
      emailContent.setHtml(html);
      emailContent.setSubject(subject);
      emailContent.setDstEmailAddress((String) args[posOfDstEmailAddress < 0 ? (args.length - 1) : posOfDstEmailAddress]);

      String emailContentJson = JSON.toJSONString(emailContent);
      LOGGER.info("xml邮件模板：emailContentJson=" + emailContentJson);
      rabbitMQProducer.preSendEmail(emailContentJson);
    } catch (Exception e) {
      LOGGER.error("emailAround()-error", e);
    }

  }
}

