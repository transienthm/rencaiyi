package hr.wozai.service.thirdparty.server.test.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by wangbin on 16/6/10.
 */
@Component("emailTemplateTest")
public class EmailTemplateTest extends BaseTest implements ApplicationContextAware{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateTest.class);
    //@Value("${email.sendcloud.apiUser}")
    static private String apiUser ="Mail_Wozai_Prod";
    //@Value(("${email.sendcloud.apiKey}"))
    static private String apiKey ="tjHGh3xWzTkMvpCT";

    private static ApplicationContext ctx;


    @Autowired
    private static SqsProducer sqsProducer;

    @Test
    public void testAll() {
        try {
            String message;

            sqsProducer = SqsProducer.getCurSqsProducer();
            //FEED 邮件3个

/*            message = EmailTemplate.getFeedAtEmailContent(EmailTemplate.FEED_AT, "张三", "李四", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            message = EmailTemplate.getFeedCommentAtEmailContent(EmailTemplate.FEED_COMMENT_AT, "张三", "李四", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            message = EmailTemplate.getFeedCommentEmailContent(EmailTemplate.FEED_COMMENT, "张三", "李四", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            //OKR邮件4个
            message = EmailTemplate.getOKRUpdateEmailContent(EmailTemplate.OKR_UPDATE, "张三", "目标", "更新了", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getOKRPeriodDeadlineReminderEmailContent(EmailTemplate.OKR_PERIOD_DEADLINE_REMINDER, "张三", "个人", "2016-5月", "3", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getOKRRegularReminderEmailContent(EmailTemplate.OKR_REGULAR_REMINDER, "张三", "成为2k17冠军", "2016-5月", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getOKRDeadlineReminderEmailContent(EmailTemplate.OKR_OBJECTIVE_DEADLINE_REMINDER, "张三", "成为2k17冠军", "99%", "3","2016.10.31", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            //ONBOARDING 1个其他的在OnboardingFlowNotifier
            message = EmailTemplate.getOnboardingRemindEmailContent(EmailTemplate.ONBOARDING_REMIND, "张三", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            //REVIEW邮件10个
            message = EmailTemplate.getReviewBeginEmailContent(EmailTemplate.REVIEW_BEGIN, "张三", "2016年", "年末", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            message = EmailTemplate.getReviewOngoingEmailContent(EmailTemplate.REVIEW_ONGOING, "张三", "李四", "年末", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            message = EmailTemplate.getReviewFinishEmailContent(EmailTemplate.REVIEW_FINISH, "张三", "2016年", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            message = EmailTemplate.getReviewCancelEmailContent(EmailTemplate.REVIEW_CANCEL, "张三", "2016年", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            message = EmailTemplate.getReviewCancelAndBackupEmailContent(EmailTemplate.REVIEW_CANCEL_BACKUP, "张三", "2016年", "无", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
                        message = EmailTemplate.getReviewSelfReminderEmailContent(EmailTemplate.REVIEW_SELF_REMINDER, "张三","测试review提醒", "2016.10.8", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getReviewOngoingReminderEmailContent(EmailTemplate.REVIEW_ONGOING_REMINDER, "张三","测试review提醒", "2016.10.8", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getReviewDeadlineReminderEmailContent(EmailTemplate.REVIEW_DEADLINE_REMINDER, "张三","测试review提醒", "2016.10.8", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getReviewActivityAutoCancelEmailContent(EmailTemplate.REVIEW_ACTIVITY_AUTO_CANCEL, "张三", "反馈活动", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);

            message = EmailTemplate.getReviewInvitationAutoCancelEmailContent(EmailTemplate.REVIEW_INVITATION_AUTO_CANCEL, "张三", "李四", "活动", "谷歌", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);


            //申请试用
            message = EmailTemplate.getTrialApplicationEmailContent(EmailTemplate.TRIAL_APPLICATION, "总经理", "120", "闪签", "wangbin@sqian.com", "130", "张三", "10", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
           //重置密码
            message = EmailTemplate.getResetPasswordEmailContent(EmailTemplate.RESET_PASSWORD, "张三", "https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D", "闪签", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            // 授权人工操作
            message = EmailTemplate.getWarrentManualOperationEmailContent(EmailTemplate.WARRANT_MANUAL_OPERATION, "wangbin@sqian.com", "www.baidu.com", "wangbin@sqian.com");
            sqsProducer.sendMessage(message);
            */


        } catch (Exception e) {

        }
    }

    static int userNameListCount = 0;
    static int teamAndOrgNameListCount = 0;
    static int jobLevelListCount = 0;
    static int jobTitleListCount = 0;
    static int dateListCount = 0;
    static int emailListCount = 0;
    static int otherListCount = 0;
    static int urlListCount = 0;

    /**
     * 根据邮件模板Enum与参数Map生成相应邮件内容
     * @param emailTemplate
     * @param params
     * @return
     */
    public String generateEmailContent(EmailTemplate emailTemplate, Map<String, String> params) {

        JSONObject xsmtpapi = generateXsmtpapi(params);

        JSONObject result = new JSONObject();

        result.put("templateInvokeName", emailTemplate.getEmailType());
        result.put("xsmtpapi", xsmtpapi);
        return result.toJSONString();
    }



    public String generateEmailContentWithoutSurroundingPercentSign(EmailTemplate emailTemplate, Map<String, String> params) {

        Map<String, String> material = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = "%" + entry.getKey() + "%";
            String value = entry.getValue();
            material.put(key, value);

        }
        JSONObject xsmtpapi = generateXsmtpapi(material);

        JSONObject result = new JSONObject();

        result.put("templateInvokeName", emailTemplate.getEmailType());
        result.put("xsmtpapi", xsmtpapi);
        return result.toJSONString();
    }

    /**
     * 通过EmailTempalte生成相应的邮件参数,以供测试使用
     *
     * @param emailTemplate
     * @return
     * @throws IOException
     */
    public Map<String, String> generateParamsMap(EmailTemplate emailTemplate) throws IOException {
        Map<String, String> result = new HashMap<>();

        sqsProducer = SqsProducer.getCurSqsProducer();
        //初始化list表单
        initLists();
        Set<String> keySet = new HashSet<>();
        keySet = sqsProducer.getParamNameSet(emailTemplate);

        //遍历存放key的集合,生成相应的value,将key value存入result中
        Iterator<String> keySetIter = keySet.iterator();
        while (keySetIter.hasNext()) {
            String s = keySetIter.next();
            String tmp = s.toLowerCase();
            result.put(s, generateMapValue(tmp));
        }


        return result;
    }





    /**
     * 供测试使用
     * @param lowercaseKey
     * @return
     */
    private static String generateMapValue(String lowercaseKey) {

        String result = null;

        Map<String, Collection> repository = new HashMap<>();

        repository.put("name", userNameList);
        repository.put("teamAndOrgName", teamAndOrgNameList);
        repository.put("jobTitle", jobTitleList);
        repository.put("jobLevel", jobLevelList);
        repository.put("date", dateList);
        repository.put("email", emailList);
        repository.put("url", urlList);
        repository.put("other", otherList);

        try {
            if (lowercaseKey.contains("username") || lowercaseKey.equals("name")
                    || lowercaseKey.contains("fullname") || lowercaseKey.contains("reportername")
                    || lowercaseKey.equals("reviewer") || lowercaseKey.equals("reviewee")) {
                List<String> resultList = (List) repository.get("name");
                if (userNameListCount >= resultList.size()) {
                    userNameListCount = 0;
                }
                result = resultList.get(userNameListCount);
                userNameListCount++;
            } else {
                if ((lowercaseKey.contains("org") || (lowercaseKey.contains("team")) && lowercaseKey.contains("name"))) {
                    List<String> resultList = (List) repository.get("teamAndOrgName");
                    if (teamAndOrgNameListCount >= resultList.size()) {
                        teamAndOrgNameListCount = 0;
                    }
                    result = resultList.get(teamAndOrgNameListCount);
                    teamAndOrgNameListCount++;
                } else if (lowercaseKey.contains("jobtitle")) {
                    List<String> resultList = (List) repository.get("jobTitle");
                    if (jobTitleListCount >= resultList.size()) {
                        jobTitleListCount = 0;
                    }
                    result = resultList.get(jobTitleListCount);
                    jobTitleListCount++;
                } else if (lowercaseKey.contains("joblevel")) {
                    List<String> resultList = (List) repository.get("jobLevel");
                    if (jobLevelListCount >= resultList.size()) {
                        jobLevelListCount = 0;
                    }
                    result = resultList.get(jobLevelListCount);
                    jobLevelListCount++;
                } else if (lowercaseKey.contains("date")) {
                    List<String> resultList = (List) repository.get("date");
                    if (dateListCount >= resultList.size()) {
                        dateListCount = 0;
                    }
                    result = resultList.get(dateListCount);
                    dateListCount++;
                } else if (lowercaseKey.contains("email")) {
                    List<String> resultList = (List) repository.get("email");
                    if (emailListCount >= resultList.size()) {
                        emailListCount = 0;
                    }
                    result = resultList.get(emailListCount);
                    emailListCount++;
                } else if (lowercaseKey.contains("url")) {
                    List<String> resultList = (List) repository.get("url");
                    if (urlListCount >= resultList.size()) {
                        urlListCount = 0;
                    }
                    result = resultList.get(urlListCount);
                    urlListCount++;
                } else {
                    List<String> resultList = (List) repository.get("other");
                    if (otherListCount >= resultList.size()) {
                        otherListCount = 0;
                    }
                    result = resultList.get(otherListCount);
                    otherListCount++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("hr.wozai.service.thirdparty.server.test.utils.EmailTemplateComponent-generateMapValue()", e);
        }
        return result;
    }


    /**
     * 测试使用
     */
    static List<String> userNameList = new ArrayList<>();
    static List<String> teamAndOrgNameList = new ArrayList<>();
    static List<String> jobLevelList = new ArrayList<>();
    static List<String> jobTitleList = new ArrayList<>();
    static List<String> dateList = new ArrayList<>();
    static List<String> otherList = new ArrayList<>();
    static List<String> emailList = new ArrayList<>();
    static List<String> urlList = new ArrayList<>();

    /**
     * 测试使用
     */
    public static void initLists() {
        userNameList.add("习近平");
        userNameList.add("李克强");
        userNameList.add("王斌");
        userNameList.add("陈哲");
        userNameList.add("乐普久");
        teamAndOrgNameList.add("百度");
        teamAndOrgNameList.add("闪签");
        teamAndOrgNameList.add("谷歌");
        teamAndOrgNameList.add("阿里");
        jobLevelList.add("T5");
        jobLevelList.add("T6");
        jobTitleList.add("后端工程师");
        jobTitleList.add("前端工程师");
        jobTitleList.add("市场部员");
        dateList.add("2016-1-1");
        dateList.add("2016-2-2");
        otherList.add("无");
        emailList.add("wangbin@sqian.com");
        urlList.add("https://test-env-image.oss-cn-beijing.aliyuncs.com/avatar_image/3c8d64d9-24b3-43f2-a55e-230d24918edb%7C0%2C0%2C100?Expires=1477643092&OSSAccessKeyId=6YEJ2pwfoEjWXrpL&Signature=Uc96RM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DRM1Qy5szDQYoaY%3DY6EgyMJH3RM1Qy5szDQYoaY%3D");
        urlList.add("www.qq.com");
    }


    private JSONObject generateXsmtpapi(Map<String, String> material) {
        JSONObject xsmtpapi = new JSONObject();
        JSONObject sub = new JSONObject();
        JSONArray to = new JSONArray();
        for (Map.Entry<String, String> entry : material.entrySet()) {
            JSONArray jsonArray = new JSONArray();
            if (entry.getKey().contains("EmailAddress")) {
                to.add(entry.getValue());
                continue;
            }
            jsonArray.add(entry.getValue());
            sub.put(entry.getKey(), jsonArray);
        }
        xsmtpapi.put("sub", sub);
        xsmtpapi.put("to", to);
        return xsmtpapi;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static EmailTemplateTest getCurBean() {
        EmailTemplateTest emailTemplateTest = ctx.getBean(EmailTemplateTest.class);
        System.out.println("=========================================" + emailTemplateTest);
        return emailTemplateTest;
    }


    /**
     * 得到云端相应模板的参数集
     * @param emailTemplate
     * @return
     */
    public static Set<String> getParamNameSet(EmailTemplate emailTemplate) {
        Set<String> keySet = new HashSet<>();
        try {
            String invokeName = emailTemplate.getEmailType();

            final String url = "http://api.sendcloud.net/apiv2/template/get";
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient httpClient = HttpClients.createDefault();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("apiUser", apiUser));
            params.add(new BasicNameValuePair("apiKey", apiKey));
            params.add(new BasicNameValuePair("invokeName", invokeName));

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            // 处理响应
            JSONObject jsonObject;
            JSONObject info;
            JSONObject data;
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 正常返回, 解析返回数据
                jsonObject = (JSONObject) JSONObject.parse(EntityUtils.toString(response.getEntity()));
                info = (JSONObject) jsonObject.get("info");
                data = (JSONObject) info.get("data");
                String html = (String) data.get("html");
                String subject = (String) data.get("subject");
                html = subject + html;
                keySet = EmailTemplateHelper.getStringBetweenPercentSign(html);
            }
            httpPost.releaseConnection();
        } catch (Exception e) {
            LOGGER.error("getParamNameSet()-error");
        }
        return keySet;
    }
}