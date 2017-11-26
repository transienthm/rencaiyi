// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.thirdparty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.vo.messagecenter.*;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.feed.client.dto.FeedDTO;
import hr.wozai.service.feed.client.dto.FeedListDTO;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.okr.dto.ObjectiveDTO;
import hr.wozai.service.user.client.okr.dto.ObjectiveListDTO;
import hr.wozai.service.user.client.okr.dto.OkrLogDTO;
import hr.wozai.service.review.client.dto.ReviewTemplateDTO;
import hr.wozai.service.review.client.dto.ReviewTemplateListDTO;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.dto.MessageListDTO;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;


@Controller("messageCenterController")
public class MessageCenterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCenterController.class);

    private final String URL_OKR_PREFIX = "#/okr/objectives/";
    private final String URL_OKR_POSTFIX = "?type=2&teamId=";
    private final String URL_REVIEW_PREFIX = "#/review/item/";
    private final String URL_REVIEW_PEER = "#/review/peer";
    private final String URL_REVIEW_HOMEPAGE = "#/review/index/";
    private final String URL_REVIEW_POSTFIX_ACTIVITY = "/activities";
    private final String URL_REVIEW_POSTFIX_INVITATION = "/invitations";
    private final String URL_USER_PREFIX = "#/user/";
    private final String URL_NEWSFEED_PREFIX = "#/newsfeed/";
    private final String URL_ONBOARDING_REJECT = "#/onboarding/staff/fill";
    private final String URL_ONBOARDING_SUBMIT_PREFIX = "#/team/staffProfile/";
    private final String URL_HASHTAG = "#/";
    private final String URL_CONVR_SCHEDULE = "#/conversation/initiate";
    private final String URL_SURVEY_ACTIVITY = "#/survey/activity";

    
    @Autowired
    FacadeFactory facadeFactory;

    @LogAround

    @RequestMapping(value = "/messages/get-unread-number", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> getUnReadMessageNumber() {
        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();

        Result<Object> result = new Result<>();

        IntegerDTO remoteResult = facadeFactory.getMessageCenterFacade().getUnReadMessageNumber(orgId, actorUserId, Long.MIN_VALUE);
        ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
            throw new ServiceStatusException(serviceStatus);
        }

        result.setCodeAndMsg(serviceStatus);
        result.setData(remoteResult.getData());

        return result;

    }


    @LogAround

    @RequestMapping(value = "/messages", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listMessage(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {

        Result<Object> result = new Result<>();

        Map<String, List<MessageVO>> maps = new LinkedHashMap<>();

        List<Map> resultList = new ArrayList<>();
        List<MessageVO> messageVOs;
        MessageListVO messageListVO;

        messageListVO = getMessageVOList(pageNumber, pageSize);
        messageVOs = messageListVO.getMessageVOs();


        for (MessageVO messageVO : messageVOs) {
            String date = getDate(messageVO.getCreatedTime());
            if (maps.containsKey(date)) {
                List<MessageVO> exist = maps.get(date);
                exist.add(messageVO);
                maps.put(date, exist);
            } else {
                List<MessageVO> newOne = new ArrayList<>();
                newOne.add(messageVO);
                maps.put(date, newOne);
            }
        }

        for (Map.Entry entry : maps.entrySet()) {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("date", entry.getKey());
            resultMap.put("messages", entry.getValue());
            resultList.add(resultMap);
        }

        Map<String, Object> lastMap = new HashMap<>();
        lastMap.put("messageObject", resultList);
        lastMap.put("totalNumber", messageListVO.getTotalNumber());

        result.setData(lastMap);
        result.setCodeAndMsg(ServiceStatus.COMMON_OK);
        return result;
    }

    private MessageListVO getMessageVOList(Integer pageNumber, Integer pageSize) {

        MessageListVO result = new MessageListVO();
        List<MessageVO> messageVOs = new ArrayList<>();
        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
        if (!isValid) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        MessageListDTO remoteResult = facadeFactory.getMessageCenterFacade().listAllMessages(orgId, actorUserId,
                Long.MIN_VALUE, pageNumber, pageSize);
        ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
            throw new ServiceStatusException(serviceStatus);
        }

        if (remoteResult.getMessageDTOs().size() == 0) {
            result.setMessageVOs(messageVOs);
            result.setTotalNumber(remoteResult.getTotalNumber());
            return result;
        }

        List<Long> feedIds = new ArrayList<>();
        List<Long> reviewIds = new ArrayList<>();
        List<Long> objectiveIds = new ArrayList<>();
        // List<Long> okrLogIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        Set<Long> userIdSet = new HashSet<>();
        List<Long> statusUpdateIds = new ArrayList<>();
        Set<Long> statusUpdateIdSet = new HashSet<>();
        List<Long> jobTransferIds = new ArrayList<>();
        Set<Long> jobTransferIdSet = new HashSet<>();
        Map<Long, Set<Long>> convrScheduleReceiverAndTargetMap = new HashMap<>();

        // 遍历获取不同的对象ID
        for (MessageDTO messageDTO : remoteResult.getMessageDTOs()) {
            MessageVO messageVO = new MessageVO();
            List<Long> users = messageDTO.getSenders();
            BeanHelper.copyPropertiesHandlingJSON(messageDTO, messageVO);
            //messageVO.setSenders(users);
            if (null != users && users.size() > 0) {
                setUserIdList(userIdSet, users, messageVO);
            }
            List<Long> senders = handleIllegalUserId(users);
            messageVO.setSenders(senders);
            long objectId = messageDTO.getObjectId();
            int templateId = messageDTO.getTemplateId();
            long reviewTemplateId = -1l;
            MessageTemplate messageTemplate = MessageTemplate.getEnumByCode(templateId);
            if (messageTemplate != null) {
                messageVO.setMessageType(messageTemplate.getMessageType());
                messageVO.setTemplate(messageTemplate.getContent());
                messageVOs.add(messageVO);
                switch (messageTemplate) {
                    case NEWS_FEED_AT:
                    case NEWS_FEED_DIANZAN:
                    case NEWS_FEED_COMMENT:
                    case NEWS_FEED_COMMENT_AT:
                        feedIds.add(objectId);
                        break;
                    case ONBOARDING_SUBMIT:
                    case ONBOARDING_REJECT:
                    case ONBOARDING_APPROVE:
                        break;
                    case REVIEW_ACTIVITY_BEGIN:
                    case REVIEW_ACTIVITY_CANCEL:
                    case REVIEW_ONGOING:
                    case REVIEW_FINISH:
                        JSONObject reviewTemplate = JSONObject.parseObject(messageDTO.getObjectContent());
                        if (reviewTemplate.get("templateId") != null) {
                            reviewTemplateId = Long.parseLong(reviewTemplate.get("templateId").toString());
                            reviewIds.add(reviewTemplateId);
                        } else {
                            LOGGER.error("Cannot parse ReviewTemplateId-error");
                        }
                        break;
                    case REVIEW_PEER_BATCH_INVITE:
                    case REVIEW_MANAGER_INVITE:
                    case REVIEW_PEER_NOTIFY_MANAGER:
                        break;
                    case OKR_UPDATE:
                    case OKR_ADD:
                        objectiveIds.add(objectId);
                        break;
                    /*case OKR_LOG_AT:
                    case OKR_LOG_COMMENT:
                        okrLogIds.add(objectId);
                        break;*/
                    case TRANSFER_NOTIFICATION:
                        jobTransferIdSet.add(objectId);
                        break;
                    case RESIGN_NOTIFICATION:
                    case PASS_PROBATION_TO_DIRECTOR:
                    case PASS_PROBATION_TO_STAFF:
                        statusUpdateIdSet.add(objectId);
                        break;
                    case CONVR_SCHEDULE_REMINDER:
                        updateUserIdSetForConvrSchedule(convrScheduleReceiverAndTargetMap, userIdSet, messageDTO);
                        break;
                    default:
                        break;
                }
            }
        }


        // objective map
        ObjectiveListDTO objectiveDTOs = new ObjectiveListDTO();
        if (objectiveIds.size() > 0) {
            objectiveDTOs = facadeFactory.getOkrFacade().listObjectivesByObjectiveIds(orgId, objectiveIds, actorUserId, adminUserId);
        }
        Map<Long, ObjectiveDTO> objectiveMap = generateObjectiveMap(objectiveDTOs.getObjectiveDTOList());

        // okr log map
       /* OkrLogListDTO okrLogDTOs = new OkrLogListDTO();
        if (okrLogIds.size() > 0) {
            okrLogDTOs = facadeFactory.getOkrFacade().listOkrLogsByOkrLogIds(orgId, okrLogIds, actorUserId, adminUserId);
        }
        Map<Long, OkrLogDTO> okrLogMap = generateOkrLogMap(okrLogDTOs.getOkrLogDTOList());
*/
        //feed map
        FeedListDTO feedListDTO = new FeedListDTO();
        if (feedIds.size() > 0) {
            feedListDTO = facadeFactory.getFeedFacade().listFeedByFeedIds(orgId, feedIds, actorUserId, adminUserId);
        }
        // FeedListDTO feedListDTO = new FeedListDTO();
        Map<Long, FeedDTO> feedMap = generateFeedMap(feedListDTO.getFeedDTOList());

        //review map
        ReviewTemplateListDTO reviewTemplateListDTO = new ReviewTemplateListDTO();
        if (reviewIds.size() > 0) {
            reviewTemplateListDTO = facadeFactory.getReviewTemplateFacade().listReviewTemplateByTemplateIds(orgId, reviewIds, actorUserId, adminUserId);
        }
        Map<Long, ReviewTemplateDTO> reviewMap = generateReviewTemplateMap(reviewTemplateListDTO.getReviewTemplateDTOs());

        //statusUpdate map
        StatusUpdateListDTO statusUpdateListDTO = new StatusUpdateListDTO();
        statusUpdateIds.addAll(statusUpdateIdSet);
        if (statusUpdateIds.size() > 0) {
            statusUpdateListDTO = facadeFactory.getUserProfileFacade().listStatusUpdateByStatusUpdateIds(orgId, statusUpdateIds, actorUserId, adminUserId);
            for (StatusUpdateDTO statusUpdateDTO : statusUpdateListDTO.getStatusUpdateDTOs()) {
                long userId = statusUpdateDTO.getUserId();
                userIdSet.add(userId);
            }
        }
        Map<Long, StatusUpdateDTO> statusUpdateMap = generateStatusUpdateMap(statusUpdateListDTO.getStatusUpdateDTOs());

        //jobTransfer map
        JobTransferResponseListDTO jobTransferResponseListDTO = new JobTransferResponseListDTO();
        jobTransferIds.addAll(jobTransferIdSet);
        if (jobTransferIds.size() > 0) {
            jobTransferResponseListDTO = facadeFactory.getUserProfileFacade().listJobTransferByJobTransferIds(orgId, jobTransferIds, actorUserId, adminUserId);
            for (JobTransferResponseDTO jobTransferResponseDTO : jobTransferResponseListDTO.getJobTransferDTOs()) {
                long userId = jobTransferResponseDTO.getUserId();
                userIdSet.add(userId);
            }
        }
        Map<Long, JobTransferResponseDTO> jobTransferMap = generateJobTransferMap(jobTransferResponseListDTO.getJobTransferDTOs());


        userIds.addAll(userIdSet);
        //user map
        CoreUserProfileListDTO userProfiles = new CoreUserProfileListDTO();
        if (userIds.size() > 0) {
            userProfiles = facadeFactory.getUserProfileFacade().listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
        }
        Map<Long, CoreUserProfileDTO> coreUserProfileMap = generateUserProfileMap(userProfiles.getCoreUserProfileDTOs());

        for (MessageVO messageVO : messageVOs) {
            List<Long> users = messageVO.getSenders();
            messageVO.setUsers(getCoreUserProfileVO(users, coreUserProfileMap));

            long objectId = messageVO.getObjectId();
            int templateId = messageVO.getTemplateId();
            MessageTemplate messageTemplate = MessageTemplate.getEnumByCode(templateId);
            messageVO.setMessageType(messageTemplate.getMessageType().intValue());
            messageVO.setTemplate(messageTemplate.getContent());
            switch (messageTemplate) {
                case NEWS_FEED_AT:
                case NEWS_FEED_DIANZAN:
                case NEWS_FEED_COMMENT:
                case NEWS_FEED_COMMENT_AT:
                    setContentAndUrlWithFeed(objectId, feedMap, messageVO);
                    break;
                case ONBOARDING_SUBMIT:
                case ONBOARDING_REJECT:
                case ONBOARDING_APPROVE:
                    setUrlWithOnboarding(messageVO);
                    break;
                case REVIEW_ACTIVITY_BEGIN:
                case REVIEW_ACTIVITY_CANCEL:
                case REVIEW_FINISH:
                case REVIEW_PEER_BATCH_INVITE:
                case REVIEW_MANAGER_INVITE:
                case REVIEW_PEER_NOTIFY_MANAGER:
                case REVIEW_ONGOING:
                    setContentAndUrlWithReviewTemplate(objectId, reviewMap, messageVO);
                    break;
                case OKR_UPDATE:
                case OKR_ADD:
                    setContentAndUrlWithObjective(objectId, objectiveMap, messageVO);
                    break;
                /*case OKR_LOG_AT:
                case OKR_LOG_COMMENT:
                    setContentAndUrlWithOkrLog(objectId, okrLogMap, messageVO);
                    break;*/
                case RESIGN_NOTIFICATION:
                case PASS_PROBATION_TO_DIRECTOR:
                case PASS_PROBATION_TO_STAFF:
                    setContentAndUrlWithStatusUpdate(objectId, statusUpdateMap, coreUserProfileMap, messageVO);
                    break;
                case TRANSFER_NOTIFICATION:
                    setContentAndUrlWithJobTransfer(objectId, jobTransferMap, coreUserProfileMap, messageVO);
                    break;
                case CONVR_SCHEDULE_REMINDER:
                    setContentAndUrlForConvrSchedule(convrScheduleReceiverAndTargetMap,coreUserProfileMap, messageVO);
                    break;
                case SURVEY_PUSH:
                    setContentAndUrlForSurveyPush(messageVO);
                    break;
                default:
                    break;
            }
        }
        result.setMessageVOs(messageVOs);
        result.setTotalNumber(remoteResult.getTotalNumber());
        return result;
    }


    @LogAround

    @RequestMapping(value = "messages/live", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listLiveMessages() {

        Result<Object> result = new Result<>();
        List<MessageVO> messageVOs = null;
        MessageListVO messageListVO = new MessageListVO();
        try {
            messageListVO = getMessageVOList(1, 3);
            messageVOs = messageListVO.getMessageVOs();
        } catch (ServiceStatusException e) {
            result.setCodeAndMsg(e.getServiceStatus());
            LOGGER.error("listLiveMessages()-error:{}", e);
        }
        result.setCodeAndMsg(ServiceStatus.COMMON_OK);
        result.setData(messageVOs);
        return result;
    }


    private void setUserIdList(Set<Long> userIdSet, List<Long> users, MessageVO messageVO) throws NumberFormatException {
        for (Long s : users) {
            if (s == null || s.toString().equals("") || s == 0) {
                continue;
            }
            try {
                long id = s;
                userIdSet.add(id);
            } catch (Exception e) {
                LOGGER.error("setUserIdList()-error", e);
                continue;
            }
        }
    }

    private Map<Long, CoreUserProfileDTO> generateUserProfileMap(List<CoreUserProfileDTO> coreUserProfileDTOs) {
        Map<Long, CoreUserProfileDTO> result = new HashMap<>();
        if (null == coreUserProfileDTOs || coreUserProfileDTOs.size() == 0) {
            return result;
        }
        for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            Long key = coreUserProfileDTO.getUserId();
            result.put(key, coreUserProfileDTO);
        }
        return result;
    }

    private List<CoreUserProfileVO> getCoreUserProfileVO(List<Long> userIds, Map<Long, CoreUserProfileDTO> map) {
        List<CoreUserProfileVO> result = new ArrayList<>();
        if (userIds == null) {
            return result;
        }
        for (Long s : userIds) {
            if (map.containsKey(s)) {
                CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                CoreUserProfileDTO coreUserProfileDTO = map.get(s);
                BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
                result.add(coreUserProfileVO);
            } else {
                LOGGER.error("Cannot find profile with user id:{}", s);
            }
        }
        return result;
    }

    private Map<Long, ObjectiveDTO> generateObjectiveMap(List<ObjectiveDTO> objectiveDTOs) {
        Map<Long, ObjectiveDTO> result = new HashMap<>();
        if (null == objectiveDTOs || objectiveDTOs.size() == 0) {
            return result;
        }
        for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
            long key = objectiveDTO.getObjectiveId();
            result.put(key, objectiveDTO);
        }
        return result;
    }

    private void setContentAndUrlWithObjective(long key, Map<Long, ObjectiveDTO> map, MessageVO messageVO) {
        String url = null;
        if (map.containsKey(key) && map.get(key) != null) {
            ObjectiveDTO objectiveDTO = map.get(key);
            SimpleObjectVO s = new SimpleObjectVO();
            BeanUtils.copyProperties(objectiveDTO, s);
            messageVO.setObjectContent((JSONObject) JSONObject.toJSON(s));
            url = URL_OKR_PREFIX + encryptId("OKR", objectiveDTO.getObjectiveId()) + URL_OKR_POSTFIX
                    + encryptId("OKR", objectiveDTO.getOwnerId());
        } else if (messageVO.getObjectContent() != null) {
            JSONObject objectContent = messageVO.getObjectContent();
            long objectiveId = objectContent.getLong("objectiveId");
            long teamId = objectContent.getLong("ownerId");
            url = URL_OKR_PREFIX + encryptId("OKR", objectiveId) + URL_OKR_POSTFIX + encryptId("OKR", teamId);
            String content = objectContent.getString("content");
            JSONObject contentObject = new JSONObject();
            contentObject.put("content", content);
            messageVO.setObjectContent(contentObject);

        }

        messageVO.setLinkURL(url);
        messageVO.setLinkText("点击查看");
    }

    private void updateUserIdSetForConvrSchedule(Map<Long, Set<Long>> map, Set<Long> userIdSet, MessageDTO messageDTO) {
        JSONObject usersObject = (JSONObject) JSONObject.parse(messageDTO.getObjectContent());
        String setJsonString = (String) usersObject.get("users");
        Set<Long> users = JSON.parseObject(setJsonString, Set.class);
        Set<Long> newUsers = new HashSet<>();
        for (Object id : users) {
            Long idLong;
            if (id instanceof Integer) {
                Integer idInteger = (Integer) id;
                idLong = idInteger.longValue();
            } else {
                idLong = (Long) id;
            }
            userIdSet.add(idLong);
            newUsers.add(idLong);
        }
        map.put(messageDTO.getReceiverId(), newUsers);
    }

    private Map<Long, OkrLogDTO> generateOkrLogMap(List<OkrLogDTO> okrLogDTOs) {
        Map<Long, OkrLogDTO> result = new HashMap<>();
        if (null == okrLogDTOs || okrLogDTOs.size() == 0) {
            return result;
        }
        for (OkrLogDTO okrLogDTO : okrLogDTOs) {
            long key = okrLogDTO.getOkrLogId();
            result.put(key, okrLogDTO);
        }
        return result;
    }

    private void setContentAndUrlWithOkrLog(long key, Map<Long, OkrLogDTO> map, MessageVO messageVO) {
        if (map.containsKey(key) && map.get(key) != null) {
            OkrLogDTO okrLogDTO = map.get(key);
            SimpleObjectVO s = new SimpleObjectVO();
            BeanUtils.copyProperties(okrLogDTO, s);
            messageVO.setObjectContent((JSONObject) JSONObject.toJSON(s));
        }
        messageVO.setLinkURL(URL_HASHTAG);
        messageVO.setLinkText("点击查看");
    }

    private Map<Long, ReviewTemplateDTO> generateReviewTemplateMap(List<ReviewTemplateDTO> reviewTemplateDTOs) {
        Map<Long, ReviewTemplateDTO> result = new HashMap<>();
        if (null == reviewTemplateDTOs || reviewTemplateDTOs.size() == 0) {
            return result;
        }
        for (ReviewTemplateDTO reviewTemplateDTO : reviewTemplateDTOs) {
            long key = reviewTemplateDTO.getTemplateId();
            result.put(key, reviewTemplateDTO);
        }
        return result;
    }

    private Map<Long, StatusUpdateDTO> generateStatusUpdateMap(List<StatusUpdateDTO> statusUpdateDTOs) {
        Map<Long, StatusUpdateDTO> result = new HashMap<>();
        if (null == statusUpdateDTOs || statusUpdateDTOs.size() == 0) {
            return result;
        }
        for (StatusUpdateDTO statusUpdateDTO : statusUpdateDTOs) {
            long key = statusUpdateDTO.getStatusUpdateId();
            result.put(key, statusUpdateDTO);
        }
        return result;
    }

    private Map<Long, JobTransferResponseDTO> generateJobTransferMap(List<JobTransferResponseDTO> jobTransferResponseDTOs) {
        Map<Long, JobTransferResponseDTO> result = new HashMap<>();
        if (null == jobTransferResponseDTOs || jobTransferResponseDTOs.size() == 0) {
            return result;
        }
        for (JobTransferResponseDTO jobTransferResponseDTO : jobTransferResponseDTOs) {
            long key = jobTransferResponseDTO.getJobTransferId();
            result.put(key, jobTransferResponseDTO);
        }
        return result;
    }

    private void setUrlWithOnboarding(MessageVO messageVO) {
        String url;
        String linkText;
        if (IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.ONBOARDING_REJECT.getCode())) {//补充信息
            url = URL_ONBOARDING_REJECT;
            linkText = "点击填写";
        } else if (IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.ONBOARDING_APPROVE.getCode())) {//完成入职
            url = URL_HASHTAG;
            linkText = "点击进入首页";
        } else if (IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.ONBOARDING_SUBMIT.getCode())) {//员工向你提交入职信息
            url = URL_ONBOARDING_SUBMIT_PREFIX + encryptId("ONBOARDING", messageVO.getObjectId());
            linkText = "点击查看";
        } else {
            url = URL_HASHTAG;
            linkText = "点击查看";
        }
        messageVO.setLinkURL(url);
        messageVO.setLinkText(linkText);
    }

    private void setContentAndUrlWithStatusUpdate(long key, Map<Long, StatusUpdateDTO> statusUpdateDTOMap, Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap, MessageVO messageVO) {
        if (statusUpdateDTOMap.containsKey(key) && statusUpdateDTOMap.get(key) != null) {
            Long userId;
            StatusUpdateDTO statusUpdateDTO = statusUpdateDTOMap.get(key);
            userId = statusUpdateDTO.getUserId();
            RTPVO rtpVO = new RTPVO();
            //如果userId已经在coreUserProfileDTOMap中
            if (coreUserProfileDTOMap.containsKey(userId) && coreUserProfileDTOMap.get(userId) != null) {
                CoreUserProfileDTO coreUserProfileDTO = coreUserProfileDTOMap.get(userId);
                rtpVO.setUserName(coreUserProfileDTO.getFullName());
            } else {
                LOGGER.error("setContentAndUrlWithJobTransfer()-error:{userName:null,userId:" + userId + "}");
            }

            if (null != statusUpdateDTO.getServiceStatusDTO()) {
                String date = TimeUtils.formatDateWithTimeZone(statusUpdateDTO.getUpdateDate(), TimeUtils.BEIJING);
                rtpVO.setDate(date);
            } else {
                String date = TimeUtils.formatDateWithTimeZone(statusUpdateDTO.getCreatedTime(), TimeUtils.BEIJING);
                rtpVO.setDate(date);
            }

            messageVO.setObjectContent((JSONObject) JSONObject.toJSON(rtpVO));
        }
        messageVO.setLinkURL(URL_HASHTAG);
        messageVO.setLinkText("点击查看");
    }

    private void setContentAndUrlWithJobTransfer(long key, Map<Long, JobTransferResponseDTO> jobTransferMap, Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap, MessageVO messageVO) {
        try {
            if (jobTransferMap.containsKey(key) && jobTransferMap.get(key) != null) {
                Long userId;
                JobTransferResponseDTO jobTransferResponseDTO = jobTransferMap.get(key);
                userId = jobTransferResponseDTO.getUserId();
                RTPVO rtpVO = new RTPVO();
                if (coreUserProfileDTOMap.containsKey(userId) && coreUserProfileDTOMap.get(userId) != null) {
                    CoreUserProfileDTO coreUserProfileDTO = coreUserProfileDTOMap.get(userId);
                    rtpVO.setUserName(coreUserProfileDTO.getFullName());
                } else {
                    LOGGER.error("setContentAndUrlWithJobTransfer()-error:{userName:null,userId:" + userId + "}");
                }
                String date = TimeUtils.formatDateWithTimeZone(jobTransferResponseDTO.getTransferDate(), TimeUtils.BEIJING);
                rtpVO.setDate(date);
                messageVO.setObjectContent((JSONObject) JSONObject.toJSON(rtpVO));
                messageVO.setLinkURL(userId.toString());
            }
            String url = URL_USER_PREFIX + encryptId("RTP", messageVO.getLinkURL());
            messageVO.setLinkURL(url);
            messageVO.setLinkText("点击查看");
        } catch (Exception e) {
            LOGGER.error("setContentAndUrlWithJobTransfer()-error", e);
        }
    }

    private void setContentAndUrlWithReviewTemplate(long key, Map<Long, ReviewTemplateDTO> map, MessageVO messageVO) {
        String url;
        if (map.containsKey(key) && map.get(key).getTemplateName() != null) {
            ReviewTemplateDTO reviewTemplateDTO = map.get(key);
            SimpleReviewVO s = new SimpleReviewVO();
            BeanUtils.copyProperties(reviewTemplateDTO, s);
            s.setPublicDeadline(getDate(reviewTemplateDTO.getPublicDeadline()));
            messageVO.setObjectContent((JSONObject) JSONObject.toJSON(s));
        }

        if (IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.REVIEW_ACTIVITY_BEGIN.getCode())
                || IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.REVIEW_FINISH.getCode())) {//请提交自评;公示
            url = URL_REVIEW_PREFIX + encryptId("REVIEW", messageVO.getObjectId()) + URL_REVIEW_POSTFIX_ACTIVITY;
        } else if (IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.REVIEW_ONGOING.getCode())
                || IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.REVIEW_MANAGER_INVITE.getCode())) {
            url = URL_REVIEW_PREFIX + encryptId("REVIEW", messageVO.getObjectId()) + URL_REVIEW_POSTFIX_INVITATION;
        } else if (IntegerUtils.equals(messageVO.getTemplateId(), MessageTemplate.REVIEW_PEER_BATCH_INVITE.getCode())
                || IntegerUtils.equals(messageVO.getTemplateId(),MessageTemplate.REVIEW_PEER_NOTIFY_MANAGER.getCode())) {
            url = URL_REVIEW_HOMEPAGE + encryptId("REVIEW", messageVO.getObjectId());
        } else {
            url = URL_REVIEW_PEER;
        }
        messageVO.setLinkURL(url);
        messageVO.setLinkText("点击查看");
    }

    private Map<Long, FeedDTO> generateFeedMap(List<FeedDTO> feedDTOs) {
        Map<Long, FeedDTO> result = new HashMap<>();
        if (null == feedDTOs || feedDTOs.size() == 0) {
            return result;
        }
        for (FeedDTO feedDTO : feedDTOs) {
            long key = feedDTO.getFeedId();
            result.put(key, feedDTO);
        }
        return result;
    }

    private void setContentAndUrlWithFeed(long key, Map<Long, FeedDTO> map, MessageVO messageVO) {
        if (map.containsKey(key) && map.get(key) != null) {
            FeedDTO feedDTO = map.get(key);
            SimpleObjectVO s = new SimpleObjectVO();
            BeanUtils.copyProperties(feedDTO, s);
            messageVO.setObjectContent((JSONObject) JSONObject.toJSON(s));
        }
        String url = URL_NEWSFEED_PREFIX + encryptId("NEWS_FEED", messageVO.getObjectId());
        messageVO.setLinkURL(url);
        messageVO.setLinkText("点击查看");
    }

    private void setContentAndUrlForConvrSchedule(Map<Long, Set<Long>> convrScheduleReceiverAndTargetMap, Map<Long, CoreUserProfileDTO> userProfileDTOMap, MessageVO messageVO) {
        long receiverId = messageVO.getReceiverId();
        List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
        if (convrScheduleReceiverAndTargetMap.containsKey(receiverId)) {
            Set<Long> targetUserSet = convrScheduleReceiverAndTargetMap.get(receiverId);
            for (Long targetUserId : targetUserSet) {
                CoreUserProfileDTO coreUserProfileDTO = userProfileDTOMap.get(targetUserId);
                CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
                coreUserProfileVOs.add(coreUserProfileVO);
            }
        }
        messageVO.setUsers(coreUserProfileVOs);
        String url = URL_CONVR_SCHEDULE;
        messageVO.setLinkURL(url);
        messageVO.setLinkText("点击查看");

    }

    private void setContentAndUrlForSurveyPush(MessageVO messageVO) {
        String url = URL_SURVEY_ACTIVITY;
        messageVO.setLinkURL(url);
        messageVO.setLinkText("点击这里");
    }
    private static String getDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(timestamp));
        return date;
    }

    private List<Long> handleIllegalUserId(List<Long> users) {
        List<Long> result = new ArrayList<>();
        if (users == null) {
            return null;
        }

        for (Long s : users) {
            if (s == null || s.toString().equals("") || s == 0) {
                continue;
            }
            try {
                result.add(s);
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }

    private String encryptId(String str, Object obj){
        String result = null;
        try {
            result = EncryptUtils.symmetricEncrypt(obj.toString());
            LOGGER.debug("messageType:" + str + " after encrypt:" + result);
        } catch (Exception e) {
            LOGGER.error("encryptId()-error:()", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
        return result.toUpperCase();
    }
}
