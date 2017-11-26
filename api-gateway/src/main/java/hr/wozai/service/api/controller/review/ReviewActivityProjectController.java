// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ReviewHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.review.ReviewCommentVO;
import hr.wozai.service.api.vo.review.ReviewInputProjectVO;
import hr.wozai.service.api.vo.review.ReviewProjectVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewProjectControllerNew")
public class ReviewActivityProjectController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewActivityProjectController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;

  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/projects",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> insertProject(
      @PathVariable(value = "activityId") String encryptedActivityId,
      @RequestBody @Valid ReviewInputProjectVO reviewInputProjectVO
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));

    ReviewProjectDTO reviewProjectDTO = new ReviewProjectDTO();
    BeanUtils.copyProperties(reviewInputProjectVO, reviewProjectDTO);

    LongDTO remoteResult = facadeFactory.getReviewActivityProjectFacade().insertProject(orgId, activityId, reviewProjectDTO,
        actorUserId, adminUserId);

    if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    IdVO data = new IdVO();
    data.setIdValue(remoteResult.getData());

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(data);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/projects/{projectId}",
      method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteProject(
      @PathVariable(value = "activityId") String encryptedActivityId,
      @PathVariable(value = "projectId") String encryptedProjectId
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));
    long projectId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedProjectId));

    VoidDTO remoteResult = facadeFactory.getReviewActivityProjectFacade().deleteProject(orgId, activityId, projectId,
        actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    return result;
  }


  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/projects/{projectId}",
      method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> updateProject(
      @PathVariable(value = "activityId") String encryptedActivityId,
      @PathVariable(value = "projectId") String encryptedProjectId,
      @RequestBody @Valid ReviewInputProjectVO reviewInputProjectVO,
      BindingResult bindingResult
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));
    long projectId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedProjectId));

    ReviewProjectDTO reviewProjectDTO = new ReviewProjectDTO();
    BeanUtils.copyProperties(reviewInputProjectVO, reviewProjectDTO);
    reviewProjectDTO.setProjectId(projectId);

    VoidDTO remoteResult = facadeFactory.getReviewActivityProjectFacade().updateProject(orgId, activityId, reviewProjectDTO,
            actorUserId, adminUserId);
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    return result;
  }


  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/projects/{projectId}",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getActivityProjectDetail(
      @PathVariable(value = "activityId") String encryptedActivityId,
      @PathVariable(value = "projectId") String encryptedProjectId
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));
    long projectId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedProjectId));

    ReviewProjectDetailDTO reviewProjectDetailDTO = facadeFactory.getReviewActivityProjectFacade().getActivityProjectDetail(orgId,
            activityId, projectId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewProjectDetailDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewProjectDetailDTO.getServiceStatusDTO().getCode());
    }

    ReviewProjectVO reviewProjectVO = new ReviewProjectVO();

    BeanUtils.copyProperties(reviewProjectDetailDTO, reviewProjectVO);

    List<ReviewCommentDTO> submittedCommentDTOs = reviewProjectDetailDTO.getSubmittedComments();
    if(null == submittedCommentDTOs) {
      submittedCommentDTOs = Collections.EMPTY_LIST;
    }
    List<ReviewCommentVO> submittedCommentVOs = new ArrayList<>();
    for(ReviewCommentDTO reviewCommentDTO: submittedCommentDTOs) {
      ReviewCommentVO reviewCommentVO = reviewUtils.getReviewCommentVO(orgId, reviewCommentDTO, actorUserId, adminUserId);
      submittedCommentVOs.add(reviewCommentVO);
    }
    reviewProjectVO.setSubmittedComments(submittedCommentVOs);

    // make review anonymous if required
    ReviewTemplateDTO reviewTemplateDTO = facadeFactory.getReviewTemplateFacade()
        .findReviewTemplate(orgId, reviewProjectDetailDTO.getTemplateId(), actorUserId, adminUserId);
    int isReviewerAnonymous = reviewTemplateDTO.getIsReviewerAnonymous();
    if (1 == isReviewerAnonymous) {
      RoleListDTO roleListDTO = facadeFactory.getSecurityModelFacade()
              .getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != roleListDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(roleListDTO.getServiceStatusDTO().getCode());
      }
      boolean isHr = false;
      for (RoleDTO roleDTO: roleListDTO.getRoleDTOList()) {
        if (roleDTO.getRoleName().equals(DefaultRole.HR.getName())) {
          isHr = true;
          break;
        }
      }
      if (!isHr) {
        if (!CollectionUtils.isEmpty(reviewProjectVO.getSubmittedComments())) {
          for (ReviewCommentVO reviewCommentVO: reviewProjectVO.getSubmittedComments()) {
            ReviewHelper.makeReviewCommentVOAnonymous(reviewCommentVO);
          }
        }
      }
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(reviewProjectDetailDTO.getServiceStatusDTO().getCode()));
    result.setData(reviewProjectVO);

    return result;
  }


  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/overview/projects/{projectId}",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getActivityProjectOverview(
          @PathVariable(value = "activityId") String encryptedActivityId,
          @PathVariable(value = "projectId") String encryptedProjectId
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
            ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.CREATE.getCode());
    if(!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));
    long projectId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedProjectId));

    ReviewProjectDetailDTO reviewProjectDetailDTO = facadeFactory.getReviewActivityProjectFacade().getActivityProjectDetailByHR(orgId,
            activityId, projectId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewProjectDetailDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewProjectDetailDTO.getServiceStatusDTO().getCode());
    }

    ReviewProjectVO reviewProjectVO = new ReviewProjectVO();

    BeanUtils.copyProperties(reviewProjectDetailDTO, reviewProjectVO);

    List<ReviewCommentDTO> submittedCommentDTOs = reviewProjectDetailDTO.getSubmittedComments();
    if(null == submittedCommentDTOs) {
      submittedCommentDTOs = Collections.EMPTY_LIST;
    }
    List<ReviewCommentVO> submittedCommentVOs = new ArrayList<>();
    for(ReviewCommentDTO reviewCommentDTO: submittedCommentDTOs) {
      ReviewCommentVO reviewCommentVO = reviewUtils.getReviewCommentVO(orgId, reviewCommentDTO, actorUserId, adminUserId);
      submittedCommentVOs.add(reviewCommentVO);
    }
    reviewProjectVO.setSubmittedComments(submittedCommentVOs);

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(reviewProjectDetailDTO.getServiceStatusDTO().getCode()));
    result.setData(reviewProjectVO);

    return result;
  }

}
