package hr.wozai.service.user.server.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.utils.ConvrUtils;
import hr.wozai.service.user.server.dao.conversation.ConvrScheduleTaskDao;
import hr.wozai.service.user.server.dao.userorg.CoreUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by wangbin on 2016/11/30.
 */
@Component
public class ConvrScheduleTask {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConvrScheduleTask.class);

    @Value("${url.host}")
    private String url;

    @Autowired
    private ConvrScheduleTaskDao convrScheduleTaskDao;

    @Autowired
    private OrgDao orgDao;


    @Autowired
    @Qualifier("messageCenterFacadeProxy")
    private ThriftClientProxy messageCenterFacadeProxy;

    private MessageCenterFacade messageCenterFacade;

    @Autowired
    private CoreUserProfileDao coreUserProfileDao;

    @Autowired
    private EmailTemplateHelper emailTemplateHelper;

    private Map<String, String> fixedParamsMap;
    private List<String> dynamicParamSeq;
    private List<List<String>> dynamicParams;

    @PostConstruct
    public void init() throws Exception{
        dynamicParamSeq = new ArrayList<>();
        dynamicParamSeq.add("userName");
        dynamicParamSeq.add("dstEmailAddress");
        dynamicParamSeq.add("users");
        url += "#/conversation/initiate";
        messageCenterFacade = (MessageCenterFacade) messageCenterFacadeProxy.getObject();
    }

    @Scheduled(cron = "0 50 7 * * ? ")
    public void sendConvrScheduleNotification() {

        List<Org> orgList = orgDao.listAllOrgs();
        Date date = new Date();
        Integer dayOfWeek = TimeUtils.getDayOfWeekFromDate(date);
        for (Org org : orgList) {
            List<ConvrSchedule> reminderNeeded = new ArrayList<>();
            //存放所有userId
            Set<Long> userIds = new HashSet<>();
            //source与target对应关系map
            Map<Long, Set<Long>> sourceAndTargetMap = new HashMap<>();
            dynamicParams = new ArrayList<>();
            long orgId = org.getOrgId();
            String orgShortName = org.getShortName();
            fixedParamsMap = new HashMap<>();
            fixedParamsMap.put("orgShortName", orgShortName);
            fixedParamsMap.put("url", url);

            List<ConvrSchedule> convrSchedules = convrScheduleTaskDao.listConvrScheduleByOrgId(orgId);
            if (CollectionUtils.isEmpty(convrSchedules)) {
                continue;
            }
            for (ConvrSchedule convrSchedule : convrSchedules) {
                Integer periodType = convrSchedule.getPeriodType();
                Integer remindDay = convrSchedule.getRemindDay();
                //如果是每周提醒
                if (periodType.equals(PeriodType.EVERY_WEEK.getCode()) && dayOfWeek.equals(remindDay)) {
                    handleRelationInfo(reminderNeeded, convrSchedule, userIds, sourceAndTargetMap);
                    //如果是每半月提醒
                } else if (periodType.equals(PeriodType.HALF_MONTH.getCode()) && ConvrUtils.isTimeTohalfMonthReminder(date, remindDay)) {
                    handleRelationInfo(reminderNeeded, convrSchedule, userIds, sourceAndTargetMap);
                    //如果是每月提醒
                } else if (periodType.equals(PeriodType.EVERY_MONTH.getCode()) && ConvrUtils.isTimeToEveryMonthReminder(date, remindDay)) {
                    handleRelationInfo(reminderNeeded, convrSchedule, userIds, sourceAndTargetMap);
                } else {
                    continue;
                }
            }

            if (CollectionUtils.isEmpty(reminderNeeded)) {
                continue;
            }

            List<CoreUserProfile> coreUserProfiles = coreUserProfileDao.listCoreUserProfileByOrgIdAndUserId(orgId, new ArrayList<>(userIds));
            Map<Long, CoreUserProfile> userIdAndCoreUserProfileMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(coreUserProfiles)) {
                for (CoreUserProfile coreUserProfile : coreUserProfiles) {
                    long userId = coreUserProfile.getUserId();
                    userIdAndCoreUserProfileMap.put(userId, coreUserProfile);
                }
            }

            for (ConvrSchedule convrSchedule : reminderNeeded) {
                List<String> dynamicParam = new ArrayList<>();
                long sourceUserId = convrSchedule.getSourceUserId();
                CoreUserProfile sourceUserProfile = userIdAndCoreUserProfileMap.get(sourceUserId);
                if (null != sourceUserProfile) {
                    dynamicParam.add(sourceUserProfile.getFullName());
                    dynamicParam.add(sourceUserProfile.getEmailAddress());
                } else {
                    continue;
                }
                if (sourceAndTargetMap.containsKey(sourceUserId)) {
                    Set<Long> targetUserIds = sourceAndTargetMap.get(sourceUserId);
                    StringBuilder sb = new StringBuilder();
                    Set<CoreUserProfile> users = new HashSet<>();
                    for (Long id : targetUserIds) {
                        sb.append(userIdAndCoreUserProfileMap.get(id).getFullName() + " ");
                    }
                    dynamicParam.add(sb.toString());
                }
                dynamicParams.add(dynamicParam);
            }

            sendConvrScheduleReminderMail(dynamicParamSeq, dynamicParams, fixedParamsMap);
            sendConvrScheduleReminderMessage(orgId,sourceAndTargetMap);

        }
    }

    private void sendConvrScheduleReminderMail(List<String> dynamicParamSeq, List<List<String>> dynamicParams,
                                               Map<String, String> fixedParamsMap) {
        BatchEmail batchEmail = new BatchEmail();
        batchEmail.setFixedParamsMap(fixedParamsMap);
        batchEmail.setDynamicParamSeq(dynamicParamSeq);
        batchEmail.setDynamicParams(dynamicParams);
        batchEmail.setEmailTemplate(EmailTemplate.CONVR_SCHEDULE_REMINDER);

        emailTemplateHelper.preBatchSendEmail(batchEmail);
    }

    private void sendConvrScheduleReminderMessage(long orgId, Map<Long, Set<Long>> sourceAndTargetMap) {

        for (Map.Entry<Long, Set<Long>> entry : sourceAndTargetMap.entrySet()) {
            Long receiverId = entry.getKey();
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setOrgId(orgId);
            messageDTO.setTemplateId(MessageTemplate.CONVR_SCHEDULE_REMINDER.getCode());
            messageDTO.setReceiverId(receiverId);
            messageDTO.setObjectId(receiverId);
            String targetUserIdSet = JSON.toJSONString(entry.getValue());
            JSONObject usersJson = new JSONObject();
            usersJson.put("users", targetUserIdSet);
            messageDTO.setObjectContent(usersJson.toJSONString());


            VoidDTO result = messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(receiverId));
            LOGGER.info("sendConvrScheduleReminderMessage()-result{}" + result.getServiceStatusDTO());

        }

    }

















    private void handleRelationInfo(List<ConvrSchedule> reminderNeeded, ConvrSchedule convrSchedule, Set<Long> userIds,
                        Map<Long, Set<Long>> sourceAndTargetMap) {

        reminderNeeded.add(convrSchedule);
        //将userId收集起来
        long sourceUserId = convrSchedule.getSourceUserId();
        long targetUserId = convrSchedule.getTargetUserId();
        userIds.add(targetUserId);
        userIds.add(sourceUserId);
        //将source及target的对应关系存入map
        if (sourceAndTargetMap.containsKey(sourceUserId)) {
            sourceAndTargetMap.get(sourceUserId).add(targetUserId);
        } else {
            Set<Long> targetUserIds = new HashSet<>();
            targetUserIds.add(targetUserId);
            sourceAndTargetMap.put(sourceUserId, targetUserIds);
        }
    }







}
