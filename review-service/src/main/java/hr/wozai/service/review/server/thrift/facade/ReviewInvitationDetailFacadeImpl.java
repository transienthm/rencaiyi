// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import hr.wozai.service.review.server.helper.ReviewInvitationHelper;
import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.utils.ReviewEmailUtils;
import hr.wozai.service.review.server.utils.ReviewMessageUtils;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.facade.ReviewInvitationDetailFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.model.ReviewQuestion;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.service.ReviewQuestionService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.utils.ReviewUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@Service("reviewInvitationDetailFacadeImpl")
public class ReviewInvitationDetailFacadeImpl implements ReviewInvitationDetailFacade {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationDetailFacadeImpl.class);

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewActivityService reviewActivityService;

  @Autowired
  private ReviewQuestionService reviewQuestionService;

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  @Autowired
  private ReviewUtils reviewUtils;

  @Autowired
  private ReviewEmailUtils reviewEmailUtils;

  @Autowired
  private ReviewMessageUtils reviewMessageUtils;

  /**
   * Get review invitation detail
   *
   * 前提: Template is IN_PROGRESS
   *
   * A 非主管的邀请:
   *
   * 1. 如果还没有提交: 在公示日之前, 是可以提交的 (submittable)
   *     question 下的 comment 可以修改, 因为是必添项所以没有删除接口
   *     project 下的 comment 可以修改, 可以删除
   *
   * 2. 如果提交了
   * (1) 如果在互评截止日之前, (与1相同)
   *     question 下的 comment 可以修改, 因为是必添项所以没有删除接口
   *     project 下的 comment 可以修改, 可以删除
   * (2) 如果过了互评截止日, 不能做任何修改
   *
   *
   * B 主管的邀请:
   *
   * 1. 如果还没有提交: 随时可以提交 (submittable)
   *     question 下的 comment 可以修改, 因为是必添项所以没有删除接口
   *     project 下的 comment 可以修改, 可以删除
   *
   * 2. 如果提交了, 与A基本相同, 期限有延长
   * (1) 如果在 公示日 之前,
   *     question 下的 comment 可以修改, 因为是必添项所以没有删除接口
   *     project 下的 comment 可以修改, 可以删除
   * (2) 如果在 公示日 之后, 不能做任何修改
   *
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @LogAround
  @Override
  public ReviewInvitationDetailDTO getReviewInvitationDetail(long orgId, long invitationId,
                                                             long actorUserId, long adminUserId) {

    ReviewInvitationDetailDTO result = new ReviewInvitationDetailDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # 当评价邀请取消后(被HR取消活动, 评价人主动拒绝而取消, 评价人在截止日前没有评价导致的取消)
      // # 不能查看详细信息
      if (1 == reviewInvitation.getIsCanceled()) {
        throw new ServiceStatusException(ServiceStatus.REVIEW_INVITATION_NOT_FOUND);
      }

      long templateId = reviewInvitation.getTemplateId();
      long revieweeId = reviewInvitation.getRevieweeId();

      // # template status
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
      BeanUtils.copyProperties(reviewTemplate, reviewTemplateDTO);

      boolean isManager = reviewInvitation.getIsManager() == 1;

      // # list project
      List<ReviewProjectDTO> reviewProjectDTOs =
          reviewUtils.getReviewProjectDTOs(orgId, templateId, revieweeId);

      // # list question
      List<ReviewQuestion> reviewQuestions =
          reviewQuestionService.listReviewQuestion(orgId, templateId);
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listAllReviewInvitationByTemplateIdAndRevieweeId(orgId, templateId, revieweeId);
        List<Long> allReviewerIds = new ArrayList<>();
        for (ReviewInvitation oneReviewInvitation: reviewInvitations) {
          allReviewerIds.add(oneReviewInvitation.getReviewerId());
        }

      // # list question comment base on template status
      List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = getReviewQuestionDetailDTOs(orgId, templateId,
          revieweeId, actorUserId, reviewQuestions, isManager, allReviewerIds, reviewTemplate, reviewInvitation);

      // # list old template
      List<ReviewPastInvitationDTO> pastInvitationDTOs =
          getPastInvitationDTO(orgId, revieweeId, actorUserId);

      ReviewActivity reviewActivity =
          reviewActivityService.findReviewActivityByRevieweeId(orgId, templateId, revieweeId);

      result.setInvitationId(invitationId);
      result.setOrgId(orgId);
      result.setReviewTemplateDTO(reviewTemplateDTO);
      result.setRevieweeId(revieweeId);
      result.setReviewProjectDTOs(reviewProjectDTOs);
      result.setReviewQuestionDetailDTOs(reviewQuestionDetailDTOs);
      result.setIsManager(reviewInvitation.getIsManager());
      result.setIsSubmitted(reviewInvitation.getIsSubmitted());
      result.setIsCanceled(reviewInvitation.getIsCanceled());
      result.setScore(reviewInvitation.getScore());
      result.setPastInvitationDTOs(pastInvitationDTOs);
      result.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
      result.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
      result.setPublicDeadline(reviewTemplate.getPublicDeadline());
      result.setIsSubmittable(getIsSubmittableForInvitation(reviewInvitation, reviewTemplate));
      if (1 == reviewActivity.getIsSubmitted()) {
        result.setIsInActive(1);
      } else {
        result.setIsInActive(0);
      }
    } catch (Exception e) {
      LOGGER.error("getReviewInvitationDetail(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * # 根据 Template 的状态, 设置信息的状态信息:IN_PROGRESS和FINISH可以设置
     # 只有在 Template 在 IN_PROGRESS 时, 评价邀请才可以 submit
     # 互评截止日前,互评可以提交,更新;
     # 互评截止日后,公示日前,互评只能提交,不能更新;
     # 公示日前,主管评价可以提交,更新;
     # 公示日后,主管评价只能提交,不能更新
   * @param reviewInvitation
   * @param reviewTemplate
   * @return
   */
  private int getIsSubmittableForInvitation(ReviewInvitation reviewInvitation, ReviewTemplate reviewTemplate) {
    boolean isManager = reviewInvitation.getIsManager() == 1;
    boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
    boolean isPublicDeadline = reviewUtils.isTemplatePublic(reviewTemplate);
    boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

    int result = 0;
    if (ReviewTemplateStatus.IN_PROGRESS.getCode() == reviewTemplate.getState()) {
      if (isManager) {
        if (!isPublicDeadline) {
          result = 1;
        } else if (isPublicDeadline
                   && !isSubmitted) {
          result = 1;
        } else {
          result = 0;
        }
      } else {
        if (!isPeerReviewDeadline) {
          result = 1;
        } else if (isPeerReviewDeadline
                   && !isSubmitted) {
          result = 1;
        } else {
          result = 0;
        }
      }
    }
    return result;
  }

  @LogAround
  private List<ReviewPastInvitationDTO> getPastInvitationDTO(long orgId, long revieweeId, long reviewerId) {

    List<ReviewPastInvitationDTO> pastInvitationDTOs = new ArrayList<>();

    List<ReviewInvitation> revieweeReviewInvitations = reviewInvitationService.listRevieweeReviewInvitation(orgId,
        revieweeId, reviewerId);

    List<Long> templateIds = new ArrayList<>();
    for(ReviewInvitation pastInvitation: revieweeReviewInvitations) {
      templateIds.add(pastInvitation.getTemplateId());
    }
    List<ReviewTemplate> reviewTemplates = reviewTemplateService.listReviewTemplateByTemplateIds(orgId, templateIds);

    for(ReviewInvitation pastInvitation: revieweeReviewInvitations) {
      ReviewPastInvitationDTO pastInvitationDTO = new ReviewPastInvitationDTO();
      pastInvitationDTO.setInvitationId(pastInvitation.getInvitationId());
      long pastTemplateId = pastInvitation.getTemplateId();
      String templateName = "";
      for(ReviewTemplate template: reviewTemplates) {
        if(template.getTemplateId() == pastTemplateId) {
          templateName = template.getTemplateName();
          break;
        }
      }
      pastInvitationDTO.setTemplateName(templateName);
      pastInvitationDTOs.add(pastInvitationDTO);
    }
    return pastInvitationDTOs;
  }


  @LogAround
  private List<ReviewQuestionDetailDTO> getReviewQuestionDetailDTOs(long orgId, long templateId,
                                                                    long revieweeId, long reviewerId,
                                                                    List<ReviewQuestion> reviewQuestions,
                                                                    boolean isManager, List<Long> allReviewerUserIds,
                                                                    ReviewTemplate reviewTemplate,
                                                                    ReviewInvitation reviewInvitation) throws Exception {

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = new ArrayList<>();

    boolean isCanceled = reviewInvitation.getIsCanceled() == 1;
    if (isCanceled) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    int status = reviewTemplate.getState();

    for(ReviewQuestion reviewQuestion: reviewQuestions) {

      ReviewQuestionDetailDTO reviewQuestionDetailDTO = new ReviewQuestionDetailDTO();
      BeanUtils.copyProperties(reviewQuestion, reviewQuestionDetailDTO);

      long questionId = reviewQuestion.getQuestionId();

      // reviewee comment
//      List<Long> reviewerIds = new ArrayList<>();
//      if (!isManager) {
//        reviewerIds.add(revieweeId);
//      } else {
//        reviewerIds = allReviewerUserIds;
//        reviewerIds.add(revieweeId);
//      }
      List<Long> reviewerIds = new ArrayList<>(Arrays.asList(revieweeId));
      List<ReviewCommentDTO> revieweeReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
          ReviewItemType.QUESTION.getCode(), questionId, revieweeId, reviewerIds);
//      if(revieweeReviewCommentDTOs.size() != 1) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//      }
      if (revieweeReviewCommentDTOs.size() == 1) {
        reviewQuestionDetailDTO.setRevieweeComment(revieweeReviewCommentDTOs.get(0));
      } else if (revieweeReviewCommentDTOs.size() > 1) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }

      // submitted comment
      if(ReviewTemplateStatus.FINISH.getCode() == status
          || ReviewTemplateStatus.IN_PROGRESS.getCode() == status) {
        // submitted review
        reviewerIds = reviewUtils.getSubmittedReviewerIds(orgId, templateId, revieweeId);
        // not include actorUserId
        reviewerIds.remove(reviewerId);
        List<ReviewCommentDTO> submittedReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
            ReviewItemType.QUESTION.getCode(), questionId, revieweeId, reviewerIds);
        reviewQuestionDetailDTO.setSubmittedComment(submittedReviewCommentDTOs);
      }

      // reviewer comment
      reviewerIds = new ArrayList<>();
      reviewerIds.add(reviewerId);
      List<ReviewCommentDTO> reviewerReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
          ReviewItemType.QUESTION.getCode(), questionId, revieweeId, reviewerIds);
      if(reviewerReviewCommentDTOs.size() > 1) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }
      else if(reviewerReviewCommentDTOs.size() == 1) {
        reviewQuestionDetailDTO.setReviewerComment(reviewerReviewCommentDTOs.get(0));
      }

      // set status
      reviewQuestionDetailDTO.setIsEditable(getIsSubmittableForInvitation(reviewInvitation, reviewTemplate));

      reviewQuestionDetailDTOs.add(reviewQuestionDetailDTO);
    }

    return reviewQuestionDetailDTOs;
  }

  @LogAround
  @Override
  public ReviewInvitedUserListDTO getReviewActivityInvitation(long orgId, long invitationId,
                                                              long managerUserId,
                                                              long actorUserId, long adminUserId) {

    ReviewInvitedUserListDTO result = new ReviewInvitedUserListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      if(managerUserId != actorUserId) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long templateId = reviewInvitation.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long revieweeId = reviewInvitation.getRevieweeId();
      result = reviewUtils.getReviewInvitedUsers(orgId, templateId, revieweeId, managerUserId);

    } catch (Exception e) {
      LOGGER.error("getReviewActivityInvitation(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public VoidDTO submitPeerReviewInvitation(long orgId, long invitationId,
                                        long managerUserId, int score,
                                        long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      if (managerUserId == actorUserId) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (1 == reviewInvitation.getIsCanceled()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      long templateId = reviewInvitation.getTemplateId();
      long revieweeId = reviewInvitation.getRevieweeId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // template status check
      int isSubmittable = getIsSubmittableForInvitation(reviewInvitation, reviewTemplate);
      if (isSubmittable == 0) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // comment number check
      long savedReviewCommentAmount = reviewCommentService.countReviewQuestionByReviewer(orgId, templateId,
          revieweeId, actorUserId);
      long reviewQuestionAmount = reviewQuestionService.countReviewQuestionOfTemplate(orgId, templateId);
      if (savedReviewCommentAmount != reviewQuestionAmount) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      reviewInvitation.setIsSubmitted(1);
      reviewInvitation.setLastModifiedUserId(actorUserId);
      reviewInvitationService.updateReviewInvitation(reviewInvitation);

      // notify reviewee's reporter
      long currTs = TimeUtils.getNowTimestmapInMillis();
      if (currTs > reviewTemplate.getPeerReviewDeadline()) {
        reviewEmailUtils.sendPeerReviewNotifyManagerEmail(
            orgId, actorUserId, revieweeId, reviewTemplate.getPeerReviewDeadline(),
            managerUserId, templateId, actorUserId, adminUserId);
        reviewMessageUtils
            .sendPeerReviewNotifyManagerMessage(orgId, templateId, actorUserId, revieweeId, managerUserId);
      }

    } catch (Exception e) {
      LOGGER.error("submitPeerReviewInvitation(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) validate
   *  2) cancel
   *
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param amdinUserId
   * @return
   */
  @Override
  @LogAround
  public VoidDTO cancelSubmissionOfPeerReviewInvitation(
      long orgId, long invitationId, long actorUserId, long amdinUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (1 == reviewInvitation.getIsCanceled()
          || actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      long templateId = reviewInvitation.getTemplateId();
      long revieweeId = reviewInvitation.getRevieweeId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      if (!ReviewInvitationHelper
          .isReviewSubmissionCancellable(reviewInvitation.getIsSubmitted(), reviewTemplate.getPeerReviewDeadline())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // 2)
      reviewInvitation.setIsSubmitted(0);
      reviewInvitation.setLastModifiedUserId(actorUserId);
      reviewInvitationService.updateReviewInvitation(reviewInvitation);

    } catch (Exception e) {
      LOGGER.error("cancelSubmissionOfPeerReviewInvitation(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public VoidDTO submitManagerReviewInvitation(long orgId, long invitationId,
                                            long managerUserId, int score,
                                            long actorUserId, long amdinUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      if (managerUserId != actorUserId) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (1 == reviewInvitation.getIsCanceled()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      long templateId = reviewInvitation.getTemplateId();
      long revieweeId = reviewInvitation.getRevieweeId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // template status check
      int isSubmittable = getIsSubmittableForInvitation(reviewInvitation, reviewTemplate);
      if (isSubmittable == 0) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // comment number check
      long savedReviewCommentAmount =
          reviewCommentService.countReviewQuestionByReviewer(orgId, templateId, revieweeId, actorUserId);
      long reviewQuestionAmount = reviewQuestionService.countReviewQuestionOfTemplate(orgId, templateId);
      if (savedReviewCommentAmount != reviewQuestionAmount) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      reviewInvitation.setIsSubmitted(1);
      reviewInvitation.setLastModifiedUserId(actorUserId);
        if(score <= 0 || score > 500) {
          throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }
        reviewInvitation.setScore(score);
      reviewInvitationService.updateReviewInvitation(reviewInvitation);

    } catch (Exception e) {
      LOGGER.error("submitManagerReviewInvitation(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) validate
   *  2) cancel
   *
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param amdinUserId
   * @return
   */
  @Override
  public VoidDTO cancelSubmissionOfManagerReviewInvitation(
      long orgId, long invitationId, long actorUserId, long amdinUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (1 == reviewInvitation.getIsCanceled()
          || actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      long templateId = reviewInvitation.getTemplateId();
      long revieweeId = reviewInvitation.getRevieweeId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      if (!ReviewInvitationHelper
          .isReviewSubmissionCancellable(reviewInvitation.getIsSubmitted(), reviewTemplate.getPublicDeadline())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // 2)
      reviewInvitation.setIsSubmitted(0);
      reviewInvitation.setLastModifiedUserId(actorUserId);
      reviewInvitationService.updateReviewInvitation(reviewInvitation);

    } catch (Exception e) {
      LOGGER.error("cancelSubmissionOfManagerReviewInvitation(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  @LogAround
  @Override
  public LongDTO insertInvitationComment(long orgId, long invitationId, long questionId,
                                         long managerUserId,
                                         String content,
                                         long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # parameter check
      if (null == content || content.trim().isEmpty()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      // # permission check
      ReviewInvitation reviewInvitation =
          reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and activity status check
      long templateId = reviewInvitation.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isCanceled = reviewInvitation.getIsCanceled() == 1;
      if (isCanceled) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isManger = actorUserId == managerUserId;
      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      boolean isTemplatePublic = reviewUtils.isTemplatePublic(reviewTemplate);

      boolean status = false;
      if (!isManger) {
        if (!isSubmitted && !isTemplatePublic) {
          status = true;
        }
      } else {
        if (!isSubmitted) {
          status = true;
        }
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long revieweeId = reviewInvitation.getRevieweeId();

      // # check whether comment already
      boolean existComment = reviewCommentService.existReviewItemCommentByReviewer(orgId, templateId,
          ReviewItemType.QUESTION.getCode(), questionId, revieweeId, actorUserId);
      if(existComment) {
        LOGGER.error("Exist comment!");
        throw new ServiceStatusException(ServiceStatus.REVIEW_COMMENT_EXIST);
      }

      // # insert comment
      ReviewComment reviewComment = new ReviewComment();
      reviewComment.setOrgId(orgId);
      reviewComment.setTemplateId(templateId);
      reviewComment.setRevieweeId(revieweeId);
      reviewComment.setReviewerId(actorUserId);
      reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
      reviewComment.setItemId(questionId);
      reviewComment.setContent(content);
      reviewComment.setLastModifiedUserId(actorUserId);

      long commentId = reviewCommentService.insertReviewComment(reviewComment);
      result.setData(commentId);
    } catch (Exception e) {
      LOGGER.error("insertInvitationComment(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  @LogAround
  @Override
  public VoidDTO updateInvitationComment(long orgId, long invitationId, long commentId,
                                         long managerUserId,
                                         String content,
                                         long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # parameter check
      if (null == content || content.trim().isEmpty()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      // # permission check
      ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
      if (actorUserId != reviewComment.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and invitation status check
      long templateId = reviewComment.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      boolean isCanceled = reviewInvitation.getIsCanceled() == 1;
      if (isCanceled) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isManger = actorUserId == managerUserId;
      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean isTemplatePublic = reviewUtils.isTemplatePublic(reviewTemplate);

      boolean status = false;
      //manager has privilege to modify comment before template public
      if (!isManger) {
        if (!isSubmitted && !isTemplatePublic) {
          status = true;
        } else if (isSubmitted && !isPeerReviewDeadline) {
          status = true;
        }
      } else {
        if (!isSubmitted) {
          status = true;
        } else if (isSubmitted && !isTemplatePublic) {
          status = true;
        }
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      reviewComment.setContent(content);
      reviewComment.setLastModifiedUserId(actorUserId);
      reviewCommentService.updateReviewComment(reviewComment);

    } catch (Exception e) {
      LOGGER.error("updateInvitationComment(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

}
