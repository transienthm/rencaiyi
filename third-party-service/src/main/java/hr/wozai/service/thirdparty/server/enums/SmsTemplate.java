package hr.wozai.service.thirdparty.server.enums;

import com.amazonaws.services.dynamodbv2.xspec.S;

/**
 * Created by wangbin on 16/5/29.
 */
public enum  SmsTemplate {

    DEFAULT_PASSWORD(1216707L),
    VERIFICATION_CODE(1041297L),
    WARNING_NOTICE(1399337L);

    private Long templateId;
    SmsTemplate(Long templateId){
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }
}
