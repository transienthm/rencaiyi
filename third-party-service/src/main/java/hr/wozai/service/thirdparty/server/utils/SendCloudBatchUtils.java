package hr.wozai.service.thirdparty.server.utils;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.utils.ParseEmailTempXMLUtils;
import io.jstack.sendcloud4j.SendCloud;
import io.jstack.sendcloud4j.mail.Email;
import io.jstack.sendcloud4j.mail.Result;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by wangbin on 2016/10/17.
 */
@Component("sendCloudBatchUtils")
public class SendCloudBatchUtils {

  @Autowired
  ParseEmailTempXMLUtils parseEmailTempXMLUtils;

  private static Logger LOGGER = LoggerFactory.getLogger(SendCloudBatchUtils.class);

  @Value("${email.sendcloud.apiBatchUser}")
  private String sendCloudApiUser;

  @Value("${email.sendcloud.apiKey}")
  private String sendCloudApiKey;

  @Value("${email.sendcloud.from}")
  private String sendCloudFrom;

  @Value("${email.sendcloud.addaddressList.url}")
  private String addressListAddUrl;

  @Value("${email.sendcloud.addaddressMember.url}")
  private String addressMemberAddUrl;

  //@Value("${email.sendcloud.fromName}")
  private String sendCloudFromName = "人才易";


  public void batchSend(BatchEmail batchEmail) {
    SendCloud webapi = SendCloud.createWebApi(sendCloudApiUser, sendCloudApiKey);
//    Email email = Email.general();
    try {
      /**
       * 批量发送邮件步骤：
       * 1. 新建sendcloud地址列表
       * 2. 为地址列表添加成员
       * 3. 通过BatchEmail对象为成员拼凑参数
       * 4. 发送批量邮件
       */
      String unicode = batchEmail.unicode();
      //新建sendcloud地址列表
      String addressList = getAddressList(unicode);
      LOGGER.info("新建地址列表为：" + addressList);
      //添加地址列表成员
      List<String> members = getAddressMembersParamMembersList(batchEmail);
      List<Map<String, String>> vars = getAddressMembersParamVarList(batchEmail);

      boolean result = addAddressMember(addressList, members, vars);
      LOGGER.info("添加地址列表成员结果：" + result);

      Map parseResult = parseEmailTempXMLUtils.getParseResult();

      String emailTemplateName = batchEmail.getEmailTemplate().getEmailType();
      //emailTemplateXML解析结果 Map
      Map templateMap = (Map) parseResult.get(emailTemplateName);
      String html = (String) templateMap.get("HTML");
      String subject = (String) templateMap.get("Subject");

      long start = System.currentTimeMillis();
      sendMaillist(addressList, subject, html);

/*      sendMaillist("wangbin@sqian.com", html);
      sendMaillist("wbs_qqz@163.com", html);
      sendMaillist("transienthm@gmail.com", html);
      sendMaillist("wbs_qqz@126.com", html);
      sendMaillist("305459221@qq.com", html);
      sendMaillist("transienthm@vip.qq.com", html);
      sendMaillist("transienthm@sina.cn", html);*/
      long end = System.currentTimeMillis();
      LOGGER.info("批量发送邮件实际时间为：" + (end - start) + "ms");
    } catch (Exception e) {
      LOGGER.error("batchSend()-error:" + e);
    }
  }

  /**
   * 新建sendcloud地址列表
   * @param addressListName 地址列表名
   * @return
   * @throws Exception
   */
  private String getAddressList(String addressListName) throws Exception{

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(addressListAddUrl);

    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("apiUser", sendCloudApiUser));
    params.add(new BasicNameValuePair("apiKey", sendCloudApiKey));
    params.add(new BasicNameValuePair("address", addressListName + "@maillist.sendcloud.org"));
    params.add(new BasicNameValuePair("name", addressListName));

    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    HttpResponse response = httpClient.execute(httpPost);

    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
      LOGGER.info(EntityUtils.toString(response.getEntity()));
      httpPost.releaseConnection();
      return addressListName + "@maillist.sendcloud.org";
    } else {
      LOGGER.error("sendTemplate()-error");
      httpPost.releaseConnection();
      return null;
    }
  }

  /**
   * 新建地址列表成员
   * @param addressList 地址列表
   * @param members 成员
   * @param vars 邮件变量
   * @return
   * @throws Exception
   */
  private boolean addAddressMember(String addressList, List<String> members, List<Map<String,String>> vars) throws Exception{
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost((addressMemberAddUrl));

    //组织参数
    String membersStr = addressMemberListToString(members);
    String varsStr = addressVarsListToString(vars);

    LOGGER.info("***************");
    LOGGER.info("membersStr:" + membersStr);
    LOGGER.info("varsStr:" + varsStr);
    LOGGER.info("***************");

    List params = new ArrayList<>();
    params.add(new BasicNameValuePair("apiUser", sendCloudApiUser));
    params.add(new BasicNameValuePair("apiKey", sendCloudApiKey));
    params.add(new BasicNameValuePair("address", addressList));
    params.add(new BasicNameValuePair("members", membersStr));
    //params.add(new BasicNameValuePair("names","sqian;qq;163;1;12"));
    params.add(new BasicNameValuePair("vars",varsStr));

    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    HttpResponse response = httpClient.execute(httpPost);

    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
      LOGGER.info(EntityUtils.toString(response.getEntity()));
      httpPost.releaseConnection();
      return true;
    } else {
      LOGGER.info("sendTemplate()-error");
      httpPost.releaseConnection();
      return false;
    }
  }

  private boolean sendMaillist(String to, String subject, String html) throws Exception {

    final String url = "http://sendcloud.sohu.com/webapi/mail.send.json";

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);

    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("api_user", sendCloudApiUser));
    params.add(new BasicNameValuePair("api_key", sendCloudApiKey));
    params.add(new BasicNameValuePair("to", to));
    params.add(new BasicNameValuePair("html", html));
    params.add(new BasicNameValuePair("from", sendCloudFrom));
    params.add(new BasicNameValuePair("fromname", sendCloudFromName));
    params.add(new BasicNameValuePair("subject", subject));
    params.add(new BasicNameValuePair("use_maillist", "true"));
    params.add(new BasicNameValuePair("resp_email_id", "true"));

    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    HttpResponse response = httpClient.execute(httpPost);

    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
      LOGGER.info(EntityUtils.toString(response.getEntity()));
      httpPost.releaseConnection();
      return true;

    } else {
      httpPost.releaseConnection();
      System.err.println("error");
      return false;
    }
  }

  /**
   * 地址列表参数List<String> members转换成sendcloud接口能接受的格式
   * @param members
   */
  private String addressMemberListToString(List<String> members) {
    StringBuilder sb = new StringBuilder();
    for (Iterator iterator = members.iterator(); iterator.hasNext(); ) {
      sb.append(iterator.next());
      if (iterator.hasNext()) {
        sb.append(";");
      }
    }
    return sb.toString();
  }

  /**
   * 将vars转换成sendcloud地址列表接口需要的格式
   * @param vars
   * @return
   */
  private String addressVarsListToString(List<Map<String,String>> vars) {
    StringBuilder sb = new StringBuilder();
    Map.Entry entry;
    for (Iterator<Map<String,String>> listIter = vars.iterator();listIter.hasNext();) {
      Map<String, String> perVars = listIter.next();
      //参数头左大括号
      sb.append("{");
      //遍历map拼凑一组参数数据
      for (Iterator<Map.Entry<String,String>> mapIter = perVars.entrySet().iterator();mapIter.hasNext();) {
        entry = mapIter.next();
        sb.append("\"").append(entry.getKey()).append("\"").append(":").append("\"").append(entry.getValue()).append("\"");
        if (mapIter.hasNext()) {
          sb.append(",");
        }
      }
      //参数尾的右大括号
      sb.append("}");
      //不同组参数用";"隔开
      if (listIter.hasNext()) {
        sb.append(";");
      }
    }
    return sb.toString();
  }

  /**
   * 得到添加地址列表成员参数：members的方法
   * @param batchEmail
   * @return
   */
  private List<String> getAddressMembersParamMembersList(BatchEmail batchEmail) {
    List<String> dstEmailAddressList = new ArrayList<>();
    int position = -1;
    for (int i = 0; i < batchEmail.getDynamicParamSeq().size(); i++) {
      if (StringUtils.isEqual(batchEmail.getDynamicParamSeq().get(i), "dstEmailAddress")) {
        position = i;
      }
    }
    //member
    List<List<String>> outerList = batchEmail.getDynamicParams();
    for (List<String> innerList : outerList) {
      dstEmailAddressList.add(innerList.get(position));
    }
    return dstEmailAddressList;
  }

  /**
   * 得到添加地址列表成员参数:vars的方法
   * @param batchEmail
   * @return
   */
  private List<Map<String, String>> getAddressMembersParamVarList(BatchEmail batchEmail) {
    List<Map<String, String>> vars = new ArrayList<>();
    Map<String, String> perVarsGroup ;
    List<String> keyList = batchEmail.getDynamicParamSeq();
    List<List<String>> outerList = batchEmail.getDynamicParams();
    Map<String, String> fixedParams = batchEmail.getFixedParamsMap();
    int varCount = keyList.size();

    for (List<String> innerList : outerList) {
      perVarsGroup = new LinkedHashMap<>();
      for (int i = 0; i < varCount; i++) {
        innerList.set(i, innerList.get(i).replaceAll(",", "，").replaceAll(";","；").replaceAll("\"","“"));
        perVarsGroup.put(keyList.get(i), innerList.get(i));
      }
      if (CollectionUtils.isEmpty(fixedParams)) {
        vars.add(perVarsGroup);
        continue;
      }
      for (Map.Entry<String, String> entry : fixedParams.entrySet()) {
        perVarsGroup.put(entry.getKey(), entry.getValue());
      }
      vars.add(perVarsGroup);
    }
    return vars;
  }
}
