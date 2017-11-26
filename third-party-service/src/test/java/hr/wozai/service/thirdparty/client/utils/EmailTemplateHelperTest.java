package hr.wozai.service.thirdparty.client.utils;

import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin on 2016/10/19.
 */
public class EmailTemplateHelperTest extends BaseTest{

  @Autowired
  private EmailTemplateHelper emailTemplateHelper;

  @Test
  public void preBatchSendEmail() throws Exception {
    BatchEmail batchEmail = new BatchEmail();
          /*Map<String, String> fixedMap = new HashMap<>();
    fixedMap.put("orgShortName", "谷+歌");
    fixedMap.put("url", "ww#w.goo{le.com");
*/
    List<String> dynamicParamSeq = new ArrayList<>();
    dynamicParamSeq.add("reviewer");
    dynamicParamSeq.add("dstEmailAddress");
    dynamicParamSeq.add("reviewee");
    dynamicParamSeq.add("deadline");
    dynamicParamSeq.add("orgShortName");
    dynamicParamSeq.add("url");
    dynamicParamSeq.add("users");
    dynamicParamSeq.add("userName");

    List<List<String>> dynamicParams = new ArrayList<>();

    List<String> params = new ArrayList<>();
    params.add("张,一；1");
    params.add("wangbin@sqian.com");
    params.add("张;二\"2");
    params.add("明&天");
    params.add("谷歌");
    params.add("www.google.com");
    params.add("张三，张四");
    params.add("张,一；1");

    List<String> params1 = new ArrayList<>();
    params1.add("李dsf1");
    params1.add("wbs_qqz@163.com");
    params1.add("李2");
    params1.add("后天");
    params1.add("谷歌");
    params1.add("www.google.com");
    params1.add("李三");
    params1.add("李dsf1");

    List<String> params2 = new ArrayList<>();
    params2.add("王1");
    params2.add("wbs_qqz@126.com");
    params2.add("王2");
    params2.add("今天");
    params2.add("谷歌");
    params2.add("www.google.com");
    params2.add("王三");
    params2.add("王1");

    List<String> params3 = new ArrayList<>();
    params3.add("赵1");
    params3.add("transienthm@gmail.com");
    params3.add("赵2");
    params3.add("明天");
    params3.add("谷歌");
    params3.add("www.google.com");
    params3.add("赵三");
    params3.add("赵1");

    List<String> params4 = new ArrayList<>();
    params4.add("陈1");
    params4.add("wbs_qqz@sohu.com");
    params4.add("陈2");
    params4.add("大后天");
    params4.add("谷歌");
    params4.add("www.google.com");
    params4.add("陈三");
    params4.add("陈1");

    List<String> params5 = new ArrayList<>();
    params5.add("闫1");
    params5.add("transienthm@sina.com");
    params5.add("闫2");
    params5.add("明天");
    params5.add("谷歌");
    params5.add("www.google.com");
    params5.add("闫三");
    params5.add("闫1");


    dynamicParams.add(params);
    dynamicParams.add(params1);
    dynamicParams.add(params2);
    dynamicParams.add(params3);
    dynamicParams.add(params4);
    dynamicParams.add(params5);

    batchEmail.setDynamicParams(dynamicParams);
    batchEmail.setDynamicParamSeq(dynamicParamSeq);
    //batchEmail.setFixedParamsMap(fixedMap);
//    batchEmail.setEmailTemplate(EmailTemplate.REVIEW_MANAGER_INVITE);
//    emailTemplateHelper.preBatchSendEmail(batchEmail);

//    batchEmail.setEmailTemplate(EmailTemplate.SURVEY_PUSH);
//    emailTemplateHelper.preBatchSendEmail(batchEmail);
//
//    batchEmail.setEmailTemplate(EmailTemplate.CONVR_SCHEDULE_REMINDER);
//    emailTemplateHelper.preBatchSendEmail(batchEmail);

  }

  @Test
  public void preSend() {
//    emailTemplateHelper.preSendKeyResultDeadlineReminder(EmailTemplate.OKR_KEYRESULT_DEADLINE_REMINDER, "吃两斤饭", "3", "小张", "胖10斤", "后天", "11%", "www.google.com", "谷歌", "wangbin@sqian.com");
//    emailTemplateHelper.preSendOkrAddNote(EmailTemplate.OKR_ADD_NOTE, "张三", "个人", "2k17冠军", "李四", "加油", "https://exmail.qq.com/cgi-bin/frame_html?sid=krbkta60OOHFqxhA,7&r=17d9f73fab9255433c26866c87572549www.baidu.com", "百度", "wangbin@sqian.com");
  }

}