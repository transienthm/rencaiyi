// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.review.client.enums.ReviewRoleType;
import hr.wozai.service.review.server.model.*;
import hr.wozai.service.servicecommons.commons.utils.FastJSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.enums.TemplateListType;
import hr.wozai.service.review.client.facade.ReviewTemplateFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.service.ReviewProjectService;
import hr.wozai.service.review.server.service.ReviewQuestionService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.utils.ReviewActivityNameSortUtils;
import hr.wozai.service.review.server.utils.ReviewActivityScoreSortUtils;
import hr.wozai.service.review.server.utils.ReviewEmailUtils;
import hr.wozai.service.review.server.utils.ReviewMessageUtils;
import hr.wozai.service.review.server.utils.ReviewUtils;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@Service("reviewTemplateFacadeImpl")
public class ReviewTemplateFacadeImpl implements ReviewTemplateFacade {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateFacadeImpl.class);

  private static final String SELF_EVALUATE = "selfEvaluate";
  private static final String PEER_EVALUATE = "peerEvaluate";
  private static final String MANAGER_EVALUATE = "managerEvaluate";
  private static final String PUBLIC_DAY = "publicDay";
  private static final String REVIEW_TEMPLATE = "reviewTemplate";
  private static final String REVIEW_ACTIVITIES = "reviewActivities";
  private static final String REVIEW_INVITATIONS = "reviewInvitations";
  private static final String REVIEW_COMMENTS = "reviewComments";
  private static final String REVIEW_QUESTIONS = "reviewQuestions";
  private static final String REVIEW_PROJECTS = "reviewProjects";
  private static final String REVIEW_INVITED_TEAMS = "reviewInvitedTeams";

  private static String ORDER_BY_NAME = "name";
  private static String ORDER_BY_SCORE = "score";
  private static String ASC = "ASC";
  private static String DESC = "DESC";

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
  private ReviewEmailUtils reviewEmailUtils;

  @Autowired
  private ReviewMessageUtils reviewMessageUtils;

  @Autowired
  private ReviewActivityNameSortUtils nameSortUtils;

  @Autowired
  private ReviewActivityScoreSortUtils scoreSortUtils;

  @Autowired
  private ReviewUtils reviewUtils;

//  @Autowired
//  @Qualifier("userFacadeProxy")
//  private ThriftClientProxy userFacadeProxy;
//
//  private UserFacade userFacade;
//
//  @PostConstruct
//  public void init() throws Exception {
//    userFacade = (UserFacade) userFacadeProxy.getObject();
//  }

  /**
   * Steps:
   *  1) insert review template
   *  2) insert invited teams
   *
   * @param orgId
   * @param reviewTemplateDTO
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public LongDTO insertReviewTemplate(
      long orgId, ReviewTemplateDTO reviewTemplateDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      ReviewTemplate reviewTemplate = new ReviewTemplate();
      BeanHelper.copyPropertiesHandlingJSON(reviewTemplateDTO, reviewTemplate);
      reviewTemplate.setPublishedTime(TimeUtils.getNowTimestmapInMillis());
      reviewTemplateService.insertReviewTemplate(reviewTemplate);
      result.setData(reviewTemplate.getTemplateId());

      // 2)
      List<Long> teamIds = reviewTemplateDTO.getTeamIds();
      if (CollectionUtils.isEmpty(teamIds)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      List<ReviewInvitedTeam> reviewInvitedTeams = new ArrayList<>();
      for (Long teamId: teamIds) {
        ReviewInvitedTeam reviewInvitedTeam = new ReviewInvitedTeam();
        reviewInvitedTeam.setOrgId(orgId);
        reviewInvitedTeam.setReviewTemplateId(reviewTemplate.getTemplateId());
        reviewInvitedTeam.setTeamId(teamId);
        reviewInvitedTeams.add(reviewInvitedTeam);
      }
      reviewTemplateService.batchInsertReviewInvitedTeam(reviewInvitedTeams);

    } catch (Exception e) {
      LOGGER.error("insertReviewTemplate(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Find review template
   * @param orgId
   * @param templateId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewTemplateDTO findReviewTemplate(long orgId, long templateId, long actorUserId, long adminUserId) {

    ReviewTemplateDTO result = new ReviewTemplateDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewTemplate remoteResult = reviewTemplateService.findReviewTemplate(orgId, templateId);
      // make sure the actor is invited
      List<ReviewActivity> invitedActivities =
          reviewActivityService.listAllReviewActivityOfRevieweeId(orgId, actorUserId);
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listAllReviewInvitationOfReviewer(orgId, actorUserId);
      Set<Long> visibleTemplateIds = new HashSet<>();
      for (ReviewInvitation reviewInvitation: reviewInvitations) {
        visibleTemplateIds.add(reviewInvitation.getTemplateId());
      }
      for (ReviewActivity reviewActivity : invitedActivities) {
        visibleTemplateIds.add(reviewActivity.getTemplateId());
      }
      if (!visibleTemplateIds.contains(remoteResult.getTemplateId())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      BeanHelper.copyPropertiesHandlingJSON(remoteResult, result);
    } catch (Exception e) {
      LOGGER.error("findReviewTemplate(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * List review template by orgId and status
   *
   * @param orgId
   * @param statuses
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @LogAround
  @Override
  public ReviewTemplateListDTO listReviewTemplate(
      long orgId, int pageNumber, int pageSize, List<Integer> statuses, int templateListType,
      boolean isHr, long actorUserId, long adminUserId) {

    ReviewTemplateListDTO result = new ReviewTemplateListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      if (null == TemplateListType.getEnumByCode(templateListType)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      List<ReviewTemplate> reviewTemplates =
          reviewTemplateService.listReviewTemplate(orgId, pageNumber, pageSize, statuses);
      List<ReviewTemplateDTO> reviewTemplateDTOs = new ArrayList<>();

      List<ReviewActivity> invitedActivities =
          reviewActivityService.listAllReviewActivityOfRevieweeId(orgId, actorUserId);
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listAllReviewInvitationOfReviewer(orgId, actorUserId);
      Set<Long> visibleTemplateIds = new HashSet<>();
      Map<Long, ReviewTemplate> reviewTemplateMap = new HashMap<>();
      for (ReviewTemplate reviewTemplate: reviewTemplates) {
        reviewTemplateMap.put(reviewTemplate.getTemplateId(), reviewTemplate);
      }
      for (ReviewInvitation reviewInvitation: reviewInvitations) {
        if (reviewTemplateMap.containsKey(reviewInvitation.getTemplateId())) {
          ReviewTemplate reviewTemplate = reviewTemplateMap.get(reviewInvitation.getTemplateId());
          long currTs = TimeUtils.getNowTimestmapInMillis();
          if (reviewInvitation.getIsManager() == 1) {
            if (currTs >= reviewTemplate.getPeerReviewDeadline()) {
              visibleTemplateIds.add(reviewInvitation.getTemplateId());
            }
          } else {
            if (currTs >= reviewTemplate.getSelfReviewDeadline()) {
              visibleTemplateIds.add(reviewInvitation.getTemplateId());
            }
          }
        }
      }
      for (ReviewActivity reviewActivity: invitedActivities) {
        visibleTemplateIds.add(reviewActivity.getTemplateId());
      }
      for(ReviewTemplate reviewTemplate: reviewTemplates) {

        if (!isHr
            || templateListType == TemplateListType.VIEW.getCode()) {
          // make sure the actor is invited
          if (!visibleTemplateIds.contains(reviewTemplate.getTemplateId())) {
            continue;
          }
        }

        ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();

        BeanHelper.copyPropertiesHandlingJSON(reviewTemplate, reviewTemplateDTO);
        reviewTemplateDTO.setQuestions(reviewTemplate.getQuestions());

        // If in progress or finished, show statistic data
        // TODO: Remove, as it is replaced with template report
        if (ReviewTemplateStatus.IN_PROGRESS.getCode() == reviewTemplateDTO.getState() ||
            ReviewTemplateStatus.FINISH.getCode() == reviewTemplateDTO.getState() ) {

          long templateId = reviewTemplateDTO.getTemplateId();

          long revieweeNumber = reviewActivityService.countReviewActivityOfTemplate(orgId, templateId);
          reviewTemplateDTO.setRevieweeNumber(revieweeNumber);

          long invitedNumber = reviewInvitationService.countReviewInvitationOfTemplate(orgId, templateId);
          reviewTemplateDTO.setInvitedNumber(invitedNumber);

          long finishedNumber = reviewInvitationService.countFinishedReviewInvitationOfTemplate(orgId, templateId);
          reviewTemplateDTO.setFinishedNumber(finishedNumber);
        }

        // handle invitedTeams
        List<ReviewInvitedTeam> reviewInvitedTeams =
            reviewTemplateService.listReviewInvitedTeam(orgId, reviewTemplate.getTemplateId());
        List<Long> teamIds = new ArrayList<>();
        for (ReviewInvitedTeam reviewInvitedTeam: reviewInvitedTeams) {
          teamIds.add(reviewInvitedTeam.getTeamId());
        }
        reviewTemplateDTO.setTeamIds(teamIds);

        reviewTemplateDTOs.add(reviewTemplateDTO);
      }

      result.setReviewTemplateDTOs(reviewTemplateDTOs);
    } catch (Exception e) {
      LOGGER.error("listReviewTemplate(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Count review template
   * @param orgId
   */
  @LogAround
  @Override
  public LongDTO countReviewTemplate(long orgId) {
    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long amount = reviewTemplateService.countReviewTemplate(orgId);
      result.setData(amount);
    } catch (Exception e) {
      LOGGER.error("countReviewTemplate(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

//  /**
//   * Update review template
//   * @param orgId
//   * @param reviewTemplateDTO
//   * @param actorUserId
//   * @param adminUserId
//   */
//  @LogAround
//  @Override
//  public VoidDTO updateReviewTemplate(long orgId, ReviewTemplateDTO reviewTemplateDTO, long actorUserId, long adminUserId) {
//
//    VoidDTO result = new VoidDTO();
//
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//
//      long templateId = reviewTemplateDTO.getTemplateId();
//      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
//      if(ReviewTemplateStatus.DRAFT.getCode() != reviewTemplate.getState()) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST.getCode());
//      }
//
//      BeanHelper.copyPropertiesHandlingJSON(reviewTemplateDTO, reviewTemplate);
//      reviewTemplateService.updateReviewTemplate(reviewTemplate);
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//    }
//
//    return result;
//  }

//  /**
//   * Publish review template
//   * @param orgId
//   * @param templateId
//   * @param lastModifiedUserId
//   * @param actorUserId
//   * @param adminUserId
//   */
//  @LogAround
//  @Override
//  public VoidDTO publishReviewTemplate(
//      long orgId, long templateId, long lastModifiedUserId, long actorUserId, long adminUserId) {
//
//    VoidDTO result = new VoidDTO();
//
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//
//      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
//      if(ReviewTemplateStatus.DRAFT.getCode() != reviewTemplate.getState()) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST.getCode());
//      }
//
//      reviewTemplateService.publishReviewTemplate(orgId, templateId, lastModifiedUserId);
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//    }
//
//    return result;
//  }


  /**
   * Cancel review template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public VoidDTO cancelReviewTemplate(long orgId, long templateId, long lastModifiedUserId,
                                      long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if(ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST.getCode());
      }

      reviewTemplateService.cancelReviewTemplate(orgId, templateId, lastModifiedUserId);

      // Cancel all activity
      cancelReviewActivities(orgId, templateId, lastModifiedUserId, actorUserId, adminUserId);

      // Cancel all invitation
      cancelReviewInvitations(orgId, templateId, lastModifiedUserId, actorUserId, adminUserId);

    } catch (Exception e) {
      LOGGER.error("cancelReviewTemplate(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  private void cancelReviewActivities(long orgId, long templateId,
                                      long lastModifiedUserId,
                                      long actorUserId, long adminUserId) {

    // Cancel all activity
    List<ReviewActivity> reviewActivities =
        reviewActivityService.listUnCanceledReviewActivityOfTemplate(orgId, templateId);

    for(ReviewActivity reviewActivity: reviewActivities) {

      reviewActivity.setIsCanceled(1);
      reviewActivity.setLastModifiedUserId(lastModifiedUserId);
      reviewActivityService.updateReviewActivity(reviewActivity);

      long revieweeId = reviewActivity.getRevieweeId();
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;

      // Send email
      if( !isSubmitted ) {
        try {
          reviewEmailUtils.sendUnSubmittedActivityCancelEmail(orgId, templateId, revieweeId,
                  actorUserId, adminUserId);
          reviewMessageUtils.sendCancelMessage(orgId, templateId, revieweeId, actorUserId, adminUserId);
        }catch (Exception e) {
          LOGGER.error(e.toString());
        }

        // delete comment after send email, or email cannot find comment
        reviewCommentService.deleteReviewCommentByReviewer(orgId, templateId,
                revieweeId, revieweeId, lastModifiedUserId);
      }
      else {
        try {
          reviewEmailUtils.sendSubmittedCancelEmail(orgId, templateId, revieweeId, actorUserId, adminUserId);
          reviewMessageUtils.sendCancelMessage(orgId, templateId, revieweeId, actorUserId, adminUserId);
        } catch (Exception e) {
          LOGGER.error(e.toString());
        }
      }
    }
  }

  @LogAround
  private void cancelReviewInvitations(long orgId, long templateId, long lastModifiedUserId,
                                       long actorUserId, long adminUserId) {

    // Cancel all invitation
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationService.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);

    for(ReviewInvitation reviewInvitation: reviewInvitations) {

      reviewInvitation.setIsCanceled(1);
      reviewInvitation.setLastModifiedUserId(lastModifiedUserId);
      reviewInvitationService.updateReviewInvitation(reviewInvitation);

      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;

      long revieweeId = reviewInvitation.getRevieweeId();
      long reviewerId = reviewInvitation.getReviewerId();

      // Send email
      if( !isSubmitted ) {
        try {
          reviewEmailUtils.sendUnSubmittedInvitationCancelEmail(orgId, templateId,
                  revieweeId, reviewerId, actorUserId, adminUserId);
          reviewMessageUtils.sendCancelMessage(orgId, templateId, reviewerId, actorUserId, adminUserId);
        } catch (Exception e) {
          LOGGER.error(e.toString());
        }

        reviewCommentService.deleteReviewCommentByReviewer(orgId, templateId,
                revieweeId, reviewerId, lastModifiedUserId);
      }
      else {
        try {
          reviewEmailUtils.sendSubmittedCancelEmail(orgId, templateId, reviewerId,
                  actorUserId, adminUserId);
          reviewMessageUtils.sendCancelMessage(orgId, templateId, reviewerId, actorUserId, adminUserId);
        } catch (Exception e) {
          LOGGER.error(e.toString());
        }
      }
    }
  }

  /**
   * List review template by template ids
   * @param orgId
   * @param templateIds
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewTemplateListDTO listReviewTemplateByTemplateIds(long orgId, List<Long> templateIds,
                                                               long actorUserId, long adminUserId) {

    ReviewTemplateListDTO result = new ReviewTemplateListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewTemplate> reviewTemplates = reviewTemplateService.listReviewTemplateByTemplateIds(orgId, templateIds);
      List<ReviewTemplateDTO> reviewTemplateDTOs = new ArrayList<>();

      for(ReviewTemplate reviewTemplate: reviewTemplates) {

        ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();

        BeanHelper.copyPropertiesHandlingJSON(reviewTemplate, reviewTemplateDTO);
        reviewTemplate.setQuestions(reviewTemplate.getQuestions());

        reviewTemplateDTOs.add(reviewTemplateDTO);
      }

      result.setReviewTemplateDTOs(reviewTemplateDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Get review template report
   * @param orgId
   * @param templateId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewReportDTO getReviewTemplateReport(long orgId, long templateId,
                                                 long actorUserId, long adminUserId) {

    ReviewReportDTO result = new ReviewReportDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // template status check
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (reviewTemplate.getState() != ReviewTemplateStatus.IN_PROGRESS.getCode() &&
              reviewTemplate.getState() != ReviewTemplateStatus.FINISH.getCode()) {
        LOGGER.error("Get template report: template status error");
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
      BeanHelper.copyPropertiesHandlingJSON(reviewTemplate, reviewTemplateDTO);

      List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
      List<String> questions = new ArrayList<>();
      for (ReviewQuestion reviewQuestion : reviewQuestions) {
        questions.add(reviewQuestion.getName());
      }
      reviewTemplateDTO.setQuestions(questions);

      result.setReviewTemplateDTO(reviewTemplateDTO);

      setSelfReviewStatistics(orgId, templateId, result);
      setStaffReviewStatistics(orgId, templateId, result);
      setManagerReviewStatistics(orgId, templateId, result);
      setManagerScore(orgId, templateId, result);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * 获取 自评 的统计信息
   * @param orgId
   * @param templateId
   * @param result
   */
  private void setSelfReviewStatistics(long orgId, long templateId,
                                      ReviewReportDTO result) {

    List<Long> selfActivities = reviewActivityService.listAllRevieweeIdOfTemplate(orgId, templateId);

    List<Long> selfSubmittedActivities = reviewActivityService.listSubmittedRevieweeIdOfTemplate(orgId, templateId);
    result.setSelfFinished(selfSubmittedActivities.size());

    List<Long> leftActivities = new ArrayList<>(selfActivities);
    leftActivities.removeAll(selfSubmittedActivities);

    // 如果增加了 project 或者 comment 即为进行中
    List<Long> commentsActivities = reviewCommentService.listActivityOfTemplate(orgId, templateId, leftActivities);
    List<Long> projectActivities = reviewProjectService.listProjectRevieweeIdOfTemplate(orgId, templateId);

    List<Long> inProgressActivities = new ArrayList<>();
    for(Long revieweeId: leftActivities) {
      if(commentsActivities.contains(revieweeId) || projectActivities.contains(revieweeId)) {
        inProgressActivities.add(revieweeId);
      }
    }
    result.setSelfInProgress(inProgressActivities.size());

    result.setSelfNotBegin(leftActivities.size() - inProgressActivities.size());
  }

  private void setStaffReviewStatistics(long orgId, long templateId,
                                        ReviewReportDTO result) {

    List<ReviewInvitation> staffReviewInvitations = reviewInvitationService.listStaffReviewerIdOfTemplate(orgId, templateId);

    List<ReviewInvitation> staffSubmittedInvitations = reviewInvitationService.listStaffSubmittedReviewerIdOfTemplate(orgId, templateId);
    result.setPeerFinished(staffSubmittedInvitations.size());

    List<ReviewInvitation> leftInvitations = new ArrayList<>(staffReviewInvitations);
    leftInvitations.removeAll(staffSubmittedInvitations);

    List<Long> reviewerIds = new ArrayList<>();
    for(ReviewInvitation reviewInvitation: leftInvitations) {
      reviewerIds.add(reviewInvitation.getReviewerId());
    }
    List<ReviewInvitation> commentsInvitations = reviewCommentService.listInvitationOfTemplate(orgId, templateId, reviewerIds);

    List<ReviewInvitation> inProgressInvitations = new ArrayList<>();
    for(ReviewInvitation reviewInvitation: leftInvitations) {
      if(commentsInvitations.contains(reviewInvitation) ) {
        inProgressInvitations.add(reviewInvitation);
      }
    }
    result.setPeerInProgress(inProgressInvitations.size());

    result.setPeerNotBegin(leftInvitations.size() - inProgressInvitations.size());
  }

  private void setManagerReviewStatistics(long orgId, long templateId,
                                          ReviewReportDTO result) {

    List<ReviewInvitation> managerReviewInvitations = reviewInvitationService.listManagerReviewerIdOfTemplate(orgId, templateId);

    List<ReviewInvitation> managerSubmittedInvitations = reviewInvitationService.listManagerSubmittedReviewerIdOfTemplate(orgId, templateId);
    result.setManagerFinished(managerSubmittedInvitations.size());

    List<ReviewInvitation> leftInvitations = new ArrayList<>(managerReviewInvitations);
    leftInvitations.removeAll(managerSubmittedInvitations);

    List<Long> reviewerIds = new ArrayList<>();
    for(ReviewInvitation reviewInvitation: leftInvitations) {
      reviewerIds.add(reviewInvitation.getReviewerId());
    }
    List<ReviewInvitation> commentsInvitations = reviewCommentService.listInvitationOfTemplate(orgId, templateId, reviewerIds);

    List<ReviewInvitation> inProgressInvitations = new ArrayList<>();
    for(ReviewInvitation reviewInvitation: leftInvitations) {
      if(commentsInvitations.contains(reviewInvitation) ) {
        inProgressInvitations.add(reviewInvitation);
      }
    }
    result.setManagerInProgress(inProgressInvitations.size());

    result.setManagerNotBegin(leftInvitations.size() - inProgressInvitations.size());
  }

  @LogAround
  private void setManagerScore(long orgId, long templateId, ReviewReportDTO result) {
    Map<Integer, Long> scores = reviewInvitationService.countReviewInvitationScore(orgId, templateId);
    result.setManagerScore(scores);
  }

  /**
   * Get review activities of template
   * @param orgId
   * @param templateId
   * @param orderBy
   * @param direction
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewActivityUserListDTO getActivitiesOfTemplate(long orgId, long templateId,
                                                           String orderBy, String direction,
                                                           long actorUserId, long adminUserId) {

    ReviewActivityUserListDTO result = new ReviewActivityUserListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // status check
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (reviewTemplate.getState() != ReviewTemplateStatus.IN_PROGRESS.getCode() &&
              reviewTemplate.getState() != ReviewTemplateStatus.FINISH.getCode()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      List<ReviewActivity> activities = reviewActivityService.listAllReviewActivityOfTemplate(orgId, templateId);

      List<ReviewInvitation> invitations = reviewInvitationService.listAllReviewInvitationOfTemplate(orgId, templateId);

      List<ReviewActivityUserDTO> activityUserDTOs = new ArrayList<>();

      for (ReviewActivity reviewActivity : activities) {

        int isInActive = (0 == reviewActivity.getIsSubmitted()) ? 1 : 0;

        ReviewActivityUserDTO reviewActivityUserDTO = new ReviewActivityUserDTO();

        ReviewActivityDTO reviewActivityDTO = new ReviewActivityDTO();
        BeanUtils.copyProperties(reviewActivity, reviewActivityDTO);
        reviewActivityDTO.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
        reviewActivityDTO.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
        reviewActivityUserDTO.setActivityDTO(reviewActivityDTO);

        List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
        for (ReviewInvitation reviewInvitation : invitations) {

          ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
          BeanUtils.copyProperties(reviewInvitation, reviewInvitationDTO);
          reviewInvitationDTO.setIsInActive(isInActive);
          reviewInvitationDTO.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
          reviewInvitationDTO.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
          reviewInvitationDTO.setPublicDeadline(reviewTemplate.getPublicDeadline());

          if (reviewInvitation.getRevieweeId().equals(reviewActivity.getRevieweeId())) {
            if (reviewInvitation.getIsManager() == 1) {
              reviewActivityUserDTO.setManagerInvitationDTO(reviewInvitationDTO);
            } else {
              reviewInvitationDTOs.add(reviewInvitationDTO);
            }
          }
        }
        reviewActivityUserDTO.setStaffInvitationDTOs(reviewInvitationDTOs);

        activityUserDTOs.add(reviewActivityUserDTO);
      }

      reviewUtils.fillManagerNameInReviewActivityUserDTOs(orgId, activityUserDTOs);

      if(orderBy.equals(ORDER_BY_NAME)) {
        Collections.sort(activityUserDTOs, nameSortUtils);

        if(direction.equals(DESC)) {
          List<?> shallowCopy = activityUserDTOs.subList(0, activityUserDTOs.size());
          Collections.reverse(shallowCopy);
        }
      }

      if(orderBy.equals(ORDER_BY_SCORE)) {
        Collections.sort(activityUserDTOs, scoreSortUtils);

        if(direction.equals(DESC)) {
          List<?> shallowCopy = activityUserDTOs.subList(0, activityUserDTOs.size());
          Collections.reverse(shallowCopy);
        }
      }

      result.setActivityUserDTOs(activityUserDTOs);

    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

//  /**
//   * @param orgId
//   * @param actorUserId
//   * @param adminUserId
//   * @return
//   */
//  @LogAround
//  @Override
//  public ReviewTemplateListDTO listAllValidReviewTemplates(long orgId, long actorUserId, long adminUserId) {
//    ReviewTemplateListDTO result = new ReviewTemplateListDTO();
//
//    ServiceStatusDTO serviceStatusDTO =
//            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      List<ReviewTemplateDTO> reviewTemplateDTOs = new ArrayList<>();
//      List<ReviewTemplate> reviewTemplates = reviewTemplateService.listAllValidReviewTemplates(orgId);
//
//      if (!CollectionUtils.isEmpty(reviewTemplates)) {
//        for (ReviewTemplate reviewTemplate : reviewTemplates) {
//          ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
//
//          List<ReviewInvitedTeam> reviewInvitedTeams =
//                  reviewTemplateService.listReviewInvitedTeam(orgId, reviewTemplate.getTemplateId());
//
//          List<Long> teamIds = new ArrayList<>();
//          if (!CollectionUtils.isEmpty(reviewInvitedTeams)) {
//            reviewInvitedTeams.forEach(
//                    (reviewInvitedTeam) -> teamIds.add(reviewInvitedTeam.getTeamId())
//            );
//          }
//          reviewTemplateDTO.setTeamIds(teamIds);
//
//          BeanHelper.copyPropertiesHandlingJSON(reviewTemplate, reviewTemplateDTO);
//          List<String> questions = reviewTemplate.getQuestions();
//          if (questions == null) {
//            questions = new ArrayList<>();
//          }
//          reviewTemplateDTO.setQuestions(questions);
//          reviewTemplateDTOs.add(reviewTemplateDTO);
//        }
//      }
//
//      result.setReviewTemplateDTOs(reviewTemplateDTOs);
//    } catch (Exception e) {
//      LOGGER.error("listAllValidReviewTemplates(): error", e);
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//    }
//
//    return result;
//  }

  /**
   * @param reviewTemplate
   * @return
   */
  @LogAround
  private boolean isValidTemplate(ReviewTemplate reviewTemplate, ReviewRoleType role) {
    if (reviewTemplate.getState() != ReviewTemplateStatus.IN_PROGRESS.getCode()) {
      return false;
    }
    long currentTime = System.currentTimeMillis();
    switch (role) {
      case SELF:
        if (reviewTemplate.getPublishedTime() == null) {
          return false;
        }
        return currentTime > reviewTemplate.getPublishedTime() &&
                currentTime <= reviewTemplate.getPublicDeadline();
      case PEER:
        return currentTime > reviewTemplate.getSelfReviewDeadline() &&
                currentTime <= reviewTemplate.getPublicDeadline();
      case MANAGER:
        return currentTime > reviewTemplate.getPeerReviewDeadline() &&
                currentTime <= reviewTemplate.getPublicDeadline();
      default:
        break;
    }
    return false;
  }

  @LogAround
  @Override
  public ReviewTemplateListDTO listAllValidTemplatesForActivitiesOfHomepage(
          long orgID, long actorUserID) {
    ReviewTemplateListDTO result = new ReviewTemplateListDTO();

    ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
            ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg()
    );
    result.setServiceStatusDTO(serviceStatusDTO);
    List<ReviewTemplateDTO> reviewTemplateDTOs = new ArrayList<>();
    result.setReviewTemplateDTOs(reviewTemplateDTOs);

    try {
      List<ReviewTemplate> reviewTemplates = reviewTemplateService.listAllValidReviewTemplates(orgID);
      if (CollectionUtils.isEmpty(reviewTemplates)) {
        return result;
      }

      HashSet<Long> allValidTemplatesSet = new HashSet<>();
      HashMap<Long, ReviewTemplate> mapTemplatesInfo = new HashMap<>();

      for (ReviewTemplate reviewTemplate: reviewTemplates) {
        long templateID = reviewTemplate.getTemplateId();
        allValidTemplatesSet.add(templateID);
        mapTemplatesInfo.put(templateID, reviewTemplate);
      }

      List<Long> templatesIncludeRevieweeList = reviewActivityService
              .listAllValidReviewActivitiesByRevieweeAndTemplatesList(
                      orgID, new ArrayList<>(allValidTemplatesSet), actorUserID
              );
      if (CollectionUtils.isEmpty(templatesIncludeRevieweeList)) {
        return result;
      }

      for (long templateID: templatesIncludeRevieweeList) {

        if (!isValidTemplate(mapTemplatesInfo.get(templateID), ReviewRoleType.SELF)) {
          continue;
        }

        List<ReviewInvitedTeam> invitedTeams = reviewTemplateService.listReviewInvitedTeam(orgID, templateID);
        if (CollectionUtils.isEmpty(invitedTeams)) {
          continue;
        }
        HashSet<Long> teamIds = new HashSet<>();
        invitedTeams.forEach(
                (reviewInvitedTeam) -> teamIds.add(reviewInvitedTeam.getTeamId())
        );
        ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
        reviewTemplateDTO.setTeamIds(new ArrayList<>(teamIds));
        BeanUtils.copyProperties(mapTemplatesInfo.get(templateID), reviewTemplateDTO);

        reviewTemplateDTOs.add(reviewTemplateDTO);
      }

      if (!CollectionUtils.isEmpty(reviewTemplateDTOs)) {
        Collections.sort(reviewTemplateDTOs);
      }
    } catch (Exception e) {
      LOGGER.error("listAllValidTemplatesForActivitiesOfHomepage(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public ReviewTemplateContainUserProfileListDTO listAllValidTemplatesForInvitationsOfHomepage(
          long orgID, long actorUserID) {
    ReviewTemplateContainUserProfileListDTO result = new ReviewTemplateContainUserProfileListDTO();

    ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
            ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg()
    );
    result.setServiceStatusDTO(serviceStatusDTO);
    List<ReviewTemplateContainUserProfileDTO> finalReviewTemplateContainUserProfileDTOs = new ArrayList<>();
    result.setReviewTemplateContainUserProfileDTOs(finalReviewTemplateContainUserProfileDTOs);

    try {
      // Get templates.
      List<ReviewTemplate> reviewTemplates = reviewTemplateService.listAllValidReviewTemplates(orgID);
      if (CollectionUtils.isEmpty(reviewTemplates)) {
        return result;
      }

      HashSet<Long> allValidTemplatesSet = new HashSet<>();
      HashMap<Long, ReviewTemplate> mapTemplatesInfo = new HashMap<>();

      for (ReviewTemplate reviewTemplate: reviewTemplates) {
        long templateID = reviewTemplate.getTemplateId();
        allValidTemplatesSet.add(templateID);
        mapTemplatesInfo.put(templateID, reviewTemplate);
      }

      List<Long> templatesIncludeRevieweeList = reviewActivityService
              .listAllValidReviewActivitiesByRevieweeAndTemplatesList(
                      orgID, new ArrayList<>(allValidTemplatesSet), actorUserID
              );
      if (!CollectionUtils.isEmpty(templatesIncludeRevieweeList)) {
        allValidTemplatesSet.removeAll(templatesIncludeRevieweeList);
      }

      if (CollectionUtils.isEmpty(allValidTemplatesSet)) {
        return result;
      }

      HashSet<Long> templatesExcludeRevieweeSet = allValidTemplatesSet;
      HashMap<Long, HashSet<Long>> mapTemplateToTeams = new HashMap<>();

      for (long templateID: templatesExcludeRevieweeSet) {
        List<ReviewInvitedTeam> invitedTeams = reviewTemplateService.listReviewInvitedTeam(orgID, templateID);
        if (CollectionUtils.isEmpty(invitedTeams)) {
          continue;
        }
        for (ReviewInvitedTeam reviewInvitedTeam: invitedTeams) {
          long teamID = reviewInvitedTeam.getTeamId();

          if (mapTemplateToTeams.containsKey(templateID)) {
            HashSet<Long> teams = mapTemplateToTeams.get(templateID);
            teams.add(teamID);
            mapTemplateToTeams.put(templateID, teams);
          } else {
            HashSet<Long> teams = new HashSet<>();
            teams.add(teamID);
            mapTemplateToTeams.put(templateID, teams);
          }
        }
      }

      if (CollectionUtils.isEmpty(mapTemplateToTeams)) {
        return result;
      }

      List<ReviewInvitation> invitationsByReviewerAndTemplatesList = reviewInvitationService
              .listAllReviewInvitationsByTemplatesAndReviewer(
                      orgID, new ArrayList<>(templatesExcludeRevieweeSet), actorUserID
              );

      if (CollectionUtils.isEmpty(invitationsByReviewerAndTemplatesList)) {
        return result;
      }

      HashMap<Long, HashSet<Long>> mapTemplateToRevieweesByReviewer = new HashMap<>();
      for (ReviewInvitation reviewInvitation: invitationsByReviewerAndTemplatesList) {
        long revieweeID = reviewInvitation.getRevieweeId();
        if (revieweeID == actorUserID) {
          continue;
        }

        long templateID = reviewInvitation.getTemplateId();
        if (reviewInvitation.getIsManager() == 1) {
          if (!isValidTemplate(mapTemplatesInfo.get(templateID), ReviewRoleType.MANAGER)) {
            continue;
          }
        } else {
          if (!isValidTemplate(mapTemplatesInfo.get(templateID), ReviewRoleType.PEER)) {
            continue;
          }
        }

        if (mapTemplateToRevieweesByReviewer.containsKey(templateID)) {
          HashSet<Long> reviewees = mapTemplateToRevieweesByReviewer.get(templateID);
          reviewees.add(revieweeID);
          mapTemplateToRevieweesByReviewer.put(templateID, reviewees);
        } else {
          HashSet<Long> reviewees = new HashSet<>();
          reviewees.add(revieweeID);
          mapTemplateToRevieweesByReviewer.put(templateID, reviewees);
        }
      }

      if (CollectionUtils.isEmpty(mapTemplateToRevieweesByReviewer)) {
        return result;
      }

      Iterator iterator = mapTemplateToRevieweesByReviewer.keySet().iterator();
      while (iterator.hasNext()) {
        long templateID = (long)iterator.next();
        if (!mapTemplateToTeams.containsKey(templateID)) {
          continue;
        }

        ReviewTemplateContainUserProfileDTO reviewTemplateContainUserProfileDTO = new ReviewTemplateContainUserProfileDTO();
        BeanUtils.copyProperties(mapTemplatesInfo.get(templateID), reviewTemplateContainUserProfileDTO);

        reviewTemplateContainUserProfileDTO.setRevieweeIds(
                new ArrayList<>(mapTemplateToRevieweesByReviewer.get(templateID))
        );

        reviewTemplateContainUserProfileDTO.setTeamIds(
                new ArrayList<>(mapTemplateToTeams.get(templateID))
        );

        finalReviewTemplateContainUserProfileDTOs.add(reviewTemplateContainUserProfileDTO);
      }

      if (!CollectionUtils.isEmpty(finalReviewTemplateContainUserProfileDTOs)) {
        Collections.sort(finalReviewTemplateContainUserProfileDTOs);
      }
    } catch (Exception e) {
      LOGGER.error("listAllValidTemplatesForInvitationsOfHomepage(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  public static long getDate(long dateStamp, int period) {
    Date date = new Date(dateStamp);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, period);
    date = calendar.getTime();
    return date.getTime();
  }

  @LogAround
  private void insertReviewObject(
          long selfReviewDeadline,
          long peerReviewDeadline,
          long publicDeadline,
          ReviewObject reviewObject,
          JSONObject jsonTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId) throws Exception {
    // template
    ReviewTemplate reviewTemplate = JSON.parseObject(
            jsonTemplatesInfo.get(REVIEW_TEMPLATE).toString(),
            ReviewTemplate.class
    );
    reviewTemplate.setStartTime(this.getDate(reviewObject.getCurrentTime(), -20));
    reviewTemplate.setEndTime(this.getDate(reviewObject.getCurrentTime(), -10));
    reviewTemplate.setSelfReviewDeadline(selfReviewDeadline);
    reviewTemplate.setPeerReviewDeadline(peerReviewDeadline);
    reviewTemplate.setPublicDeadline(publicDeadline);
    reviewObject.setReviewTemplate(reviewTemplate);
    reviewTemplateService.insertReviewTemplateOnly(reviewObject.getReviewTemplate());

    // activity
    List<ReviewActivity> reviewActivities = JSON.parseArray(
            jsonTemplatesInfo.get(REVIEW_ACTIVITIES).toString(),
            ReviewActivity.class
    );
    if (!CollectionUtils.isEmpty(reviewActivities)) {
      reviewObject.setReviewActivities(reviewActivities);
      for (ReviewActivity reviewActivity : reviewObject.getReviewActivities()) {
        long revieweeid = reviewActivity.getRevieweeId();
        if (mapUserNumberToId.containsKey(revieweeid)) {
          reviewActivity.setRevieweeId(mapUserNumberToId.get(revieweeid));
        }
      }
      reviewActivityService.batchInsertReviewActivities(reviewObject.getReviewActivities());
    }

    // invitation
    List<ReviewInvitation> reviewInvitations = JSON.parseArray(
            jsonTemplatesInfo.get(REVIEW_INVITATIONS).toString(),
            ReviewInvitation.class
    );
    if (!CollectionUtils.isEmpty(reviewInvitations)) {
      reviewObject.setReviewInvitations(reviewInvitations);
      for (ReviewInvitation reviewInvitation : reviewObject.getReviewInvitations()) {
        long revieweeId = reviewInvitation.getRevieweeId();
        if (mapUserNumberToId.containsKey(revieweeId)) {
          reviewInvitation.setRevieweeId(mapUserNumberToId.get(revieweeId));
        }
        long reviewerId = reviewInvitation.getReviewerId();
        if (mapUserNumberToId.containsKey(reviewerId)) {
          reviewInvitation.setReviewerId(mapUserNumberToId.get(reviewerId));
        }
      }
      reviewInvitationService.batchInsertReviewInvitations(reviewObject.getReviewInvitations());
    }

    // question
    HashMap<Long, Long> mapQuestionNumberToId = new HashMap<>();
    List<ReviewQuestion> reviewQuestions = JSON.parseArray(
            jsonTemplatesInfo.get(REVIEW_QUESTIONS).toString(),
            ReviewQuestion.class
    );
    if (!CollectionUtils.isEmpty(reviewQuestions)) {
      reviewObject.setReviewQuestions(reviewQuestions);
      for (ReviewQuestion reviewQuestion : reviewObject.getReviewQuestions()) {
        long questionNumber = reviewQuestion.getQuestionId();
        reviewQuestionService.insertReviewQuestion(reviewQuestion);
        mapQuestionNumberToId.put(questionNumber, reviewQuestion.getQuestionId());
      }
    }

    // comment
    List<ReviewComment> reviewComments = JSON.parseArray(
            jsonTemplatesInfo.get(REVIEW_COMMENTS).toString(),
            ReviewComment.class
    );
    if (!CollectionUtils.isEmpty(reviewComments)) {
      reviewObject.setReviewComments(reviewComments);
      for (ReviewComment reviewComment : reviewObject.getReviewComments()) {
        long itemId = reviewComment.getItemId();
        if (mapQuestionNumberToId.containsKey(itemId)) {
          reviewComment.setItemId(mapQuestionNumberToId.get(itemId));
        }
        long revieweeId = reviewComment.getRevieweeId();
        if (mapUserNumberToId.containsKey(revieweeId)) {
          reviewComment.setRevieweeId(mapUserNumberToId.get(revieweeId));
        }
        long reviewerId = reviewComment.getReviewerId();
        if (mapUserNumberToId.containsKey(reviewerId)) {
          reviewComment.setReviewerId(mapUserNumberToId.get(reviewerId));
        }
      }
      reviewCommentService.batchInsertReviewComments(reviewObject.getReviewComments());
    }

    // project
    List<ReviewProject> reviewProjects = JSON.parseArray(
            jsonTemplatesInfo.get(REVIEW_PROJECTS).toString(),
            ReviewProject.class
    );
    if (!CollectionUtils.isEmpty(reviewProjects)) {
      reviewObject.setReviewProjects(reviewProjects);
      for (ReviewProject reviewProject : reviewObject.getReviewProjects()) {
        long revieweeId = reviewProject.getRevieweeId();
        if (mapUserNumberToId.containsKey(revieweeId)) {
          reviewProject.setRevieweeId(mapUserNumberToId.get(revieweeId));
        }
      }
      reviewProjectService.batchInsertReviewProjects(reviewObject.getReviewProjects());
    }

    // invited-team
    List<ReviewInvitedTeam> reviewInvitedTeams = JSON.parseArray(
            jsonTemplatesInfo.get(REVIEW_INVITED_TEAMS).toString(),
            ReviewInvitedTeam.class
    );
    if (!CollectionUtils.isEmpty(reviewInvitedTeams)) {
      reviewObject.setReviewInvitedTeams(reviewInvitedTeams);
      for (ReviewInvitedTeam reviewInvitedTeam : reviewObject.getReviewInvitedTeams()) {
        long teamId = reviewInvitedTeam.getTeamId();
        if (mapTeamNumberToId.containsKey(teamId)) {
          reviewInvitedTeam.setTeamId(mapTeamNumberToId.get(teamId));
        }
      }
      reviewTemplateService.batchInsertReviewInvitedTeam(reviewObject.getReviewInvitedTeams());
    }
  }

  @LogAround
  private void insertSelfReviewObject(
          ReviewObject selfReviewObject,
          JSONObject jsonSelfTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId) throws Exception {
    this.insertReviewObject(
            this.getDate(selfReviewObject.getCurrentTime(), 5),
            this.getDate(selfReviewObject.getCurrentTime(), 10),
            this.getDate(selfReviewObject.getCurrentTime(), 15),
            selfReviewObject,
            jsonSelfTemplatesInfo,
            mapUserNumberToId,
            mapTeamNumberToId
    );
  }

  @LogAround
  private void insertPeerReviewObject(
          ReviewObject peerReviewObject,
          JSONObject jsonPeerTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId) throws Exception {
    this.insertReviewObject(
            this.getDate(peerReviewObject.getCurrentTime(), -1),
            this.getDate(peerReviewObject.getCurrentTime(), 5),
            this.getDate(peerReviewObject.getCurrentTime(), 10),
            peerReviewObject,
            jsonPeerTemplatesInfo,
            mapUserNumberToId,
            mapTeamNumberToId
    );
  }

  @LogAround
  private void insertManagerReviewObject(
          ReviewObject managerReviewObject,
          JSONObject jsonManagerTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId) throws Exception {
    this.insertReviewObject(
            this.getDate(managerReviewObject.getCurrentTime(), -5),
            this.getDate(managerReviewObject.getCurrentTime(), -1),
            this.getDate(managerReviewObject.getCurrentTime(), 5),
            managerReviewObject,
            jsonManagerTemplatesInfo,
            mapUserNumberToId,
            mapTeamNumberToId
    );
  }

  @LogAround
  private void insertPublicReviewObject(
          ReviewObject publicReviewObject,
          JSONObject jsonPublicTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId) throws Exception {
    this.insertReviewObject(
            this.getDate(publicReviewObject.getCurrentTime(), -5),
            this.getDate(publicReviewObject.getCurrentTime(), -3),
            this.getDate(publicReviewObject.getCurrentTime(), -1),
            publicReviewObject,
            jsonPublicTemplatesInfo,
            mapUserNumberToId,
            mapTeamNumberToId
    );
  }

  @LogAround
  @Override
  public VoidDTO initReviewGuide(
          long orgId,
          long userId,
          String stringJsonTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      JSONObject jsonTemplatesInfo = JSON.parseObject(stringJsonTemplatesInfo);
      if (jsonTemplatesInfo != null && !jsonTemplatesInfo.isEmpty()) {

        ReviewObject publicReviewObject = new ReviewObject(orgId, userId);
        this.insertPublicReviewObject(
                publicReviewObject,
                (JSONObject) jsonTemplatesInfo.get(PUBLIC_DAY),
                mapUserNumberToId,
                mapTeamNumberToId
        );

        ReviewObject managerReviewObject = new ReviewObject(orgId, userId);
        this.insertManagerReviewObject(
                managerReviewObject,
                (JSONObject) jsonTemplatesInfo.get(MANAGER_EVALUATE),
                mapUserNumberToId,
                mapTeamNumberToId
        );

        ReviewObject peerReviewObject = new ReviewObject(orgId, userId);
        this.insertPeerReviewObject(
                peerReviewObject,
                (JSONObject) jsonTemplatesInfo.get(PEER_EVALUATE),
                mapUserNumberToId,
                mapTeamNumberToId
        );

        ReviewObject selfReviewObject = new ReviewObject(orgId, userId);
        this.insertSelfReviewObject(
                selfReviewObject,
                (JSONObject) jsonTemplatesInfo.get(SELF_EVALUATE),
                mapUserNumberToId,
                mapTeamNumberToId
        );
      }
    } catch (Exception e) {
      LOGGER.error("initReviewGuide()-error:  ", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }
}
