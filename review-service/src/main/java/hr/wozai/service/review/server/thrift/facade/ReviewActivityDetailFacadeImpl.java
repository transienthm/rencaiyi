// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.facade.ReviewActivityDetailFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.model.*;
import hr.wozai.service.review.server.service.*;
import hr.wozai.service.review.server.utils.ReviewEmailUtils;
import hr.wozai.service.review.server.utils.ReviewMessageUtils;
import hr.wozai.service.review.server.utils.ReviewUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@Service("reviewActivityDetailFacadeImpl")
public class ReviewActivityDetailFacadeImpl implements ReviewActivityDetailFacade {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityDetailFacadeImpl.class);

  private static int INVITATION_FINISH = 1;
  private static int INVITATION_IN_PROCESS = 2;
  private static int INVITATION_NOT_BEGIN = 3;

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewActivityService reviewActivityService;

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewQuestionService reviewQuestionService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  @Autowired
  private ReviewUtils reviewUtils;

  @Autowired
  private ReviewEmailUtils reviewEmailUtils;

  @Autowired
  private ReviewMessageUtils reviewMessageUtils;

  /**
   * Get review activity detail
   * <p>
   * 前提: Template is IN_PROGRESS
   * 自评人: 在互评截止日之前, 是可以提交的 (submittable)
   * <p>
   * 1. 如果还没有提交:
   * project 可以修改, 可以删除,
   * question 下的 comment 可以修改, 因为是必添项所以没有删除接口
   * <p>
   * 2. 如果提交了
   * (1) 如果在自评截止日之前, project 可以修改, 但不能删除(与1的唯一区别),
   * question 下的 comment 可以修改, 因为是必添项所以没有删除接口
   * (2) 如果过了自评截止日, 不能做任何修改
   *
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewActivityDetailDTO getReviewActivityDetailDTO(
      long orgId, long activityId, long actorUserId, long adminUserId) {

    ReviewActivityDetailDTO result = new ReviewActivityDetailDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
      BeanUtils.copyProperties(reviewTemplate, reviewTemplateDTO);

      // # list project
      List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, actorUserId);
      List<ReviewProjectDTO> reviewProjectDTOs = getReviewProjectDTOs(reviewProjects);

      // # list question comments based on template status
      List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
      List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = getReviewQuestionDetailDTOs(orgId, templateId, actorUserId,
              reviewQuestions, reviewTemplate, reviewActivity);

      // # fill detail VO
      result.setActivityId(activityId);
      result.setOrgId(orgId);
      result.setReviewTemplateDTO(reviewTemplateDTO);
      result.setRevieweeId(actorUserId);

      result.setReviewProjectDTOs(reviewProjectDTOs);
      result.setReviewQuestionDetailDTOs(reviewQuestionDetailDTOs);

      int status = reviewTemplate.getState();
      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;

      // # fill detail project addable
      // # 只能在 Template 的状态是 IN_PROGRESS 自评人才有修改权限
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() == status) {
        if (!isSubmitted && !isPeerReviewDeadline) {
          result.setIsProjectAddable(1);
          result.setIsSubmittable(1);
        } else if (isSubmitted && !isSelfReviewDeadline) {
          result.setIsProjectAddable(1);
        }
      }

      // # fill detail score
      // # 当 Template 的状态是 FINISH 时, 显示主管的打分
      if (ReviewTemplateStatus.FINISH.getCode() == status
          || ReviewTemplateStatus.IN_PROGRESS.getCode() == status) {
        long revieweeId = actorUserId;
        ReviewInvitation managerInvitation = reviewInvitationService.findManagerInvitation(orgId, templateId, revieweeId);
        result.setScore(managerInvitation.getScore());
      }

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  @LogAround
  private List<ReviewProjectDTO> getReviewProjectDTOs(List<ReviewProject> reviewProjects) throws Exception {
    List<ReviewProjectDTO> reviewProjectDTOs = new ArrayList<>();

    for (ReviewProject reviewProject : reviewProjects) {
      ReviewProjectDTO reviewProjectDTO = new ReviewProjectDTO();
      BeanUtils.copyProperties(reviewProject, reviewProjectDTO);
      reviewProjectDTOs.add(reviewProjectDTO);
    }
    return reviewProjectDTOs;
  }


  @LogAround
  private List<ReviewQuestionDetailDTO> getReviewQuestionDetailDTOs(long orgId, long templateId, long revieweeId,
                                                                    List<ReviewQuestion> reviewQuestions,
                                                                    ReviewTemplate reviewTemplate,
                                                                    ReviewActivity reviewActivity) throws Exception {

    int status = reviewTemplate.getState();
    boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
    boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
    boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = new ArrayList<>();
    for (ReviewQuestion reviewQuestion : reviewQuestions) {

      ReviewQuestionDetailDTO reviewQuestionDetailDTO = new ReviewQuestionDetailDTO();

      long questionId = reviewQuestion.getQuestionId();
      BeanUtils.copyProperties(reviewQuestion, reviewQuestionDetailDTO);

      // # 获取自评人的自评: 理论情况 <= 1, 当小于1时即还没有回答此问题
      List<ReviewCommentDTO> revieweeReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
              ReviewItemType.QUESTION.getCode(), questionId, revieweeId, Arrays.asList(revieweeId));
      // TODO: cannot more than 1, as insert check
      if (revieweeReviewCommentDTOs.size() > 1) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      } else if (revieweeReviewCommentDTOs.size() == 1) {
        reviewQuestionDetailDTO.setRevieweeComment(revieweeReviewCommentDTOs.get(0));
      }

      if (ReviewTemplateStatus.IN_PROGRESS.getCode() == status) {
        if (!isSubmitted && !isPeerReviewDeadline) {
          reviewQuestionDetailDTO.setIsEditable(1);
        } else if (isSubmitted && !isSelfReviewDeadline) {
          reviewQuestionDetailDTO.setIsEditable(1);
        }
      }

      // # 如果评价活动已经结束了, 自己人可以看到所有已提交邀请的评价信息
      List<Long> reviewerIds = new ArrayList<>();
      if (ReviewTemplateStatus.FINISH.getCode() == status
          || ReviewTemplateStatus.IN_PROGRESS.getCode() == status) {
        reviewerIds = reviewUtils.getSubmittedReviewerIds(orgId, templateId, revieweeId);
        List<ReviewCommentDTO> submittedReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
                ReviewItemType.QUESTION.getCode(), questionId, revieweeId, reviewerIds);
        reviewQuestionDetailDTO.setSubmittedComment(submittedReviewCommentDTOs);
      }

      reviewQuestionDetailDTOs.add(reviewQuestionDetailDTO);
    }
    return reviewQuestionDetailDTOs;
  }


  /**
   * Get review activity invitation
   * 获取自评人此次评价活动邀请了谁
   * <p>
   * 当自评人提交后, 在自评截止日之前可以继续邀请
   * <p>
   * (1) 如果还没有提交, 只返回主管
   * (2) 如果已经提交, 查询没有拒绝的邀请者
   *
   * @param orgId
   * @param activityId
   * @param managerUserId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewInvitedUserListDTO getReviewActivityInvitation(long orgId, long activityId,
                                                              long managerUserId,
                                                              long actorUserId, long adminUserId) {

    ReviewInvitedUserListDTO result = new ReviewInvitedUserListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    ReviewActivity reviewActivity = null;
    try {
      reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

      /*if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }*/

      boolean isActivitySubmitted = reviewActivity.getIsSubmitted() == 1;

      result = reviewUtils.getReviewInvitedUsers(orgId, templateId, actorUserId, managerUserId);

      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      // (1) 当自评人未提交, 在 互评截止日 之前可以继续邀请
      // (2) 当自评人提交后, 在 自评截止日 之前可以继续邀请
      if (!isActivitySubmitted && !isPeerReviewDeadline) {
        result.setIsAddable(1);
      } else if (isActivitySubmitted && !isSelfReviewDeadline) {
        result.setIsAddable(1);
      }
    } catch (Exception e) {
      LOGGER.error("getReviewActivityInvitation():", e);
      if (reviewActivity == null || reviewActivity.getIsCanceled() == 1) {
        FacadeExceptionHelper.setServiceStatusForCanceledActivity(serviceStatusDTO);
      } else {
        FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      }
    }

    return result;
  }


  @LogAround
  public VoidDTO setReviewActivityInvitation(long orgId, long activityId,
                                             long managerUserId,
                                             List<Long> invitedUserIds,
                                             long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      if (null == invitedUserIds) {
        invitedUserIds = new ArrayList<>();
      }
      //invitedUserIds.add(managerUserId);

      // unique invited user ids
      invitedUserIds = removeDuplicate(invitedUserIds);

      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // 根据是否提交和当前时间, 判断更改邀请是否允许
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean status = false;
      if ((!isSelfReviewDeadline)
          || (!isSubmitted && !isPeerReviewDeadline)) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // 如果当前被邀请人还没有评价 (no comment), 自评人可以删除该邀请人
      // Remove removed invitations
      List<ReviewInvitation> reviewInvitations =
              reviewInvitationService.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, actorUserId);

      for (ReviewInvitation reviewInvitation : reviewInvitations) {

        long reviewerId = reviewInvitation.getReviewerId();

        // 过滤主管的邀请
        if (reviewerId == managerUserId) {
          continue;
        }
        // 过滤已有的邀请人
        if (invitedUserIds.contains(reviewerId)) {
          invitedUserIds.remove(reviewerId);
          continue;
        }

        // 当前被邀请人是有有效评价时, return BAD_REQUEST
        long amount = reviewCommentService.countReviewAllCommentByReviewer(orgId, templateId, actorUserId, reviewerId);
        if (amount > 0) {
          LOGGER.error("Invitation has comment, it cannot be removed.");
          throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
        }

        long invitationId = reviewInvitation.getInvitationId();

        // 不能删除主管的邀请
        if (reviewInvitation.getIsManager().intValue() == 0) {
          reviewInvitationService.deleteReviewInvitation(orgId, invitationId, actorUserId);
        }
      }

      // Add new invitations
      insertReviewInvitations(orgId, reviewTemplate, actorUserId, invitedUserIds, actorUserId, adminUserId);

    } catch (Exception e) {
      LOGGER.error("setReviewActivityInvitation-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  private List<Long> removeDuplicate(List<Long> invitedUserIds) {
    List<Long> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(invitedUserIds)) {
      for (Long userId : invitedUserIds) {
        if (!result.contains(userId)) {
          result.add(userId);
        }
      }
    }
    return result;
  }

  /**
   * Steps:
   *  1) check:
   *      a. temp is IN_PROGRESS
   *      b. current time is before peerReviewDeadline
   *  2) submit self review
   *  3) add peer invitations
   *  4) send email & msg if current time is after selfReviewDeadline
   *
   *
   * 提交评价活动时, 要检查的状态
   * (1) Template is IN_PROGRESS
   * (2) comment number = question number (因为插入comment时保证了每个question只有一个comment)
   * (3) 还没有提交
   * (4) currentTime < 互评截止日
   * 根据邀请人列表, 插入 invitation record
   *
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public VoidDTO submitReviewActivity(
      long orgId, long activityId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      long revieweeId = reviewActivity.getRevieweeId();
      if (actorUserId != revieweeId) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      long templateId = reviewActivity.getTemplateId();

      // 1)
      long currTs = TimeUtils.getNowTimestmapInMillis();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()
          || currTs >= reviewTemplate.getPeerReviewDeadline()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // 2)
      long savedReviewCommentAmount =
          reviewCommentService.countReviewQuestionByReviewer(orgId, templateId, actorUserId, actorUserId);
      long reviewQuestionAmount = reviewQuestionService.countReviewQuestionOfTemplate(orgId, templateId);
      if (savedReviewCommentAmount != reviewQuestionAmount) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      boolean status = false;
      if (!isSubmitted && !isPeerReviewDeadline) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      reviewActivity.setIsSubmitted(1);
      reviewActivity.setLastModifiedUserId(actorUserId);
      reviewActivityService.updateReviewActivity(reviewActivity);

//      // 3)
//      if (null == invitedUserIds) {
//        invitedUserIds = new ArrayList<>();
//      }
//      // unique invited user ids
//      invitedUserIds = removeDuplicate(invitedUserIds);
//      insertReviewInvitations(orgId, reviewTemplate, actorUserId, invitedUserIds, actorUserId, adminUserId);

      // 4)
      if (currTs > reviewTemplate.getSelfReviewDeadline()) {
        sendPeersInvitationEmailAndMessage(orgId, templateId, revieweeId, actorUserId, adminUserId);
      }

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  private void sendPeersInvitationEmailAndMessage(long organizationID, long templateID,
                                                  long revieweeID, long actorUserID, long adminUserID) throws Exception {
    List<ReviewInvitation> reviewInvitations =
            reviewInvitationService.listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(organizationID, templateID, revieweeID);
    for (ReviewInvitation reviewInvitation : reviewInvitations) {
      long invitationId = reviewInvitation.getInvitationId();
      long peerUserID = reviewInvitation.getReviewerId();
      reviewEmailUtils.sendInvitationEmail(organizationID, templateID, invitationId, revieweeID, peerUserID, actorUserID, adminUserID);
      reviewMessageUtils.sendInvitationMessage(organizationID, templateID, invitationId, revieweeID, peerUserID, actorUserID, adminUserID);
    }
  }

//  @LogAround
//  private void sendMangagerInvitationEmailAndMessage(long orgId, long templateId,
//                                                     long managerUserId, long revieweeId,
//                                                     long actorUserId, long adminUserId) {
//    ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitationByTemplate(orgId, templateId,
//            revieweeId, managerUserId);
//    long invitationId = reviewInvitation.getInvitationId();
//    try {
//      reviewEmailUtils.sendInvitationEmail(orgId, templateId, invitationId, revieweeId, managerUserId, actorUserId, adminUserId);
//
//      reviewMessageUtils.sendInvitationMessage(orgId, templateId, invitationId, revieweeId, managerUserId, actorUserId, adminUserId);
//    } catch (Exception e) {
//      LOGGER.error(e.toString());
//    }
//  }

  @LogAround
  private void insertReviewInvitations(long orgId, ReviewTemplate reviewTemplate,
                                       long revieweeId, List<Long> invitedUserIds,
                                       long actorUserId, long adminUserId) throws Exception {
    long templateId = reviewTemplate.getTemplateId();
    for (Long reviewerId : invitedUserIds) {
      try {
        ReviewInvitation inDb = reviewInvitationService.findReviewInvitationByTemplate(
                orgId, templateId, revieweeId, reviewerId);
        if (inDb != null) {
          continue;
        }
      } catch (ServiceStatusException e) {

      } catch (Exception e) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }

      ReviewInvitation reviewInvitation = new ReviewInvitation();
      reviewInvitation.setOrgId(orgId);
      reviewInvitation.setTemplateId(templateId);
      reviewInvitation.setRevieweeId(revieweeId);
      reviewInvitation.setReviewerId(reviewerId);
      reviewInvitation.setIsManager(0);
      reviewInvitation.setLastModifiedUserId(revieweeId);
      long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

      long currTs = TimeUtils.getNowTimestmapInMillis();
      if (currTs > reviewTemplate.getSelfReviewDeadline()) {
        reviewEmailUtils
            .sendInvitationEmail(orgId, templateId, invitationId, revieweeId, reviewerId, actorUserId, adminUserId);
        reviewMessageUtils
            .sendInvitationMessage(orgId, templateId, invitationId, revieweeId, reviewerId, actorUserId, adminUserId);
      }
    }
  }


  /**
   * Insert activity comment
   *
   * @param orgId
   * @param activityId
   * @param questionId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public LongDTO insertActivityComment(long orgId, long activityId, long questionId,
                                       String content,
                                       long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // # permission check
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and activity status check
      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      boolean status = false;
      if (!isSubmitted && !isPeerReviewDeadline) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # check whether comment already
      boolean existComment = reviewCommentService.existReviewItemCommentByReviewer(orgId, templateId,
              ReviewItemType.QUESTION.getCode(), questionId, actorUserId, actorUserId);
      if (existComment) {
        LOGGER.error("Exist comment!");
        throw new ServiceStatusException(ServiceStatus.REVIEW_COMMENT_EXIST);
      }

      // # insert comment
      ReviewComment reviewComment = new ReviewComment();

      reviewComment.setOrgId(orgId);
      reviewComment.setTemplateId(templateId);
      reviewComment.setRevieweeId(actorUserId);
      reviewComment.setReviewerId(actorUserId);
      reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
      reviewComment.setItemId(questionId);
      reviewComment.setContent(content);
      reviewComment.setLastModifiedUserId(actorUserId);

      long commentId = reviewCommentService.insertReviewComment(reviewComment);
      result.setData(commentId);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Update activity comment
   *
   * @param orgId
   * @param activityId
   * @param commentId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public VoidDTO updateActivityComment(long orgId, long activityId,
                                       long commentId, String content,
                                       long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # parameter valid check
      if (null == content || content.trim().isEmpty()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      // # permission check
      ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
      if (actorUserId != reviewComment.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and activity status check
      long templateId = reviewComment.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      boolean status = false;
      // not submitted and before 'peer' review deadline
      // submitted and before self review deadline, comments can only be modified
      if (!isSubmitted && !isPeerReviewDeadline) {
        status = true;
      } else if (isSubmitted && !isSelfReviewDeadline) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # update comment
      reviewComment.setContent(content);
      reviewComment.setLastModifiedTime(actorUserId);
      reviewCommentService.updateReviewComment(reviewComment);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  /**
   * HR view activity detail
   * HR 可以通过 report 的链接查看 activity 的 detail 信息
   * 思路与评价活动公示一样: 即可以查看所有 自评人 + 被邀请人 已经提交的评价; 但是没有修改权限
   *
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewActivityDetailDTO getReviewActivityDetailDTOByHR(long orgId, long activityId,
                                                                long actorUserId, long adminUserId) {

    ReviewActivityDetailDTO result = new ReviewActivityDetailDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

      long revieweeId = reviewActivity.getRevieweeId();

      int status = reviewTemplate.getState();
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != status &&
              ReviewTemplateStatus.FINISH.getCode() != status) {
        LOGGER.error("Template status error");
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
      BeanUtils.copyProperties(reviewTemplate, reviewTemplateDTO);

      // # list project
      List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
      List<ReviewProjectDTO> reviewProjectDTOs = getReviewProjectDTOs(reviewProjects);

      // # list question comments based on template status
      List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
      List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = getReviewQuestionDetailDTOsByHR(orgId, templateId, revieweeId,
              reviewQuestions, reviewTemplate, reviewActivity);

      // # fill detail VO
      result.setActivityId(activityId);
      result.setOrgId(orgId);
      result.setReviewTemplateDTO(reviewTemplateDTO);
      result.setRevieweeId(revieweeId);

      result.setReviewProjectDTOs(reviewProjectDTOs);
      result.setReviewQuestionDetailDTOs(reviewQuestionDetailDTOs);

      // # if submitted, fill detail score, it may be null
      try {
        if (reviewActivity.getIsSubmitted() == 1) {
          ReviewInvitation managerInvitation = reviewInvitationService.findManagerInvitation(orgId, templateId, revieweeId);
          result.setScore(managerInvitation.getScore());
        }
      } catch (Exception e) {
        LOGGER.info("getReviewActivityDetailDTOByHR no manager invitation");
      }

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  private List<ReviewQuestionDetailDTO> getReviewQuestionDetailDTOsByHR(long orgId, long templateId, long revieweeId,
                                                                        List<ReviewQuestion> reviewQuestions,
                                                                        ReviewTemplate reviewTemplate,
                                                                        ReviewActivity reviewActivity) throws Exception {

    boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = new ArrayList<>();
    for (ReviewQuestion reviewQuestion : reviewQuestions) {

      ReviewQuestionDetailDTO reviewQuestionDetailDTO = new ReviewQuestionDetailDTO();

      long questionId = reviewQuestion.getQuestionId();
      BeanUtils.copyProperties(reviewQuestion, reviewQuestionDetailDTO);

      // # 获取自评人的自评: 理论情况 <= 1, 当小于1时即还没有回答此问题
      List<ReviewCommentDTO> revieweeReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
              ReviewItemType.QUESTION.getCode(), questionId, revieweeId, Arrays.asList(revieweeId));
      if (revieweeReviewCommentDTOs.size() > 1) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      } else if (revieweeReviewCommentDTOs.size() == 1) {
        reviewQuestionDetailDTO.setRevieweeComment(revieweeReviewCommentDTOs.get(0));
      }

      // # 当自评已经提交后, 获取 被邀请人 提交的评价 ()
      if (isSubmitted) {
        List<Long> reviewerIds = new ArrayList<>();
        reviewerIds = reviewUtils.getSubmittedReviewerIds(orgId, templateId, revieweeId);
        List<ReviewCommentDTO> submittedReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
                ReviewItemType.QUESTION.getCode(), questionId, revieweeId, reviewerIds);
        reviewQuestionDetailDTO.setSubmittedComment(submittedReviewCommentDTOs);
      }

      reviewQuestionDetailDTOs.add(reviewQuestionDetailDTO);
    }
    return reviewQuestionDetailDTOs;
  }
}
