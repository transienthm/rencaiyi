package hr.wozai.service.thirdparty.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.utils.emailtemplate.EmailTemplateApi;
import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by wangbin on 16/6/28.
 */
@Component("emailTemplateHelper")
public class EmailTemplateHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateHelper.class);

    private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer
            = new LocalVariableTableParameterNameDiscoverer();

    private ApplicationContext ctx;

/*    @Autowired
    static SqsProducer sqsProducer;*/

    @Autowired
    static RabbitMQProducer rabbitMQProducer;

    @Autowired
    ParseEmailTempXMLUtils parseEmailTempXMLUtils;

    public void preBatchSendEmail(BatchEmail batchEmail) {
        batchEmail.selfCheck();

        rabbitMQProducer = RabbitMQProducer.getCurRabbitMQProducer();
        String batchEmailJson = JSON.toJSONString(batchEmail);
        //将batchEmail装入sqs
        rabbitMQProducer.preSendBatchEmail(batchEmailJson);
        LOGGER.info("batchEmailJson=" + batchEmailJson);
    }

    @EmailTemplateApi
    public void preSendKeyResultDeadlineReminder(EmailTemplate emailTemplate, String keyResultName,
                                                 String daysBeforeExpired, String name,
                                                 String objectiveName, String deadline,
                                                 String progress, String url,
                                                 String orgShortName, String dstEmailAddress) {

    }

    @EmailTemplateApi
    public void preSendOkrAddNote(EmailTemplate emailTemplate, String userName, String okrType,String objectiveName,
                                  String name, String content, String url, String orgShortName,
                                  String dstEmailAddress) {

    }

    private Class[] getPreSendMethodParameterTypes(int n) {
        Class[] result = new Class[n + 1];
        result[0] = EmailTemplate.class;
        for (int i = 1; i <= n; i++) {
            result[i] = String.class;
        }
        return result;
    }


    /**
     * 步骤:
     * 1. 得到方法的形参数组并将其转化为列表
     * 1)获得当前方法名String method = Thread.currentThread().getStackTrace()[1].getMethodName(); String className = Thread.currentThread() .getStackTrace()[1].getClassName();
     * 2)获得当前方法
     * 3)获得当前方法形参
     * 2. 得到sendCloud邮件模板参数集并将其转化为列表
     * 3. 对比形参列表与邮件模板列表,将相应的形参设为键,将参数值设为值,存入Map中
     * 4. 发送邮件
     */

    //得到方法形参列表
    public static List<String> getParameterNameList(Method method) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < parameterNames.length; i++) {
            result.add(parameterNames[i]);
        }
        return result;
    }

    public static Set<String> getStringBetweenPercentSign(String str) {
        Set<String> result = new HashSet<>();
        String key;
        boolean flag = false;
        String keyChar = "";
        //截取% %内的字符串
        for (int i = 0, j = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '%' && flag == false) {
                flag = true;
                continue;
            }
            if (flag == true && c != '%') {
                keyChar += c;
                j++;
            } else if (flag == true && c == '%') {
                flag = false;
                j = 0;
                key = String.valueOf(keyChar);
                keyChar = "";
                result.add(key);
            }
        }
        return result;
    }

    /**
     * 得到当前方法的方法,要求:该类下没有重载方法
     * @param methodName
     * @return
     */
    public static Method getCurrentMethod(String className,String methodName) {
        try {
            Class clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        } catch (Exception e) {
            LOGGER.error("getCurrentMethod()-error", e);
        }
        return null;
    }

    public static List<String> getSendCloudTemplateParamList(EmailTemplate emailTemplate) {
        rabbitMQProducer = RabbitMQProducer.getCurRabbitMQProducer();
        Set<String> paramSet = rabbitMQProducer.getParamNameSet(emailTemplate);
        List<String> result = new ArrayList<>();
        result.addAll(paramSet);
        return result;
    }

    public static String handleMessageByCompareStr(List<String> sendCloudParamList, List<String> paramNameList, String message) {
        String result = message;
        /**
         * 原则:
         * 1. 两字符串相等
         * 2. 相继去除最大子串后其中一个为空串
         * 3. 相似度最大
         */
        Map<String, String> haveToBeReplace = new HashMap<>();
        //找到需要修改的字符串
        System.out.println("云端参数列表:" + sendCloudParamList);
        System.out.println("本地参数列表:" + paramNameList);
        List<String> list1 ;
        list1 = ListUtils.sum(new ArrayList<>(), paramNameList);
        List<String> list2 ;
        list2 = ListUtils.sum(new ArrayList<>(), sendCloudParamList);
        boolean flag = false;
        String tempStr1 = null;
        String tempStr2 = null;
        for (String str1 : list1) {
            double similarity = -1;
            if (flag == true) {
                paramNameList.remove(tempStr1);
                sendCloudParamList.remove(tempStr2);
                tempStr1 = null;
                tempStr2 = null;
                flag = false;
                System.out.println("tempStr1:" + tempStr1 + " tempStr2:" + tempStr2);
            }
            System.out.println("tempStr1:" + tempStr1 + " tempStr2:" + tempStr2);
            for (String str2 : list2) {
                //本地参数与云端模板参数相等,什么也不做
                if (str1.equals(str2)) {
                    sendCloudParamList.remove(str2);
                    paramNameList.remove(str1);
                    System.out.println(str1 + "与" + str2 + "相同,退出本次循环");
                    break;
                } else if (handleCommonSubstring(str1, str2)) {
                    haveToBeReplace.put(str1, str2);
                    tempStr1 = str1;
                    tempStr2 = str2;
                    flag = true;
                    System.out.println(tempStr1 + "是" + tempStr2 + "的广义子串");
                } else {
                    //TODO 相似字符串处理
                    continue;
                }
            }
        }
        System.out.println("beforeHandle:" + message);
        //重新拼接字符串
        for (Map.Entry<String, String> entry : haveToBeReplace.entrySet()) {
            result = org.apache.commons.lang.StringUtils.replace(message, entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 判断是否有子串
     * @param first
     * @param second
     * @return
     */
    private static boolean handleCommonSubstring(String first, String second) {
        if (similarity(first, second) == 0) {
            return false;
        }
        String str1 = first;
        String str2 = second;
        while (isExistCommonSubstr(str1, str2)) {
            String commonSubstr = longestCommonSubstring(str1, str2);
            str1 = str1.replace(commonSubstr, "");
            str2 = str2.replace(commonSubstr, "");
        }

        if (str1.equals("") || str2.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 比较三个整数最小值
     * @param one
     * @param two
     * @param three
     * @return
     */
    private static int min(int one, int two, int three) {
        int min = one;
        if(two < min) {
            min = two;
        }
        if(three < min) {
            min = three;
        }
        return min;
    }

    /**
     * 计算一个字符串变为另一个字符串需要替换的字符个数
     * @param str1
     * @param str2
     * @return
     */
    private static int ld(String str1, String str2) {
        int d[][];    //矩阵
        int n = str1.length();
        int m = str2.length();
        int i;    //遍历str1的
        int j;    //遍历str2的
        char ch1;    //str1的
        char ch2;    //str2的
        int temp;    //记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if(n == 0) {
            return m;
        }
        if(m == 0) {
            return n;
        }
        d = new int[n+1][m+1];
        for(i=0; i<=n; i++) {    //初始化第一列
            d[i][0] = i;
        }
        for(j=0; j<=m; j++) {    //初始化第一行
            d[0][j] = j;
        }
        for(i=1; i<=n; i++) {    //遍历str1
            ch1 = str1.charAt(i-1);
            //去匹配str2
            for(j=1; j<=m; j++) {
                ch2 = str2.charAt(j-1);
                if(ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]+temp);
            }
        }
        return d[n][m];
    }

    private static double similarity(String str1, String str2) {
        int ld = ld(str1, str2);
        return 1 - (double) ld / Math.max(str1.length(), str2.length());
    }



    /**
     * 得到最大共同子串
     * @param first
     * @param second
     * @return
     */
    private static String longestCommonSubstring(String first, String second) {
        String tmp = "";
        String max = "";
        for (int i=0; i < first.length(); i++){
            for (int j = 0; j < second.length(); j++){
                for (int k = 1; (k+i) <= first.length() && (k+j) <= second.length(); k++){
                    if (first.substring(i, k + i).equals(second.substring(j, k + j))){
                        tmp = first.substring(i, k + i);
                    }
                    else{
                        if (tmp.length() > max.length())
                            max = tmp;
                        tmp = "";
                    }
                }
                if (tmp.length() > max.length())
                    max = tmp;
                tmp = "";
            }
        }
        return max;
    }

    private static boolean isExistCommonSubstr(String first, String second) {
        if (longestCommonSubstring(first, second) != "") {
            return true;
        }

        return false;
    }

    /**
     * 根据邮件模板Enum与参数Map生成相应邮件内容
     * @param emailTemplate
     * @param params
     * @return
     */
    public static String generateEmailContent(EmailTemplate emailTemplate, Map<String, String> params) {

        JSONObject xsmtpapi = generateXsmtpapi(params);

        JSONObject result = new JSONObject();

        result.put("templateInvokeName", emailTemplate.getEmailType());
        result.put("xsmtpapi", xsmtpapi);
        return result.toJSONString();
    }



    public static String generateEmailContentWithoutSurroundingPercentSign(EmailTemplate emailTemplate, Map<String, String> params) {

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

    private static JSONObject generateXsmtpapi(Map<String, String> material) {
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
}
