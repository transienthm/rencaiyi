package hr.wozai.service.thirdparty.client.utils;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component("rabbitMQProducer")
public class RabbitMQProducer implements ApplicationContextAware {
    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.user}")
    private String user;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.queue}")
    private String queueName;

    @Value("${rabbitmq.batch.queue}")
    private String batchQueueName;

    @Value("${rabbitmq.forxml.queue}")
    private String forxmlUrl;

    @Value("${email.sendcloud.apiUser}")
    private String sendCloudApiUser;

    @Value("${email.sendcloud.apiKey}")
    private String sendCloudApiKey;

    private static ApplicationContext ctx;

    ConnectionFactory connectionFactory;
    Connection connection;
    Channel channel;
    Channel batchChannel;
    Channel forxmlChannel;

    private static Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);


    @PostConstruct
    @LogAround
    public void init() {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(this.host);
        connectionFactory.setUsername(this.user);
        connectionFactory.setPassword(this.password);
        connectionFactory.setPort(5672);
        LOGGER.info("RabbitMQProducer-init() : connectionFactory = "+ connectionFactory);
        try {
            connection = connectionFactory.newConnection();
            LOGGER.info("RabbitMQProducer-init() : connectionFactory = "+ connectionFactory + ", connection = " + connection);
            channel = connection.createChannel();
            batchChannel = connection.createChannel();
            forxmlChannel = connection.createChannel();
            LOGGER.info("RabbitMQProducer-init():create channel successful, channel = " + channel + ", batchChannel = " + batchChannel
                    + ", forxmlChannel = " + forxmlChannel);
            channel.queueDeclare(this.queueName, false, false, false, null);
            batchChannel.queueDeclare(this.batchQueueName, false, false, false, null);
            forxmlChannel.queueDeclare(this.forxmlUrl, false, false, false, null);
            LOGGER.info("RabbitMQProducer-init():declare queue successful");
        } catch (Exception e) {
            LOGGER.error("RabbitMQProducer-init() - error{}" + e.getMessage());

        }
    }

    @LogAround
    public void sendMessage(String msg) {
        sendMessage(this.queueName, msg);
    }

    @LogAround
    private void sendMessage(String queueName, String msg) {
        LOGGER.info("RabbitMQProducer-sendMessage() : queueName = " + queueName + ", msg = " + msg+ ", channel = " + channel);
        try {
            channel.basicPublish("", queueName, null, msg.getBytes());
        } catch (Exception e) {
            LOGGER.error("RabbitMQProducer-sendMessage()" + e.getMessage());
        }
    }

    @LogAround
    private void sendMessageForXml(String queueName, String msg) {
        LOGGER.info("RabbitMQProducer-sendMessage() : queueName = " + queueName + ", msg = " + msg + ", channel = " + forxmlChannel);
        try {
            forxmlChannel.basicPublish("", queueName, null, msg.getBytes());
        } catch (Exception e) {
            LOGGER.error("RabbitMQProducer-sendMessage()" + e.getMessage());
        }
    }

    @LogAround
    private void sendBatchMessage(String msg) {
        LOGGER.info("RabbitMQProducer-sendMessage() : queueName = " + queueName + ", msg = " + msg + ", channel = " + batchChannel);
        try {
            batchChannel.basicPublish("", this.batchQueueName, null, msg.getBytes());
        } catch (Exception e) {
            LOGGER.error("RabbitMQProducer-sendMessage()" + e);
        }
    }

    @LogAround
    public void sendMessage(EmailTemplate emailTemplate, Map<String, String> params) {
        checkNullValue(params);
        String message = EmailTemplateHelper.generateEmailContent(emailTemplate, params);
        sendMessage(this.queueName, message);
    }

    @LogAround
    public void preSendBatchEmail(String batchEmailJson) {
        sendBatchMessage(batchEmailJson);
    }

    @LogAround
    public void preSendEmail(String emailJson) {
        sendMessageForXml(forxmlUrl, emailJson);
    }

    @LogAround
    public void sendMessageWithoutSurroundingPercentSign(EmailTemplate emailTemplate, Map<String, String> params) {
        checkNullValue(params);
        String message = EmailTemplateHelper.generateEmailContentWithoutSurroundingPercentSign(emailTemplate, params);
        sendMessage(message);
    }

    @LogAround
    private void checkNullValue(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue() == "") {
                entry.setValue("（空）");
            }
        }
    }

    public static RabbitMQProducer getCurRabbitMQProducer() {
        RabbitMQProducer result = ctx.getBean(RabbitMQProducer.class);
        return result;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    /**
     * 得到相应模板的参数集
     * @param emailTemplate
     * @return
     */
    public Set<String> getParamNameSet(EmailTemplate emailTemplate) {
        Set<String> keySet = new HashSet<>();
        try {

            String invokeName = emailTemplate.getEmailType();

            final String url = "http://api.sendcloud.net/apiv2/template/get";
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpClient httpClient = HttpClients.createDefault();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("apiUser", sendCloudApiUser));
            params.add(new BasicNameValuePair("apiKey", sendCloudApiKey));
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