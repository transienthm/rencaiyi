package hr.wozai.service.thirdparty.server.utils;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.bean.EmailContent;
import hr.wozai.service.thirdparty.server.enums.SmsTemplate;
import hr.wozai.service.thirdparty.server.service.SmsService;
import io.jstack.sendcloud4j.SendCloud;
import io.jstack.sendcloud4j.mail.Email;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("rabbitMQConsumer")
public class RabbitMQConsumer implements Runnable, InitializingBean, ApplicationContextAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private static ApplicationContext context;

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

    @Value("${email.sendcloud.from}")
    private String sendCloudFrom;

    //@Value("${email.sendcloud.fromName}")
    private String sendCloudFromName = "Zest";

    List<String> emailMsgs;
    List<String> batchEmailMsgs;
    List<String> xmlEmailMsgs;

    //private static final Properties properties;
    private static final String ENCODING = "UTF-8";

    @Autowired
    private SmsService smsService;

    @Autowired
    private SendCloudBatchUtils sendCloudBatchUtils;

    private SendCloud sendCloudApi;

    ConnectionFactory connectionFactory;
    Connection connection;
    Channel channel;
    Channel batchChannel;
    Channel forxmlChannel;
    Consumer emailConsumer;
    Consumer batchConsumer;
    Consumer xmlConsumer;

    @PostConstruct
    public void init() {

    }

    public RabbitMQConsumer() {
        LOGGER.info("spring实例化中....,实例化对象为:" + this);
        emailMsgs = new ArrayList<>();
        xmlEmailMsgs = new ArrayList<>();
        batchEmailMsgs = new ArrayList<>();
    }

    @LogAround
    public void sendSmsMessage(String mobilePhoneNumber, String text) throws Exception {
        LOGGER.info("Sqs sending cloudwatch warning message:" + text + " to:" + mobilePhoneNumber);
        boolean result = smsService.sendSmsMessage(mobilePhoneNumber, text, SmsTemplate.WARNING_NOTICE.getTemplateId());
        if (!result) {
            LOGGER.error("sendSmsMessage()-error", ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        } else {
            LOGGER.info("send warning text to mobile phone:" + text);
        }
    }

    @LogAround
    public void sendTemplate(JSONObject data) throws Exception {
        String url = "http://api.sendcloud.net/apiv2/mail/sendtemplate";

        //String apiUser = "Mail_Wozai_Prod";
        //String apiKey = "tjHGh3xWzTkMvpCT";
        //String from = "service@mail.sqian.com";
        //String fromName = "闪签";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apiUser", sendCloudApiUser));
        params.add(new BasicNameValuePair("apiKey", sendCloudApiKey));
        params.add(new BasicNameValuePair("from", sendCloudFrom));
        params.add(new BasicNameValuePair("fromName", sendCloudFromName));
        params.add(new BasicNameValuePair("templateInvokeName", data.getString("templateInvokeName")));
        params.add(new BasicNameValuePair("xsmtpapi", data.getString("xsmtpapi")));

        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpClient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
            LOGGER.info(EntityUtils.toString(response.getEntity()));
        } else {
            LOGGER.error("sendTemplate()-error");
        }
        httpPost.releaseConnection();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.currentThread().sleep(10000);
            } catch (Exception e) {
                LOGGER.error("RabbitMQconsume-sleep(): Exception");
            }
            try {
                if (channel != null && batchChannel != null && forxmlChannel != null) {
                    channel.basicConsume(queueName, true, emailConsumer);
                    batchChannel.basicConsume(batchQueueName, true, batchConsumer);
                    forxmlChannel.basicConsume(forxmlUrl, true, xmlConsumer);
                    if (emailMsgs.size() > 0) {
                        LOGGER.info("正在消费Email队列:" + queueName);
                        for (String message : emailMsgs) {
                            LOGGER.info("The message is {}", message);
                            try {
                                sendTemplate(JSONObject.parseObject(message));
                            } catch (Exception e) {
                                LOGGER.error("send email error: {}", e.getMessage());
                            }
                        }
                        emailMsgs.remove(emailMsgs.size() - 1);
                    }

                    if (batchEmailMsgs.size() > 0) {
                        long start = System.currentTimeMillis();
                        LOGGER.info("正在消费BatchEmail队列：" + batchQueueName);
                        for (String message : batchEmailMsgs) {
                            LOGGER.info("The message is {}", message);
                            BatchEmail batchEmail = JSONObject.parseObject(message, BatchEmail.class);
                            sendCloudBatchUtils.batchSend(batchEmail);
                        }
                        long end = System.currentTimeMillis();
                        LOGGER.info("读取批量邮件队列并发送批量邮件总时间为" + (end - start) + "ms");
                        batchEmailMsgs.remove(batchEmailMsgs.size() - 1);
                    }

                    if (xmlEmailMsgs.size() > 0) {
                        LOGGER.info("正在消费xmlEmail队列：" + forxmlUrl);
                        for (String message : xmlEmailMsgs) {
                            LOGGER.info("The message is {}", message);
                            EmailContent emailContent = JSONObject.parseObject(message, EmailContent.class);
                            Email email = Email.general()
                                    .from(sendCloudFrom)
                                    .fromName(sendCloudFromName)
                                    .subject(emailContent.getSubject())
                                    .html(emailContent.getHtml())
                                    .to(emailContent.getDstEmailAddress());
                            sendCloudApi.mail().send(email);
                        }
                        xmlEmailMsgs.remove(xmlEmailMsgs.size() - 1);
                    }
                } else {
                    LOGGER.error("rabbitMQ-channel: {null}");
                }
            } catch (Exception e) {
                LOGGER.error("rabbitMQConsumer-run():{}", e);
            }
        }
    }

    @Override
    @LogAround
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("RabbitMQConsumer-afterPropertiesSet() : start...");
        smsService = (SmsService) getBean("smsService");

        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(this.host);
        connectionFactory.setUsername(this.user);
        connectionFactory.setPassword(this.password);
        sendCloudApi = SendCloud.createWebApi(sendCloudApiUser, sendCloudApiKey);
        try {
            connection = connectionFactory.newConnection();
            LOGGER.info("RabbitMQConsumer-afterPropertiesSet() : connectionFactory = "+ connectionFactory + ", connection = " + connection);
            channel = connection.createChannel();
            batchChannel = connection.createChannel();
            forxmlChannel = connection.createChannel();

            channel.queueDeclare(this.queueName, false, false, false, null);
            batchChannel.queueDeclare(this.batchQueueName, false, false, false, null);
            forxmlChannel.queueDeclare(this.forxmlUrl, false, false, false, null);
            LOGGER.info("RabbitMQConsumer-afterPropertiesSet() :create channel successful, channel = " + channel + ", batchChannel = " + batchChannel
                    + ", forxmlChannel = " + forxmlChannel);
            emailMsgs = new ArrayList<>();
            batchEmailMsgs = new ArrayList<>();
            xmlEmailMsgs = new ArrayList<>();
            emailConsumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    emailMsgs.add(new String(body));
                }
            };
            batchConsumer = new DefaultConsumer(batchChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    batchEmailMsgs.add(new String(body));
                }
            };
            xmlConsumer = new DefaultConsumer(forxmlChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    xmlEmailMsgs.add(new String(body));
                }
            };
        } catch (Exception e) {
            LOGGER.error("RabbitMQConsumer-init()-error :" + e);
        }

        Thread rabbitMQConsumer = new Thread(this);
        rabbitMQConsumer.setDaemon(true);
        rabbitMQConsumer.start();
        LOGGER.info("RabbitMQConsumer-afterPropertiesSet() :rabbitMQConsumer-thread is running...");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
}
