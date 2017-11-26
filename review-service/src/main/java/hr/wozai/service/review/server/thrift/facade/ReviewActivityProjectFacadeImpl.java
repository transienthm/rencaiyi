// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import com.amazonaws.services.logs.model.LogGroup;

import hr.wozai.service.review.client.helper.ReviewActivityHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.ReviewCommentDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.facade.ReviewActivityProjectFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewProjectService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.utils.ReviewUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@Service("reviewActivityProjectFacadeImpl")
public class ReviewActivityProjectFacadeImpl implements ReviewActivityProjectFacade {

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewActivityService reviewActivityService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  @Autowired
  private ReviewUtils reviewUtils;


  @LogAround
  @Override
  public LongDTO insertProject(long orgId, long activityId,
                               ReviewProjectDTO reviewProjectDTO,
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

      // # template status check
      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      boolean status = false;
      // not submitted and before 'peer' review deadline
      if (!isSubmitted && !isPeerReviewDeadline) {
        status = true;
      }
      // submitted and before 'self' review deadline
      else if (isSubmitted && !isSelfReviewDeadline) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # insert project
      ReviewProject reviewProject = new ReviewProject();
      BeanUtils.copyProperties(reviewProjectDTO, reviewProject);
      reviewProject.setOrgId(orgId);
      reviewProject.setTemplateId(templateId);
      reviewProject.setRevieweeId(actorUserId);
      reviewProject.setLastModifiedUserId(actorUserId);

      long projectId = reviewProjectService.insertReviewProject(reviewProject);
      result.setData(projectId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public VoidDTO deleteProject(long orgId, long activityId, long projectId,
                               long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check
      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      boolean status = false;
      // not submitted and before 'peer' review deadline
      if (!isSubmitted && !isPeerReviewDeadline) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # delete project
      reviewProjectService.deleteReviewProject(orgId, projectId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @LogAround
  @Override
  public VoidDTO updateProject(long orgId, long activityId,
                               ReviewProjectDTO reviewProjectDTO,
                               long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check
      long templateId = reviewActivity.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      boolean status = false;
      if (!isSubmitted && !isPeerReviewDeadline) {
        status = true;
      } else if (isSubmitted && !isSelfReviewDeadline) {
        status = true;
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # update project
      ReviewProject reviewProject = new ReviewProject();
      BeanUtils.copyProperties(reviewProjectDTO, reviewProject);
      reviewProject.setOrgId(orgId);
      reviewProject.setTemplateId(templateId);
      reviewProject.setRevieweeId(actorUserId);
      reviewProject.setLastModifiedUserId(actorUserId);

      reviewProjectService.updateReviewProject(reviewProject);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public ReviewProjectDetailDTO getActivityProjectDetail(long orgId, long activityId, long projectId,
                                                         long actorUserId, long adminUserId) {

    ReviewProjectDetailDTO result = new ReviewProjectDetailDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      if (actorUserId != reviewActivity.getRevieweeId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewProject reviewProject = reviewProjectService.findReviewProject(orgId, projectId);

      long templateId = reviewActivity.getTemplateId();

      // 1. find project basic information
      BeanUtils.copyProperties(reviewProject, result);

      // 2. Only when template is finished, add submitted comments
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (reviewTemplate.getState() == ReviewTemplateStatus.FINISH.getCode()
          || reviewTemplate.getState() == ReviewTemplateStatus.IN_PROGRESS.getCode()) {
        List<Long> reviewerIds = reviewUtils.getSubmittedReviewerIds(orgId, templateId, actorUserId);
        List<ReviewCommentDTO> reviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
            ReviewItemType.PROJECT.getCode(), projectId, actorUserId, reviewerIds);
        result.setSubmittedComments(reviewCommentDTOs);
      }

      // 3. set editable status
      // # 状态的设置规则可能参考 ReviewActivityDetailFacadeImpl 中 ActivityDetail 的注释
      // # 即 project 的删除, 修改状态
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;
      boolean isSelfReviewDeadline = reviewUtils.isSelfReviewDeadline(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);

      int isEditable = 0;
      int isDeletable = 0;
      if (!isSubmitted && !isPeerReviewDeadline) {
        isEditable = 1;
        isDeletable = 1;
      } else if (isSubmitted && !isSelfReviewDeadline) {
        isEditable = 1;
      }
      result.setIsEditable(isEditable);
      result.setIsDeletable(isDeletable);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public ReviewProjectDetailDTO getActivityProjectDetailByHR(long orgId, long activityId, long projectId,
                                                             long actorUserId, long adminUserId) {

    ReviewProjectDetailDTO result = new ReviewProjectDetailDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
      long revieweeId = reviewActivity.getRevieweeId();

      ReviewProject reviewProject = reviewProjectService.findReviewProject(orgId, projectId);

      long templateId = reviewActivity.getTemplateId();

      // 1. find project basic information
      BeanUtils.copyProperties(reviewProject, result);

      List<Long> reviewerIds = reviewUtils.getSubmittedReviewerIds(orgId, templateId, revieweeId);

      List<ReviewCommentDTO> reviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
              ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);

      result.setSubmittedComments(reviewCommentDTOs);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

}
