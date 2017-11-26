package hr.wozai.service.review.server.utils;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;


/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-19
 */
@Component
public class ReviewMessageUtils {

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  @Qualifier("userProfileFacadeProxy")
  private ThriftClientProxy userProfileFacadeProxy;

  UserProfileFacade userProfileFacade;

  @Autowired
  @Qualifier("messageCenterFacadeProxy")
  private ThriftClientProxy messageCenterFacadeProxy;

  private MessageCenterFacade messageCenterFacade;

  @PostConstruct
  public void init() throws Exception {
    userProfileFacade = (UserProfileFacade)userProfileFacadeProxy.getObject();
    messageCenterFacade = (MessageCenterFacade)messageCenterFacadeProxy.getObject();
  }

  @LogAround
  public void sendInvitationMessage(long orgId, long templateId, long invitationId,
                                    long revieweeId, long reviewerId,
                                    long actorUserId, long adminUserId) throws Exception {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(Arrays.asList(revieweeId));
    messageDTO.setTemplateId(MessageTemplate.REVIEW_ONGOING.getCode());
    messageDTO.setObjectId(invitationId);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(reviewerId));
  }

  @LogAround
  public void sendTemplateBeginMessage(long orgId, ReviewActivity reviewActivity, long revieweeId) throws Exception {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.REVIEW_ACTIVITY_BEGIN.getCode());
    messageDTO.setObjectId(reviewActivity.getActivityId());

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, reviewActivity.getTemplateId());
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(revieweeId));
  }

  @LogAround
  public void sendPublicMessage(long orgId, long templateId, long revieweeId,
                                long actorUserId, long adminUserId) throws Exception {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.REVIEW_FINISH.getCode());
    messageDTO.setObjectId(templateId);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(revieweeId));

  }

  @LogAround
  public void sendCancelMessage(long orgId, long templateId, long userId,
                                long actorUserId, long adminUserId) throws Exception {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.REVIEW_ACTIVITY_CANCEL.getCode());
    messageDTO.setObjectId(templateId);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(userId));
  }

  @LogAround
  public void sendReviewPeerBatchInviteMessage(
      long orgId, long templateId, List<Long> peerUserIds, long reviewerUserId) {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(peerUserIds);
    messageDTO.setTemplateId(MessageTemplate.REVIEW_PEER_BATCH_INVITE.getCode());
    messageDTO.setObjectId(templateId);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("peerCount", peerUserIds.size());
    messageDTO.setObjectContent(jsonObject.toString());

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(reviewerUserId));
  }

  @LogAround
  public void sendReviewManagerBatchInviteMessage(
      long orgId, long templateId, List<Long> subUserIds, long managerUserId) {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(subUserIds);
    messageDTO.setTemplateId(MessageTemplate.REVIEW_MANAGER_INVITE.getCode());
    messageDTO.setObjectId(templateId);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("peerCount", subUserIds.size());
    messageDTO.setObjectContent(jsonObject.toString());

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(managerUserId));
  }

  @LogAround
  public void sendPeerReviewNotifyManagerMessage(
      long orgId, long templateId, long reviewerUserId, long revieweeUserId, long managerUserId) {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(Arrays.asList(reviewerUserId));
    messageDTO.setTemplateId(MessageTemplate.REVIEW_PEER_NOTIFY_MANAGER.getCode());
    messageDTO.setObjectId(templateId);

    CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(orgId, revieweeUserId, -1, -1);
    if (ServiceStatus.COMMON_OK.getCode() == coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("revieweeName", coreUserProfileDTO.getFullName());
      messageDTO.setObjectContent(jsonObject.toString());
      messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(managerUserId));
    }
  }

//********************************************************************************************************************

  @LogAround
  public void sendActivityExpireSoonMessageForSelf(
          long orgID,
          ReviewActivity reviewActivity,
          long revieweeID) throws Exception {
    // TODO: 当前产品需求设定为自评截止日前两天只发邮件, 不发消息
  }

  @LogAround
  public void sendInvitationBeginMessageForPeer(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID) throws Exception {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgID);
    messageDTO.setObjectId(invitationID);
    messageDTO.setSenders(Arrays.asList(revieweeID));
    messageDTO.setTemplateId(MessageTemplate.REVIEW_ONGOING.getCode());

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgID, templateID);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(reviewerID));
  }

  @LogAround
  public void sendActivityHasExpiredMessageForSelf(
          long orgID,
          ReviewActivity reviewActivity,
          long revieweeID) throws Exception {
    // TODO: 当前产品需求设定为互评截止日前两天, 若自评未提交, 则对自评人员不发消息
  }

  @LogAround
  public void sendInvitationExpireSoonMessageForPeer(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID) throws Exception {
    // TODO: 当前产品需求设定为互评截止日前两天只发邮件, 不发消息
  }

  @LogAround
  public void sendActivityCancelMessageForSelf(
          long orgID,
          long templateID,
          long revieweeID) throws Exception {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgID);
    messageDTO.setObjectId(templateID);
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.REVIEW_ACTIVITY_AUTO_CANCEL.getCode());

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgID, templateID);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(revieweeID));
  }

  @LogAround
  public void sendInvitationBeginMessageForManager(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID) throws Exception {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgID);
    messageDTO.setObjectId(invitationID);
    messageDTO.setSenders(Arrays.asList(revieweeID));
    messageDTO.setTemplateId(MessageTemplate.REVIEW_MANAGER_INVITE.getCode());

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgID, templateID);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(reviewerID));
  }

  @LogAround
  public void sendInvitationExpireSoonMessageForManager(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID) throws Exception {
    // TODO: 当前产品需求设定为公示日截止日前两天只发邮件, 不发消息
  }

  @LogAround
  public void sendInvitationHasExpiredMessageForPeer(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID) throws Exception {
    // TODO: 当前产品需求设定为公示日截止日前两天, 对互评人员不发消息
  }

  @LogAround
  public void sendInvitationCancelMessageForPeer(
          long orgID,
          long templateID,
          long revieweeID,
          long reviewerID) throws Exception {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgID);
    messageDTO.setObjectId(templateID);
    messageDTO.setSenders(Arrays.asList(reviewerID));
    messageDTO.setTemplateId(MessageTemplate.REVIEW_INVITATION_AUTO_CANCEL.getCode());

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgID, templateID);

    CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(orgID, revieweeID, -1, -1);
    if (ServiceStatus.COMMON_OK.getCode() == coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      JSONObject jsonObject = (JSONObject) JSONObject.toJSON(reviewTemplate);
      jsonObject.put("revieweeName", coreUserProfileDTO.getFullName());
      messageDTO.setObjectContent(jsonObject.toString());
      messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(reviewerID));
    }
  }

  @LogAround
  public void sendActivityFinishedMessageForSelf(
          long orgID,
          long templateID,
          long activityID,
          long revieweeID) throws Exception {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgID);
    messageDTO.setObjectId(activityID);
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.REVIEW_FINISH.getCode());

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgID, templateID);
    messageDTO.setObjectContent(JSONObject.toJSONString(reviewTemplate));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(revieweeID));
  }
}
