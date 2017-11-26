package hr.wozai.logmonitor;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 16/6/17.
 */
public class LogMonitorService{

    private final static Logger LOGGER = LoggerFactory.getLogger(LogMonitorService.class);

    private static AmazonSQS amazonSQS;

    private final static String apiKey = "efa36f21711650f32b47f5c8adc2c265";

    private static String API_KEY = "efa36f21711650f32b47f5c8adc2c265";

    private static String TEMPLATE_ID = "1399337";

    private static String ENCODING = "UTF-8";

    private static List<String> smsList;

    //模板发送接口的http地址
    private static String URI_TPL_SEND_SMS = "https://sms.yunpian.com/v2/sms/tpl_single_send.json";

    private final static String SQS_SMS_URL = "https://sqs.cn-north-1.amazonaws.com.cn/745956536067/sqian-sms-staging";

    static {
        amazonSQS = new AmazonSQSClient(new BasicAWSCredentials("AKIAOJPCHIMK2BT3XLNQ", "mOyQf5oWze/LAKxRS+AnxB9teQ/UNtpqdb4PLvbD"));
        amazonSQS.setRegion(Region.getRegion(Regions.CN_NORTH_1));
        try {
            Configuration config = new PropertiesConfiguration("sms.properties");
            smsList = config.getList("sms.list");
        } catch (Exception e) {
           LOGGER.error("读取配置文件异常");
        }
    }

    private static String tplSendSms(String apikey, String tplId, String tplValue, String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("tpl_id", tplId);
        params.put("tpl_value", tplValue);
        params.put("mobile", mobile);
        return post(URI_TPL_SEND_SMS, params);
    }

    private static String post(String url, Map<String, String> paramsMap) {
        System.out.println("post()正在发送sms消息");
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
                System.out.println(responseText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }

    public static void main(String[] args){
        System.out.println("工作中");
        for (String sms : smsList) {
            LOGGER.debug("可以通知到的手机号有:" + sms);
        }
        while (true) {
            try {
                if (amazonSQS != null) {

                    List<Message> smsMsgs = amazonSQS.receiveMessage(
                            new ReceiveMessageRequest(SQS_SMS_URL).withMaxNumberOfMessages(1)).getMessages();

                    if (smsMsgs.size() > 0) {
                        System.out.println("正在消费sms队列:" + SQS_SMS_URL);
                        for (Message message : smsMsgs) {
                            System.out.println("The message is {}" + message.getBody());
                            try {
                                String tplValue = URLEncoder.encode("#warning#", ENCODING) + "="
                                        + URLEncoder.encode(message.getBody(), ENCODING);
                                for (String phoneNumber : smsList) {
                                    tplSendSms(API_KEY, TEMPLATE_ID, tplValue, phoneNumber);
                                }
                            } catch (Exception e) {
                                LOGGER.error("send sms error: {}", e.getMessage());
                            }
                            amazonSQS.deleteMessage(new DeleteMessageRequest(SQS_SMS_URL, message.getReceiptHandle()));
                        }
                    }
                } else {
                    LOGGER.error("amazonSQS: {null}");
                }
            } catch (Exception e) {
                LOGGER.error("sqsConsumer-run():{}", e);
            }
        }
    }
}
