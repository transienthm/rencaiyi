package hr.wozai.service.thirdparty.client.utils;

import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import javassist.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by wangbin on 2016/10/14.
 */
@Component
public class ParseEmailTempXMLUtils {

  private static Logger LOGGER = LoggerFactory.getLogger(ParseEmailTempXMLUtils.class);

  //key = EmailTemplateApi.getEmailType value = 包含全内容Map
  private Map<String,Object> parseResult;

  public Map<String, Object> getParseResult() {
    return parseResult;
  }

  public ParseEmailTempXMLUtils(){

  }

  @PostConstruct
  public void initMap() {
    parseResult = new HashMap();

    String templateName = null;
    try {
      SAXReader sax = new SAXReader();
      Document document = sax.read(this.getClass().getClassLoader().getResourceAsStream("email/emailTemplate.xml"));
      Element root = document.getRootElement();
      List<Element> emailTemplates = root.elements("EmailTemplate");

      //遍历list，解析每一个<EmailTemplate>标签，存入Map中
      Iterator iterator;
      for (iterator = emailTemplates.iterator(); iterator.hasNext();) {
        Map perTemplate = new HashMap();
        Element emailTemplate = (Element) iterator.next();
        List<Element> elementsInEmailTemplate = emailTemplate.elements();
        //得到模板名
        if (emailTemplate.getName().equals("EmailTemplate")) {
          Attribute emailTemplateName = emailTemplate.attribute("name");
          templateName = emailTemplateName.getValue();
          LOGGER.debug("emailTemplateMap: key =" + emailTemplate.getName() + " value=" + templateName);
        }

        for (Element elementInEmailTemplate : elementsInEmailTemplate) {

          //得到parameterList
          if (elementInEmailTemplate.getName().equals("Parameters")) {
            List childList = elementInEmailTemplate.elements();
            List parameters = new ArrayList();
            for (Object obj : childList) {
              Element param = (Element) obj;
              parameters.add(param.getText());
            }
            perTemplate.put(elementInEmailTemplate.getName(), parameters);
            continue;
          }
          perTemplate.put(elementInEmailTemplate.getName(), elementInEmailTemplate.getText());
        }

        parseResult.put(templateName, perTemplate);
      }

      LOGGER.debug("XMLParseResult=" + parseResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Object getParameterObject(EmailTemplate emailTemplate) throws Exception{
    ClassPool pool = ClassPool.getDefault();
    String newClassName = "hr.wozai.service.thirdparty.client.utils.EmailTemplateParameters";
    CtClass ctClass = pool.makeClass(newClassName);

    Map targetMap = (HashMap) parseResult.get(emailTemplate.getEmailType());
    List<String> parameters = (ArrayList<String>)targetMap.get("Parameters");
    LOGGER.debug("getParameterEnum()-parameters=" + parameters);
    for (int i = 0; i < parameters.size(); i++) {
      String field = parameters.get(i);
      CtField param = new CtField(pool.get("java.lang.String"), field, ctClass);
      ctClass.addMethod(CtNewMethod.setter("set" + field.substring(0, 1).toUpperCase() +
              field.substring(1), param));
      ctClass.addMethod(CtNewMethod.getter("get" + field.substring(0, 1).toUpperCase() +
              field.substring(1), param));
    }


    String curPath = this.getClass().getResource("/").getPath();
    LOGGER.debug("curPath=" + curPath);
    ctClass.writeFile(curPath);
    return  ctClass.toClass().newInstance();
  }

/*  public Map<String,Object> doParse(EmailTemplate emailTemplate) {
    String templateName = emailTemplate.getEmailType();
    Map result = new HashMap();
    try {
      SAXReader sax = new SAXReader();
      Document root = sax.read(ParseEmailTempXMLUtils.class.getClassLoader().getResourceAsStream("email/emailTemplate.xml"));

      Element emailTemp = (Element) root.selectSingleNode("//EmailTemplate[@name='" + templateName + "']");
      List elementList = emailTemp.elements();
      for (Object o : elementList) {
        Element element = (Element) o;
        if (element.getName().equals("Parameters")){
          List childList = element.elements();
          List parameters = new ArrayList();
          for (Object obj : childList) {
            Element param = (Element) obj;
            parameters.add(param.getText());
          }

          result.put(element.getName(), parameters);
          continue;
        }
        result.put(element.getName(), element.getText());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }*/

  public static void main(String[] args) {
    ParseEmailTempXMLUtils parseEmailTempXMLUtils = new ParseEmailTempXMLUtils();
/*    Map<String, Object> map = parseEmailTempXMLUtils.doParse(EmailTemplateApi.OKR_REGULAR_REMINDER);
    for (Map.Entry entry : map.entrySet()) {
      System.out.println("key=" + entry.getKey() + " value=" + entry.getValue());
    }*/
    parseEmailTempXMLUtils.initMap();
    try {
      Object parameter = parseEmailTempXMLUtils.getParameterObject(EmailTemplate.OKR_REGULAR_REMINDER);

      ClassLoader classLoader = parseEmailTempXMLUtils.getClass().getClassLoader();
      classLoader.loadClass("EmailTemplateParameters");

      String className = parameter.getClass().getName();
      Class clazz = parameter.getClass();
      Method[] methods = clazz.getDeclaredMethods();

      for (Method method : methods) {
        System.out.println("methodName = " + method.getName());
      }
      //parameter.setUrl("www.baidu.com");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
