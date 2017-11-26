package hr.wozai.service.thirdparty.client.utils;

import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/10/14.
 */
public class ParseEmailTempXMLUtilsTest extends BaseTest{

  @Autowired
  ParseEmailTempXMLUtils parseEmailTempXMLUtils;

  @Test
  public void initMap() throws Exception {
    parseEmailTempXMLUtils.initMap();
    try {
/*      Object parameter = parseEmailTempXMLUtils.getParameterObject(EmailTemplate.OKR_REGULAR_REMINDER);
      String className = parameter.getClass().getName();
      Class clazz = parameter.getClass();
      Method[] methods = clazz.getMethods();
      for (Method method : methods) {
        System.out.println("methodName = " + method.getName());
      }*/

      Map xmlMap = parseEmailTempXMLUtils.getParseResult();
      Map paramMap = (Map) xmlMap.get("review_manager_invite_001");
      List<String> parameters = (List) paramMap.get("Parameters");
      /*for (String s : parameters) {
        System.out.println("parameter=" + s.toString());
      }
      System.out.println("parameters=" + parameters.size());
   */ } catch (Exception e) {
      e.printStackTrace();
    }
  }



}