package hr.wozai.service.thirdparty.client.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wangbin on 16/5/16.
 */
public enum EmailTemplate {
    //邮件模板共13+7+3+3+4+1+1+3

    //REVIEW模板13个

    REVIEW_BEGIN(1, "review_begin_002"),
    REVIEW_FINISH(2, "review_finish_002"),
    REVIEW_ONGOING(3, "review_ongoing_002"),
    REVIEW_CANCEL(4, "review_cancel_001"),
    REVIEW_CANCEL_BACKUP(5, "review_cancel_002"),
    REVIEW_PEER_BATCH_INVITE(6, "review_peer_batch_invite_001"),
    REVIEW_MANAGER_INVITE(7, "review_manager_invite_001"),
    REVIEW_PEER_NOTIFY_MANAGER(8, "review_peer_notify_manager_001"),
    REVIEW_SELF_REMINDER(9, "review_self_reminder_001"),
    REVIEW_ONGOING_REMINDER(10, "review_ongoing_reminder_001"),
    REVIEW_DEADLINE_REMINDER(11, "review_deadline_reminder_001"),
    REVIEW_ACTIVITY_AUTO_CANCEL(12, "review_activity_auto_cancel"),
    REVIEW_INVITATION_AUTO_CANCEL(13, "review_invitation_auto_cancel"),
    //ONBOARDING模板7个
    ONBOARDING_REMIND(101, "onboarding_remind_002"),
    INVITE_ACTIVATION(102, "invite_activation_003"),
    INVITE_ONBOARDING(103, "onboarding_activation_003"),
    SUBMIT_ONBOARDING(104, "onboarding_flow_submit_002"),
    APPROVE_ONBOARDING(105, "onboarding_flow_approve_003"),
    ENROLL_BROADCAST(106, "enroll_broadcast_002"),
    REJECT_ONBOARDING(107, "onboarding_flow_reject_002"),
    //FEED模板3个
    FEED_AT(201, "feed_at_002"),
    FEED_COMMENT(202, "feed_comment_002"),
    FEED_COMMENT_AT(203, "feed_comment_at_002"),
    //离调转模板3个
    PASS_PROBATION_NOTIFICATION(301, "pass_probation_notification_001"),
    RESIGN_NOTIFICATION(302, "resign_notification_001"),
    TRANSFER_NOTIFICATION(303, "transfer_notification_001"),
    //OKR模板6个
    OKR_UPDATE(401, "okr_update_002"),
    OKR_PERIOD_DEADLINE_REMINDER(402, "okr_period_dl_reminder_001"),
    OKR_REGULAR_REMINDER(403, "okr_regular_reminder_001"),
    OKR_OBJECTIVE_DEADLINE_REMINDER(404, "okr_deadline_reminder_001"),
    OKR_KEYRESULT_DEADLINE_REMINDER(405, "okr_keyresult_deadline_reminder"),
    OKR_ADD_NOTE(406, "okr_add_notes"),
    //试用申请模板1个
    TRIAL_APPLICATION(501, "trial_application_001"),
    //重置密码模板1个
    RESET_PASSWORD(502, "reset_password_003"),
    //开启邮件模板 3个
    OPEN_ACCOUNT_SUCCESS(601, "open_account_success_tohr_003"),
    OPEN_ACCOUNT_SUCCESS_TOADMIN(603, "open_account_success_toadmin_003"),
    WARRANT_MANUAL_OPERATION(602, "warrant_manual_operation_001"),

    //1on1 邮件1个
    CONVR_SCHEDULE_REMINDER(701, "convr_schedule_reminder_001"),
    //SURVEY 邮件1个
    SURVEY_PUSH(801, "survey_push_001"),
    ;


    public String getEmailType() {
        return emailType;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    private String emailType;
    private Integer templateId;

    EmailTemplate(Integer templateId, String emailType) {
        this.templateId = templateId;
        this.emailType = emailType;
    }

    public static EmailTemplate getEnumByTemplateId(Integer templateId) {
        if (null == templateId) {
            return null;
        }
        for (EmailTemplate emailTemplate : EmailTemplate.values()) {
            if (IntegerUtils.equals(templateId, emailTemplate.templateId)) {
                return emailTemplate;
            }
        }
        return null;
    }

    public static EmailTemplate getEnumByEmailType(String emailType) {
        if (null == emailType) {
            return null;
        }
        for (EmailTemplate emailTemplate : EmailTemplate.values()) {
            if (StringUtils.isEqual(emailTemplate.emailType, emailType)) {
                return emailTemplate;
            }
        }
        return null;
    }


    /**
     * 重置密码邮件模板
     * @param emailTemplate
     * @param name
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getResetPasswordEmailContent(EmailTemplate emailTemplate, String name, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = Thread.currentThread().getStackTrace()[1].getClassName();
        //得到当前方法
        Method method = EmailTemplateHelper.getCurrentMethod(className, methodName);
        //得到当前方法形参名List
        List<String> paramNameList = EmailTemplateHelper.getParameterNameList(method);
        List<String> sendCloudParamList = EmailTemplateHelper.getSendCloudTemplateParamList(emailTemplate);
        //List<String> paramNameList = EmailTemplateHelper.getParameterNameList(method);
/*        for (String s : paramNameList) {
            System.out.println("参数为:" + s);
        }*/
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        Set<String> keySet = EmailTemplateHelper.getStringBetweenPercentSign(message);
        return message;
    }

    /**
     * 邀请互评邮件模板
     * @param emailTemplate
     * @param reviewer
     * @param reviewee
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewOngoingEmailContent(EmailTemplate emailTemplate, String reviewer, String reviewee, String deadline, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%reviewer%\":[\"" + reviewer + "\"]," +
                "\"%reviewee%\":[\"" + reviewee + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 评价开始邮件模板
     * @param emailTemplate
     * @param name
     * @param period
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewBeginEmailContent(EmailTemplate emailTemplate, String name, String period, String deadline, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%period%\":[\"" + period + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒填写review自评邮件
     * @param emailTemplate
     * @param reviewActivityName
     * @param deadline
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewSelfReminderEmailContent(EmailTemplate emailTemplate, String name, String reviewActivityName, String deadline, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%reviewActivityName%\":[\"" + reviewActivityName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%name%\":[\"" + name + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒review互评邮件
     *
     * @param emailTemplate
     * @param reviewActivityName
     * @param deadline
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewOngoingReminderEmailContent(EmailTemplate emailTemplate, String name, String reviewActivityName, String deadline, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%reviewActivityName%\":[\"" + reviewActivityName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%name%\":[\"" + name + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒主管review截止邮件
     *
     * @param emailTemplate
     * @param reviewActivityName
     * @param deadline
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewDeadlineReminderEmailContent(EmailTemplate emailTemplate, String name, String reviewActivityName, String deadline, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%reviewActivityName%\":[\"" + reviewActivityName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%name%\":[\"" + name + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    public static String getReviewActivityAutoCancelEmailContent(EmailTemplate emailTemplate, String name, String reviewActivityName, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%reviewActivityName%\":[\"" + reviewActivityName + "\"]," +
                "\"%name%\":[\"" + name + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    public static String getReviewInvitationAutoCancelEmailContent(EmailTemplate emailTemplate, String name, String users, String reviewActivityName, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%reviewActivityName%\":[\"" + reviewActivityName + "\"]," +
                "\"%users%\":[\"" + users + "\"]," +
                "\"%name%\":[\"" + name + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    public static String getReviewManagerOngoingEmailContent(EmailTemplate emailTemplate, String reviewer, String reviewee, String url, String deadline, String orgShortName, String targetEmailAddress) {

        String templateInvokeName = emailTemplate.getEmailType();
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%url%\":[\"" + url + "\"]," +
                "\"%reviewee%\":[\"" + reviewee + "\"]," +
                "\"%reviewer%\":[\"" + reviewer + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 入职激活邮件模板
     *
     * @param emailTemplate
     * @param name
     * @param adminUserName
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getOnboardingActivationEmailContent(EmailTemplate emailTemplate, String name, String adminUserName, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%adminUserName%\":[\"" + adminUserName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * feed回复提醒邮件模板
     * @param emailTemplate
     * @param feedUserName
     * @param commentUserName
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getFeedCommentEmailContent(EmailTemplate emailTemplate, String feedUserName, String commentUserName, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%feedUserName%\":[\"" + feedUserName + "\"]," +
                "\"%commentUserName%\":[\"" + commentUserName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * feed 回复中@提醒邮件模板
     *
     * @param emailTemplate
     * @param atUserName
     * @param feedUserName
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getFeedCommentAtEmailContent(EmailTemplate emailTemplate, String atUserName, String feedUserName, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%atUserName%\":[\"" + atUserName + "\"]," +
                "\"%feedUserName%\":[\"" + feedUserName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * feed 中@提醒邮件模板
     *
     * @param emailTemplate
     * @param atUserName
     * @param feedUserName
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getFeedAtEmailContent(EmailTemplate emailTemplate, String atUserName, String feedUserName, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%atUserName%\":[\"" + atUserName + "\"]," +
                "\"%feedUserName%\":[\"" + feedUserName + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 评价公示邮件模板
     *
     * @param emailTemplate
     * @param name
     * @param period
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewFinishEmailContent(EmailTemplate emailTemplate, String name, String period, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%period%\":[\"" + period + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒新建目标周期邮件
     *
     * @param emailTemplate
     * @param name
     * @param objectiveType      目标类型（个人、团队、公司）
     * @param objectivePeriod    目标周期
     * @param daysBeforeExpired  距目标周期到期的天数
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getOKRPeriodDeadlineReminderEmailContent(EmailTemplate emailTemplate, String name, String objectiveType, String objectivePeriod, String daysBeforeExpired, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%objectiveType%\":[\"" + objectiveType + "\"]," +
                "\"%objectivePeriod%\":[\"" + objectivePeriod + "\"]," +
                "\"%daysBeforeExpired%\":[\"" + daysBeforeExpired + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒更新目标邮件
     *
     * @param emailTemplate
     * @param name
     * @param objectiveName               目标名称
     * @param content             目标周期
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getOKRRegularReminderEmailContent(EmailTemplate emailTemplate, String name, String objectiveName,
                                                           String content,  String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%objectiveName%\":[\"" + objectiveName + "\"]," +
                "\"%content%\":[\"" + content + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒目标将过期邮件
     *
     * @param emailTemplate
     * @param name
     * @param objectiveName      目标名称
     * @param completion         完成度
     * @param deadline           截止日
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getOKRDeadlineReminderEmailContent(EmailTemplate emailTemplate, String name, String objectiveName, String completion, String daysBeforeExpired, String deadline, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%objectiveName%\":[\"" + objectiveName + "\"]," +
                "\"%completion%\":[\"" + completion + "\"]," +
                "\"%deadline%\":[\"" + deadline + "\"]," +
                "\"%daysBeforeExpired%\":[\"" + daysBeforeExpired + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    public static String getOKRUpdateEmailContent(EmailTemplate emailTemplate, String name, String objectiveName, String updateContent, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%objectiveName%\":[\"" + objectiveName + "\"]," +
                "\"%updateContent%\":[\"" + updateContent + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 提醒填写入职信息邮件模板
     *
     * @param emailTemplate
     * @param name
     * @param url
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getOnboardingRemindEmailContent(EmailTemplate emailTemplate, String name, String url, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%url%\":[\"" + url + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 评价活动取消邮件模板
     *
     * @param emailTemplate
     * @param name
     * @param period
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewCancelEmailContent(EmailTemplate emailTemplate, String name, String period, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%period%\":[\"" + period + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 评价活动取消并备份邮件模板
     *
     * @param emailTemplate
     * @param name
     * @param period
     * @param content
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getReviewCancelAndBackupEmailContent(EmailTemplate emailTemplate, String name, String period, String content, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%period%\":[\"" + period + "\"]," +
                "\"%content%\":[\"" + content + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     *
     */
    public static String getTrialApplicationEmailContent(EmailTemplate emailTemplate, String userTitle, String userMobile, String companyName, String userEmail, String userWechat, String userName, String companySize, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%userTitle%\":[\"" + userTitle + "\"]," +
                "\"%userMobile%\":[\"" + userMobile + "\"]," +
                "\"%userEmail%\":[\"" + userEmail + "\"]," +
                "\"%userWechat%\":[\"" + userWechat + "\"]," +
                "\"%companyName%\":[\"" + companyName + "\"]," +
                "\"%companySize%\":[\"" + companySize + "\"]," +
                "\"%userName%\":[\"" + userName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";
        return message;
    }


    /**
     *
     */
    public static String getWarrentManualOperationEmailContent(EmailTemplate emailTemplate, String submitterEmail, String url, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%submitterEmail%\":[\"" + submitterEmail + "\"]," +
                "\"%url%\":[\"" + url + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";
        return message;
    }


    /**
     * 离职通知邮件模板
     *
     * @param emailTemplate
     * @param userName
     * @param userEmail
     * @param name
     * @param teamName
     * @param userJobTitle
     * @param resignType
     * @param resignDate
     * @param resignDescription
     * @param orgShortName
     * @param targetEmailAddress
     * @return
     */
    public static String getResignNotificationEmailContent(EmailTemplate emailTemplate, String userName,
                                                           String userEmail, String name, String teamName,
                                                           String userJobTitle, String resignType, String resignDate,
                                                           String resignDescription, String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%userName%\":[\"" + userName + "\"]," +
                "\"%userEmail%\":[\"" + userEmail + "\"]," +
                "\"%teamName%\":[\"" + teamName + "\"]," +
                "\"%userJobTitle%\":[\"" + userJobTitle + "\"]," +
                "\"%resignType%\":[\"" + resignType + "\"]," +
                "\"%resignDate%\":[\"" + resignDate + "\"]," +
                "\"%resignDescription%\":[\"" + resignDescription + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";
        return message;
    }

    public static String getTransferNotificationEmailContent(EmailTemplate emailTemplate, String name, String userName,
                                                             String userEmail, String beforeTeamName, String afterTeamName,
                                                             String beforeReporterName, String afterReporterName, String beforeJobTitle,
                                                             String afterJobTitle, String beforeJobLevel, String afterJobLevel,
                                                             String transferType, String transferDate, String transferDescription,
                                                             String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%userName%\":[\"" + userName + "\"]," +
                "\"%userEmail%\":[\"" + userEmail + "\"]," +
                "\"%beforeTeamName%\":[\"" + beforeTeamName + "\"]," +
                "\"%afterTeamName%\":[\"" + afterTeamName + "\"]," +
                "\"%beforeReporterName%\":[\"" + beforeReporterName + "\"]," +
                "\"%afterReporterName%\":[\"" + afterReporterName + "\"]," +
                "\"%beforeJobTitle%\":[\"" + beforeJobTitle + "\"]," +
                "\"%afterJobTitle%\":[\"" + afterJobTitle + "\"]," +
                "\"%beforeJobLevel%\":[\"" + beforeJobLevel + "\"]," +
                "\"%afterJobLevel%\":[\"" + afterJobLevel + "\"]," +
                "\"%transferType%\":[\"" + transferType + "\"]," +
                "\"%transferDate%\":[\"" + transferDate + "\"]," +
                "\"%transferDescription%\":[\"" + transferDescription + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    public static String getPassProbationEmailContent(EmailTemplate emailTemplate, String name, String userName,
                                                      String userEmail, String teamName, String userJobTitle,
                                                      String passprobationType, String passProbationDate, String passProbationDescription,
                                                      String orgShortName, String targetEmailAddress) {
        String templateInvokeName = emailTemplate.emailType;
        String message = "{\"templateInvokeName\":\"" + templateInvokeName + "\"," +
                "\"xsmtpapi\":{\"sub\":{\"%name%\":[\"" + name + "\"]," +
                "\"%userName%\":[\"" + userName + "\"]," +
                "\"%userEmail%\":[\"" + userEmail + "\"]," +
                "\"%teamName%\":[\"" + teamName + "\"]," +
                "\"%userJobTitle%\":[\"" + userJobTitle + "\"]," +
                "\"%passProbationType%\":[\"" + passprobationType + "\"]," +
                "\"%passProbationDate%\":[\"" + passProbationDate + "\"]," +
                "\"%passProbationDescription%\":[\"" + passProbationDescription + "\"]," +
                "\"%orgShortName%\":[\"" + orgShortName + "\"]},\"to\":[\"" + targetEmailAddress + "\"]}}";

        return message;
    }

    /**
     * 处理发送邮件json字符串,使其与云端邮件模板相一致
     *
     * @param emailTemplate
     * @param message
     * @return
     */
    private static String handleMessage(EmailTemplate emailTemplate, String message) {
        String result;
        List<String> sendCloudParamList = EmailTemplateHelper.getSendCloudTemplateParamList(emailTemplate);
        Set<String> paramNameSet = EmailTemplateHelper.getStringBetweenPercentSign(message);
        List<String> paramNameList = new ArrayList<>();
        paramNameList.addAll(paramNameSet);
        result = EmailTemplateHelper.handleMessageByCompareStr(sendCloudParamList, paramNameList, message);
        return result;
    }

}
