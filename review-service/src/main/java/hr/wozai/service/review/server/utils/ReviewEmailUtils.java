// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.utils;

import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;

import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.util.*;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.model.ReviewQuestion;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewProjectService;
import hr.wozai.service.review.server.service.ReviewQuestionService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.utils.EmailParamCons;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.user.client.userorg.facade.OrgFacade;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-15
 */
@Component
public class ReviewEmailUtils {

  private static final int maxNameCount = 3;

  @Value("${url}")
  private String URL;

  @Autowired
  EmailTemplateHelper emailTemplateHelper;

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewQuestionService reviewQuestionService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewCommentService reviewCommentService;

/*  @Autowired
  private SqsProducer sqsProducer;*/

  @Autowired
  private RabbitMQProducer rabbitMQProducer;

  @Autowired
  @Qualifier("userProfileFacadeProxy")
  private ThriftClientProxy userProfileFacadeProxy;

  private UserProfileFacade userProfileFacade;

  @Autowired
  @Qualifier("orgFacadeProxy")
  private ThriftClientProxy orgFacadeProxy;

  private OrgFacade orgFacade;

  @PostConstruct
  public void init() throws Exception {
    userProfileFacade = (UserProfileFacade)userProfileFacadeProxy.getObject();
    orgFacade = (OrgFacade)orgFacadeProxy.getObject();
  }

  @LogAround
  private String getActivityContent(long orgId, long templateId, long revieweeId) {

    String content = "";

    List<ReviewProject> reviewProjects =
        reviewProjectService.listReviewProject(orgId, templateId, revieweeId);

    long questionAmount = reviewCommentService.countReviewQuestionByReviewer(orgId, templateId, revieweeId, revieweeId);

    if(reviewProjects.size() == 0 && questionAmount == 0) {
      return null;
    }


    for (ReviewProject reviewProject : reviewProjects) {

      String name = reviewProject.getName();
      String role = reviewProject.getRole();
      Integer score = reviewProject.getScore();

      String key = "Project: " + name + "<br/>" + "role: " + role + "  " + "score: " + score.toString();
      String comment = reviewProject.getComment();

      content = content + key + "<br/>" + comment + "<br/><br/>";
    }

    List<ReviewComment> reviewComments = reviewCommentService.listReviewAllCommentByReviewer(orgId,
        templateId, revieweeId, revieweeId);

    for(ReviewComment reviewComment: reviewComments) {

      int itemType = reviewComment.getItemType();
      if(itemType != ReviewItemType.QUESTION.getCode()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }
      long itemId = reviewComment.getItemId();

      ReviewQuestion reviewQuestion = reviewQuestionService.findReviewQuestion(orgId, itemId);
      String name = reviewQuestion.getName();
      String comment = reviewComment.getContent();

      content = content + "Question: " + name + "<br/>" + comment + "<br/><br/>";
    }

    return content;
  }


  @LogAround
  private String getInvitationContent(long orgId, long templateId, long revieweeId, long reviewerId) {

    String content = "";

    List<ReviewComment> reviewComments = reviewCommentService.listReviewAllCommentByReviewer(orgId,
        templateId, revieweeId, reviewerId);

    for(ReviewComment reviewComment: reviewComments) {

      int itemType = reviewComment.getItemType();
      long itemId = reviewComment.getItemId();

      String name;
      if(ReviewItemType.QUESTION.getCode() == itemType) {
        ReviewQuestion reviewQuestion = reviewQuestionService.findReviewQuestion(orgId, itemId);
        name = reviewQuestion.getName();
      }
      else {
        ReviewProject reviewProject = reviewProjectService.findReviewProject(orgId, itemId);
        name = reviewProject.getName();
      }

      String comment = reviewComment.getContent();

      content = content + name + "<br/>" + comment + "<br/><br/>";
    }

    return content;
  }


  @LogAround
  public void sendTemplateBeginEmail(long orgId, long templateId,
                                     long activityId, long revieweeId,
                                     long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    String activityIdString;
    try {
      activityIdString = EncryptUtils.symmetricEncrypt(Long.toString(activityId));
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    CoreUserProfileDTO revieweeProfile = getCoreUserProfileVO(orgId, revieweeId,
        actorUserId, adminUserId);

    String url = "/review/item/" + activityIdString + "/activities";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    Long selfReviewDeadline = reviewTemplate.getSelfReviewDeadline();

    String emailJson = EmailTemplate.getReviewBeginEmailContent(
            EmailTemplate.REVIEW_BEGIN,
            revieweeProfile.getFullName(), reviewTemplate.getTemplateName(),
            TimeUtils.formatDateWithTimeZone(selfReviewDeadline, TimeUtils.BEIJING),
            url, orgName, revieweeProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }


  @LogAround
  public void sendInvitationEmail(long orgId, long templateId,
                                  long invitationId, long revieweeId, long reviewerId,
                                  long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    String invitationIdString;
    try {
      invitationIdString = EncryptUtils.symmetricEncrypt(Long.toString(invitationId));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    String url = "/review/item/" + invitationIdString + "/invitations";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = getCoreUserProfileVO(orgId, revieweeId,
            actorUserId, adminUserId);

    CoreUserProfileDTO reviewerProfile = getCoreUserProfileVO(orgId, reviewerId,
        actorUserId, adminUserId);

    String deadline = TimeUtils.formatDateWithTimeZone(reviewTemplate.getPeerReviewDeadline(), TimeUtils.BEIJING);

    String emailJson = EmailTemplate.getReviewOngoingEmailContent(EmailTemplate.REVIEW_ONGOING,
            reviewerProfile.getFullName(), revieweeProfile.getFullName(), deadline, url, orgName, reviewerProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }

  @LogAround
  public void sendActivityEmail(long orgId, long templateId, long activityId, long revieweeId,
                              long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    String activityString;
    try {
      activityString = EncryptUtils.symmetricEncrypt(Long.toString(activityId));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    String url = "/review/item/" + activityString + "/activities";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = getCoreUserProfileVO(orgId, revieweeId,
            actorUserId, adminUserId);

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    String period = dateFormat.format(new Date(reviewTemplate.getCreatedTime()));
    String dealine = dateFormat.format(new Date(reviewTemplate.getSelfReviewDeadline()));
    String emailJson = EmailTemplate.getReviewBeginEmailContent(EmailTemplate.REVIEW_BEGIN,
            revieweeProfile.getFullName(), period, dealine, url, orgName, revieweeProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }

  @LogAround
  public void sendPublicEmail(long orgId, long templateId, long activityId, long revieweeId,
                              long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    String activityString;
    try {
      activityString = EncryptUtils.symmetricEncrypt(Long.toString(activityId));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    String url = "/review/item/" + activityString + "/activities";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = getCoreUserProfileVO(orgId, revieweeId,
        actorUserId, adminUserId);

    String emailJson = EmailTemplate.getReviewFinishEmailContent(EmailTemplate.REVIEW_FINISH,
            revieweeProfile.getFullName(), reviewTemplate.getTemplateName(), url, orgName, revieweeProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }


  // Cancel by HR
  @LogAround
  public void sendSubmittedCancelEmail(long orgId, long templateId, long userId,
                                       long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);


    CoreUserProfileDTO userProfile = getCoreUserProfileVO(orgId, userId,
        actorUserId, adminUserId);

    String emailJson = EmailTemplate.getReviewCancelEmailContent(EmailTemplate.REVIEW_CANCEL,
            userProfile.getFullName(), reviewTemplate.getTemplateName(), orgName, userProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }


  // Cancel by HR or public deadline
  @LogAround
  public void sendUnSubmittedActivityCancelEmail(long orgId, long templateId, long revieweeId,
                                                 long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    String content = getActivityContent(orgId, templateId, revieweeId);

    if(null == content || content.isEmpty())
      return;

    CoreUserProfileDTO revieweeProfile = getCoreUserProfileVO(orgId, revieweeId,
            actorUserId, adminUserId);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    String emailJson = EmailTemplate.getReviewCancelAndBackupEmailContent(
            EmailTemplate.REVIEW_CANCEL_BACKUP,
            revieweeProfile.getFullName(), reviewTemplate.getTemplateName(), content, orgName,
            revieweeProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }

  // Cancel by HR or public deadline
  @LogAround
  public void sendUnSubmittedInvitationCancelEmail(long orgId, long templateId,
                                                   long revieweeId, long reviewerId,
                                                   long actorUserId, long adminUserId) throws Exception {
    String orgName = getOrgName(orgId, actorUserId, adminUserId);

    String content = getInvitationContent(orgId, templateId, revieweeId, reviewerId);

    if(null == content || content.isEmpty())
      return;

    CoreUserProfileDTO reviewerProfile = getCoreUserProfileVO(orgId, reviewerId,
        actorUserId, adminUserId);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    String emailJson = EmailTemplate.getReviewCancelAndBackupEmailContent(
            EmailTemplate.REVIEW_CANCEL_BACKUP,
            reviewerProfile.getFullName(), reviewTemplate.getTemplateName(), content, orgName,
            reviewerProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(emailJson);
  }

  @LogAround
  private CoreUserProfileDTO getCoreUserProfileVO(long orgId, long userId,
                                                  long actorUserId, long adminUserId) {

    CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(orgId, userId,
        actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(coreUserProfileDTO.getServiceStatusDTO().getCode());
    }
    return coreUserProfileDTO;
  }

  @LogAround
  private String getOrgName(long orgId, long actorUserId, long adminUserId) {
    OrgDTO orgDTO = orgFacade.getOrg(orgId, actorUserId, adminUserId);
    if(orgDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    return orgDTO.getShortName();
  }

  public void sendReviewPeerBatchInviteEmail(
          long orgId, List<Long> peerRevieweeUserIds, long reviewerUserId, int peerCount,
          long deadline, long invitationId, long actorUserId, long adminUserId) {

    OrgDTO orgDTO = orgFacade.getOrg(orgId, actorUserId, adminUserId);
    peerRevieweeUserIds.add(reviewerUserId);
    CoreUserProfileListDTO coreUserProfileListDTO =
        userProfileFacade.listCoreUserProfile(orgId, peerRevieweeUserIds, actorUserId, adminUserId);
    List<CoreUserProfileDTO> coreUserProfileDTOs = coreUserProfileListDTO.getCoreUserProfileDTOs();
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      CoreUserProfileDTO reviewerCUP = null;
      for (int i = 0; i < coreUserProfileDTOs.size(); i++) {
        if (reviewerUserId == coreUserProfileDTOs.get(i).getUserId()) {
          reviewerCUP = coreUserProfileDTOs.remove(i);
          break;
        }
      }
      if (!CollectionUtils.isEmpty(coreUserProfileDTOs)
          && null != reviewerCUP) {
        String peerRevieweeNames = coreUserProfileDTOs.get(0).getFullName();
        for (int i = 1; i < coreUserProfileDTOs.size() & i < maxNameCount; i++) {
          peerRevieweeNames += "、" + coreUserProfileDTOs.get(i).getFullName();
        }
        Map<String, String> emailParams = new HashMap<>();
        emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, reviewerCUP.getEmailAddress());
        emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgDTO.getShortName());
        emailParams.put(EmailParamCons.PARAM_REVIEWER, reviewerCUP.getFullName());
        emailParams.put(EmailParamCons.PARAM_PEER_NAMES, peerRevieweeNames);
        emailParams.put(EmailParamCons.PARAM_PEER_COUNT, String.valueOf(peerCount));
        emailParams.put(EmailParamCons.PARAM_DEAD_LINE, TimeUtils.getDateStringOfBeijingTimezone(deadline));
        if (setReviewTemplateUrlInEmailParams(invitationId, emailParams)) {
          return;
        }
        rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.REVIEW_PEER_BATCH_INVITE, emailParams);
      }
    }

  }

  public void sendReviewManagerBatchInviteEmail(
      long orgId, List<Long> subRevieweeUserIds, long managerUserId, int subCount,
      long deadline, long invitationId, long actorUserId, long adminUserId) {

    OrgDTO orgDTO = orgFacade.getOrg(orgId, actorUserId, adminUserId);
    subRevieweeUserIds.add(managerUserId);
    CoreUserProfileListDTO coreUserProfileListDTO =
        userProfileFacade.listCoreUserProfile(orgId, subRevieweeUserIds, actorUserId, adminUserId);
    List<CoreUserProfileDTO> coreUserProfileDTOs = coreUserProfileListDTO.getCoreUserProfileDTOs();
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      CoreUserProfileDTO managerCUP = null;
      for (int i = 0; i < coreUserProfileDTOs.size(); i++) {
        if (managerUserId == coreUserProfileDTOs.get(i).getUserId()) {
          managerCUP = coreUserProfileDTOs.remove(i);
          break;
        }
      }
      if (!CollectionUtils.isEmpty(coreUserProfileDTOs)
          && null != managerCUP) {
        String subRevieweeNames = coreUserProfileDTOs.get(0).getFullName();
        for (int i = 1; i < coreUserProfileDTOs.size() & i < maxNameCount; i++) {
          subRevieweeNames += "、" + coreUserProfileDTOs.get(i).getFullName();
        }
        Map<String, String> emailParams = new HashMap<>();
        emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, managerCUP.getEmailAddress());
        emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgDTO.getShortName());
        emailParams.put(EmailParamCons.PARAM_REVIEWER, managerCUP.getFullName());
        emailParams.put(EmailParamCons.PARAM_SUB_NAMES, subRevieweeNames);
        emailParams.put(EmailParamCons.PARAM_SUB_COUNT, String.valueOf(subCount));
        emailParams.put(EmailParamCons.PARAM_DEAD_LINE, TimeUtils.getDateStringOfBeijingTimezone(deadline));

        if (setReviewTemplateUrlInEmailParams(invitationId, emailParams)) {
          return;
        }

        rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.REVIEW_MANAGER_INVITE, emailParams);
      }
    }
  }

  private boolean setReviewTemplateUrlInEmailParams(long templateId, Map<String, String> emailParams) {
    String invitationIdString;
    try {
      invitationIdString = EncryptUtils.symmetricEncrypt(Long.toString(templateId));
    } catch (Exception e) {
      e.printStackTrace();
      return true;
    }

    String url = "/review/index/" + invitationIdString;
    try {
      String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
      url = URL + url + "?urlCode=" + encodedURL;
    } catch (Exception e) {
      url = URL + url;
    }

    emailParams.put(EmailParamCons.PARAM_URL, url);
    return false;
  }

  public void sendPeerReviewNotifyManagerEmail(
          long orgId, long reviewerUserId, long revieweeUserId, long deadline,
          long managerUserId, long templateId, long actorUserId, long adminUserId) {

    OrgDTO orgDTO = orgFacade.getOrg(orgId, actorUserId, adminUserId);
    List<Long> userIds = new ArrayList<>();
    userIds.add(reviewerUserId);
    userIds.add(revieweeUserId);
    userIds.add(managerUserId);
    CoreUserProfileListDTO coreUserProfileListDTO =
        userProfileFacade.listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
    CoreUserProfileDTO reviewerCUP = null;
    CoreUserProfileDTO revieweeCUP = null;
    CoreUserProfileDTO managerCUP = null;
    if (null != coreUserProfileListDTO.getCoreUserProfileDTOs()) {
      for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileListDTO.getCoreUserProfileDTOs()) {
        if (reviewerUserId == coreUserProfileDTO.getUserId()) {
          reviewerCUP = coreUserProfileDTO;
        } else if (revieweeUserId == coreUserProfileDTO.getUserId()) {
          revieweeCUP = coreUserProfileDTO;
        } else {
          managerCUP = coreUserProfileDTO;
        }
      }
    }

    if (null != reviewerCUP
        && null != revieweeCUP
        && null != managerCUP) {

      Map<String, String> emailParams = new HashMap<>();
      emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, managerCUP.getEmailAddress());
      emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgDTO.getShortName());
      emailParams.put(EmailParamCons.PARAM_DEAD_LINE, TimeUtils.getDateStringOfBeijingTimezone(deadline));
      emailParams.put(EmailParamCons.PARAM_REVIEWER, reviewerCUP.getFullName());
      emailParams.put(EmailParamCons.PARAM_REVIEWEE, revieweeCUP.getFullName());
      emailParams.put(EmailParamCons.PARAM_MANAGER_NAME, managerCUP.getFullName());

      if (setReviewTemplateUrlInEmailParams(templateId, emailParams)) {
        return;
      }

      rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.REVIEW_PEER_NOTIFY_MANAGER, emailParams);
    }
  }

//********************************************************************************************************************

  // Add by Zich Liu

  @LogAround
  private void addToMapEmailTemplateIDToBatchEmails(
          EmailTemplate emailTemplate,
          List<String> dynamicParamSeq,
          List<String> dynamicParam,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    Integer emailTemplateID = emailTemplate.getTemplateId();
    if (mapEmailTemplateIDToBatchEmails.containsKey(emailTemplateID)) {
      mapEmailTemplateIDToBatchEmails.get(emailTemplateID).getDynamicParams().add(dynamicParam);
    } else {
      BatchEmail batchEmail = new BatchEmail();
      batchEmail.setFixedParamsMap(new HashMap<>());
      batchEmail.setDynamicParamSeq(dynamicParamSeq);
      batchEmail.setDynamicParams(new ArrayList<>(Arrays.asList(dynamicParam)));
      batchEmail.setEmailTemplate(emailTemplate);

      mapEmailTemplateIDToBatchEmails.put(emailTemplateID, batchEmail);
    }
  }

  @LogAround
  public void sendActivityExpireSoonEmailForSelf(
          long orgID,
          long templateID,
          long activityID,
          long revieweeID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String activityString = EncryptUtils.symmetricEncrypt(Long.toString(activityID));
    ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

    String url = "/review/item/" + activityString + "/activities";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = this.getCoreUserProfileVO(
            orgID, revieweeID, actorUserID, adminUserID
    );
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String deadline = dateFormat.format(new Date(reviewTemplate.getSelfReviewDeadline()));

    this.addToMapEmailTemplateIDToBatchEmails(
            EmailTemplate.REVIEW_SELF_REMINDER,
            new ArrayList<>(
                    Arrays.asList(
                            "reviewActivityName",
                            "url",
                            "reviewee",
                            "deadline",
                            "orgShortName",
                            "dstEmailAddress"
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            reviewTemplate.getTemplateName(),
                            url,
                            revieweeProfile.getFullName(),
                            deadline,
                            orgName,
                            revieweeProfile.getEmailAddress()
                    )
            ),
            mapEmailTemplateIDToBatchEmails
    );
  }

  @LogAround
  public void sendInvitationBeginEmailForPeer(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String invitationIdString = EncryptUtils.symmetricEncrypt(Long.toString(invitationID));
    ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

    String url = "/review/item/" + invitationIdString + "/invitations";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = this.getCoreUserProfileVO(
            orgID, revieweeID, actorUserID, adminUserID
    );
    CoreUserProfileDTO reviewerProfile = this.getCoreUserProfileVO(
            orgID, reviewerID, actorUserID, adminUserID
    );
    String deadline = TimeUtils.formatDateWithTimeZone(
            reviewTemplate.getPeerReviewDeadline(), TimeUtils.BEIJING
    );

    this.addToMapEmailTemplateIDToBatchEmails(
            EmailTemplate.REVIEW_ONGOING,
            new ArrayList<>(
                    Arrays.asList(
                            "reviewer",
                            "reviewee",
                            "url",
                            "deadline",
                            "orgShortName",
                            "dstEmailAddress"
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(

                            reviewerProfile.getFullName(),
                            revieweeProfile.getFullName(),
                            url,
                            deadline,
                            orgName,
                            reviewerProfile.getEmailAddress()
                    )
            ),
            mapEmailTemplateIDToBatchEmails
    );
  }

  @LogAround
  public void sendActivityHasExpiredEmailForSelf(
          long orgID,
          long templateID,
          long activityID,
          long revieweeID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // TODO: 对互评截止日前两天, 自评未提交, 给出通知(产品目前设定为不通知)
  }

  @LogAround
  public void sendInvitationExpireSoonEmailForPeer(
          long orgID,
          long templateID,
          long invitationID,
          long reviewerID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String invitationIdString = EncryptUtils.symmetricEncrypt(Long.toString(invitationID));
    ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

    String url = "/review/item/" + invitationIdString + "/invitations";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO reviewerProfile = this.getCoreUserProfileVO(
            orgID, reviewerID, actorUserID, adminUserID
    );
    String deadline = TimeUtils.formatDateWithTimeZone(
            reviewTemplate.getPeerReviewDeadline(), TimeUtils.BEIJING
    );

    this.addToMapEmailTemplateIDToBatchEmails(
            EmailTemplate.REVIEW_ONGOING_REMINDER,
            new ArrayList<>(
                    Arrays.asList(
                            "reviewActivityName",
                            "url",
                            "reviewer",
                            "deadline",
                            "orgShortName",
                            "dstEmailAddress"
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            reviewTemplate.getTemplateName(),
                            url,
                            reviewerProfile.getFullName(),
                            deadline,
                            orgName,
                            reviewerProfile.getEmailAddress()
                    )
            ),
            mapEmailTemplateIDToBatchEmails
    );
  }

  @LogAround
  public void sendActivityCancelEmailForSelf(
          long orgID,
          long templateID,
          long revieweeID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String content = this.getActivityContent(orgID, templateID, revieweeID);
    if(content != null && !content.isEmpty()) {
      CoreUserProfileDTO revieweeProfile = this.getCoreUserProfileVO(
              orgID, revieweeID, actorUserID, adminUserID
      );
      ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

      this.addToMapEmailTemplateIDToBatchEmails(
              EmailTemplate.REVIEW_ACTIVITY_AUTO_CANCEL,
              new ArrayList<>(
                      Arrays.asList(
                              "reviewActivityName",
                              "reviewee",
                              "orgShortName",
                              "dstEmailAddress"
                      )
              ),
              new ArrayList<>(
                      Arrays.asList(
                              reviewTemplate.getTemplateName(),
                              revieweeProfile.getFullName(),
                              orgName,
                              revieweeProfile.getEmailAddress()
                      )
              ),
              mapEmailTemplateIDToBatchEmails
      );
    }
  }

  @LogAround
  public void sendInvitationBeginEmailForManager(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String invitationIdString = EncryptUtils.symmetricEncrypt(Long.toString(invitationID));
    ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

    String url = "/review/item/" + invitationIdString + "/invitations";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = this.getCoreUserProfileVO(
            orgID, revieweeID, actorUserID, adminUserID
    );
    CoreUserProfileDTO reviewerProfile = this.getCoreUserProfileVO(
            orgID, reviewerID, actorUserID, adminUserID
    );
    String deadline = TimeUtils.formatDateWithTimeZone(
            reviewTemplate.getPublicDeadline(), TimeUtils.BEIJING
    );

    this.addToMapEmailTemplateIDToBatchEmails(
            EmailTemplate.REVIEW_MANAGER_INVITE,
            new ArrayList<>(
                    Arrays.asList(
                            "url",
                            "reviewee",
                            "reviewer",
                            "deadline",
                            "orgShortName",
                            "dstEmailAddress"
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            url,
                            revieweeProfile.getFullName(),
                            reviewerProfile.getFullName(),
                            deadline,
                            orgName,
                            reviewerProfile.getEmailAddress()
                    )
            ),
            mapEmailTemplateIDToBatchEmails
    );
  }

  @LogAround
  public void sendInvitationExpireSoonEmailForManager(
          long orgID,
          long templateID,
          long invitationID,
          long reviewerID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String invitationIdString = EncryptUtils.symmetricEncrypt(Long.toString(invitationID));
    ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

    String url = "/review/item/" + invitationIdString + "/invitations";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO reviewerProfile = this.getCoreUserProfileVO(
            orgID, reviewerID, actorUserID, adminUserID
    );
    String deadline = TimeUtils.formatDateWithTimeZone(
            reviewTemplate.getPublicDeadline(), TimeUtils.BEIJING
    );

    this.addToMapEmailTemplateIDToBatchEmails(
            EmailTemplate.REVIEW_DEADLINE_REMINDER,
            new ArrayList<>(
                    Arrays.asList(
                            "reviewActivityName",
                            "url",
                            "reviewer",
                            "deadline",
                            "orgShortName",
                            "dstEmailAddress"
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            reviewTemplate.getTemplateName(),
                            url,
                            reviewerProfile.getFullName(),
                            deadline,
                            orgName,
                            reviewerProfile.getEmailAddress()
                    )
            ),
            mapEmailTemplateIDToBatchEmails
    );
  }

  @LogAround
  public void sendInvitationHasExpiredEmailForPeer(
          long orgID,
          long templateID,
          long invitationID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // TODO: 对最终截止日前两天, 互评未提交, 给出通知(产品目前设定为不通知)
  }

  @LogAround
  public void sendInvitationCancelEmailForPeer(
          long orgID,
          long templateID,
          long revieweeID,
          long reviewerID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String content = this.getInvitationContent(orgID, templateID, revieweeID, reviewerID);
    if(content != null && !content.isEmpty()) {
      CoreUserProfileDTO reviewerProfile = this.getCoreUserProfileVO(
              orgID, reviewerID, actorUserID, adminUserID
      );
      CoreUserProfileDTO revieweeProfile = this.getCoreUserProfileVO(
              orgID, revieweeID, actorUserID, adminUserID
      );
      ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

      this.addToMapEmailTemplateIDToBatchEmails(
              EmailTemplate.REVIEW_INVITATION_AUTO_CANCEL,
              new ArrayList<>(
                      Arrays.asList(
                              "reviewActivityName",
                              "users",
                              "reviewer",
                              "orgShortName",
                              "dstEmailAddress"
                      )
              ),
              new ArrayList<>(
                      Arrays.asList(
                              reviewTemplate.getTemplateName(),
                              revieweeProfile.getFullName(),
                              reviewerProfile.getFullName(),
                              orgName,
                              reviewerProfile.getEmailAddress()
                      )
              ),
              mapEmailTemplateIDToBatchEmails
      );
    }
  }

  @LogAround
  public void sendActivityFinishedEmailForSelf(
          long orgID,
          long templateID,
          long activityID,
          long revieweeID,
          long actorUserID,
          long adminUserID,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    String orgName = this.getOrgName(orgID, actorUserID, adminUserID);
    String activityString = EncryptUtils.symmetricEncrypt(Long.toString(activityID));
    ReviewTemplate reviewTemplate = this.reviewTemplateService.findReviewTemplate(orgID, templateID);

    String url = "/review/item/" + activityString + "/activities";
    String encodedURL = URLEncoder.encode("/#" + url, "UTF-8");
    url = URL + url + "?urlCode=" + encodedURL;

    CoreUserProfileDTO revieweeProfile = this.getCoreUserProfileVO(
            orgID, revieweeID, actorUserID, adminUserID
    );

    this.addToMapEmailTemplateIDToBatchEmails(
            EmailTemplate.REVIEW_FINISH,
            new ArrayList<>(
                    Arrays.asList(
                            "reviewee",
                            "period",
                            "url",
                            "orgShortName",
                            "dstEmailAddress"
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            revieweeProfile.getFullName(),
                            reviewTemplate.getTemplateName(),
                            url,
                            orgName,
                            revieweeProfile.getEmailAddress()
                    )
            ),
            mapEmailTemplateIDToBatchEmails
    );
  }

  @LogAround
  public void sendBatchEmails(HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails) {
    if (!CollectionUtils.isEmpty(mapEmailTemplateIDToBatchEmails)) {
      int batchNumberEachTime = 100;

      for (BatchEmail batchEmail : mapEmailTemplateIDToBatchEmails.values()) {
        List<List<String>> totalDynamicParams = batchEmail.getDynamicParams();
        if (CollectionUtils.isEmpty(totalDynamicParams)) {
          continue;
        }

        for (int i = 0; i < totalDynamicParams.size(); i += batchNumberEachTime) {
          int end = i + batchNumberEachTime;
          if (end > totalDynamicParams.size()) {
            end = totalDynamicParams.size();
          }

          batchEmail.setDynamicParams(totalDynamicParams.subList(i, end));
          this.emailTemplateHelper.preBatchSendEmail(batchEmail);
        }
      }
    }
  }
}
