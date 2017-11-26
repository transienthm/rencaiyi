// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.review.client.enums.ReviewRoleType;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import javax.validation.Valid;

import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.review.ReviewActivityUserVO;
import hr.wozai.service.api.vo.review.ReviewActivityVO;
import hr.wozai.service.api.vo.review.ReviewInvitationVO;
import hr.wozai.service.api.vo.review.ReviewTemplateListVO;
import hr.wozai.service.api.vo.review.ReviewTemplateVO;
import hr.wozai.service.api.vo.review.ReviewTemplateContainRevieweeProfilesVO;
import hr.wozai.service.api.vo.review.ReviewTemplateContainRevieweeProfilesListVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.helper.ReviewActivityHelper;
import hr.wozai.service.review.client.helper.ReviewInvitationHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.IdListDTO;
import hr.wozai.service.user.client.userorg.dto.ReportLineListDTO;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewTemplateController")
public class ReviewTemplateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateController.class);

  @Autowired
  private FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;

  /**
   * Steps:
   *  1) insert review template
   *  2) insert invitations
   *
   * @param reviewTemplateVO
   * @param bindingResult
   * @return
   * @throws Exception
   */
  @LogAround

  @RequestMapping(value = "/reviews/templates", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> insertTemplate(
      @RequestBody @Valid ReviewTemplateVO reviewTemplateVO,
      BindingResult bindingResult
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
            ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.CREATE.getCode());
    if (!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    List<Long> teamIds = new ArrayList<>();
    for (String encryptedTeamId: reviewTemplateVO.getEncryptedTeamIds()) {
      teamIds.add(Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTeamId)));
    }

    ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
    BeanUtils.copyProperties(reviewTemplateVO, reviewTemplateDTO);
    reviewTemplateDTO.setOrgId(orgId);
    reviewTemplateDTO.setTeamIds(teamIds);
    reviewTemplateDTO.setState(ReviewTemplateStatus.IN_PROGRESS.getCode());
    reviewTemplateDTO.setLastModifiedUserId(actorUserId);

    // 1)
    LongDTO remoteResult =
        facadeFactory.getReviewTemplateFacade().insertReviewTemplate(orgId, reviewTemplateDTO, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    IdVO data = new IdVO();
    data.setIdValue(remoteResult.getData());
    result.setData(data);

    // 2)
    IdListDTO userIdListDTO = facadeFactory.getUserFacade()
                .listUnResignedAndHasReportorTeamMembersForReview(orgId, teamIds, actorUserId, adminUserId);
    ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(userIdListDTO.getServiceStatusDTO().getCode());
    if (!ServiceStatus.COMMON_OK.equals(rpcStatus)) {
      throw new ServiceStatusException(rpcStatus);
    }
    ReportLineListDTO reportLineListDTO = facadeFactory.getUserFacade()
            .listReportLineByUserIds(orgId, userIdListDTO.getIdList(), actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reportLineListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reportLineListDTO.getServiceStatusDTO().getCode());
    }

    VoidDTO batchInsertResult = facadeFactory.getReviewActivityFacade().batchInsertReviewActivities(
        orgId, remoteResult.getData(), reportLineListDTO.getReportLineDTOList(), actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != batchInsertResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(batchInsertResult.getServiceStatusDTO().getCode());
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));
    return result;
  }


//  @LogAround
//
//  @RequestMapping(
//      value = "/reviews/templates/{templateId}/publish",
//      method = RequestMethod.POST, produces = "application/json")
//  @ResponseBody
//  public Result<Object> publishTemplate(
//      @PathVariable(value = "templateId") String encryptedTemplateId
//  ) throws Exception {
//
//    Result<Object> result = new Result<>();
//
//    long actorUserId = AuthenticationInterceptor.actorUserId.get();
//    long adminUserId = AuthenticationInterceptor.adminUserId.get();
//    long orgId = AuthenticationInterceptor.orgId.get();
//
//    long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));
//
//    boolean isPermitted = reviewUtils.isPermitted(
//        orgId, actorUserId, ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.CREATE.getCode());
//    if(!isPermitted) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    // 1. update activity
//    VoidDTO remoteResult = facadeFactory.getReviewTemplateFacade()
//        .publishReviewTemplate(orgId, templateId, actorUserId, actorUserId, adminUserId);
//    if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
//      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
//    }
//
//    // 2. add activity for all members
//    IdListDTO idListDTO = facadeFactory.getUserFacade()
//        .listUsersWhoHasReportorByOrgId(orgId, actorUserId, adminUserId);
//    if(ServiceStatus.COMMON_OK.getCode() != idListDTO.getServiceStatusDTO().getCode()) {
//      throw new ServiceStatusException(idListDTO.getServiceStatusDTO().getCode());
//    }
//    List<Long> orgUsers = idListDTO.getIdList();
//
//    ReportLineListDTO reportLineListDTO = facadeFactory.getUserFacade()
//        .listReportLineByUserIds(orgId, orgUsers, actorUserId, adminUserId);
//    if (ServiceStatus.COMMON_OK.getCode() != reportLineListDTO.getServiceStatusDTO().getCode()) {
//      throw new ServiceStatusException(reportLineListDTO.getServiceStatusDTO().getCode());
//    }
//
//    VoidDTO batchInsertResult = facadeFactory.getReviewActivityFacade().batchInsertReviewActivities(
//        orgId, templateId, reportLineListDTO.getReportLineDTOList(), actorUserId, adminUserId);
//    if(ServiceStatus.COMMON_OK.getCode() != batchInsertResult.getServiceStatusDTO().getCode()) {
//      throw new ServiceStatusException(batchInsertResult.getServiceStatusDTO().getCode());
//    }
//
//    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
//
//    return result;
//  }


  @LogAround

  @RequestMapping(value = "/reviews/templates/{templateId}", method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> cancelTemplate(
      @PathVariable(value = "templateId") String encryptedTemplateId
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();
      long orgId = AuthenticationInterceptor.orgId.get();

      boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
          ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.DELETE.getCode());
      if(!isPermitted) {
        throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
      }

      long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));

      // cancel review template
      VoidDTO remoteResult = facadeFactory.getReviewTemplateFacade().cancelReviewTemplate(
              orgId, templateId, actorUserId, actorUserId, adminUserId);
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    } catch (Exception e) {
      LOGGER.error("cancelTemplate()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


//  @LogAround
//
//  @BindingResultMonitor
//  @RequestMapping(value = "/reviews/templates/{templateId}", method = RequestMethod.PUT, produces = "application/json")
//  @ResponseBody
//  public Result<Object> updateTemplate(
//      @PathVariable(value = "templateId") String encryptedTemplateId,
//      @RequestBody @Valid ReviewTemplateVO reviewTemplateVO,
//      BindingResult bindingResult
//  ) throws Exception {
//
//    Result<Object> result = new Result<>();
//
//    long actorUserId = AuthenticationInterceptor.actorUserId.get();
//    long adminUserId = AuthenticationInterceptor.adminUserId.get();
//    long orgId = AuthenticationInterceptor.orgId.get();
//
//    long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));
//
//    boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
//        ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.EDIT.getCode());
//    if(!isPermitted) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
//    BeanUtils.copyProperties(reviewTemplateVO, reviewTemplateDTO);
//
//    reviewTemplateDTO.setOrgId(orgId);
//    reviewTemplateDTO.setTemplateId(templateId);
//    reviewTemplateDTO.setState(ReviewTemplateStatus.DRAFT.getCode());
//    reviewTemplateDTO.setLastModifiedUserId(actorUserId);
//
//    VoidDTO remoteResult = facadeFactory.getReviewTemplateFacade()
//        .updateReviewTemplate(orgId, reviewTemplateDTO, actorUserId, adminUserId);
//    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
//
//    return result;
//  }


  @LogAround

  @RequestMapping(value = "/reviews/templates", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listTemplate(
      @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
      @RequestParam(value = "statuses", required = false, defaultValue = "1,2,3,4") String statusesStr,
      @RequestParam(value = "type", required = false, defaultValue = "1") Integer templateListType
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    /*boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
        ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.READ.getCode());
    if(!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }*/

    if (StringUtils.isNullOrEmpty(statusesStr)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    List<Integer> statuses = new ArrayList<>();
    List<String> statusesList = Arrays.asList(statusesStr.split(","));
    for (String statusStr: statusesList) {
      try {
        statuses.add(Integer.parseInt(statusStr.trim()));
      } catch (Exception e) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
    }

    // handle invited teams
    TeamListDTO teamListDTO = facadeFactory.getUserFacade().listAllTeams(orgId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != teamListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(teamListDTO.getServiceStatusDTO().getCode());
    }
    Map<Long, TeamVO> teamDTOMap = new HashMap<>();
    for (TeamDTO teamDTO: teamListDTO.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      teamDTOMap.put(teamVO.getTeamId(), teamVO);
    }

    // handle role
    boolean isHr = false;
    RoleListDTO roleListDTO = facadeFactory.getSecurityModelFacade()
            .getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
    ServiceStatus roleListDTOStatus = ServiceStatus.getEnumByCode(roleListDTO.getServiceStatusDTO().getCode());
    if (!ServiceStatus.COMMON_OK.equals(roleListDTOStatus)) {
      throw new ServiceStatusException(roleListDTOStatus);
    }
    for (RoleDTO roleDTO: roleListDTO.getRoleDTOList()) {
      if (roleDTO.getRoleName().equals(DefaultRole.HR.getName())) {
        isHr = true;
        break;
      }
    }

    ReviewTemplateListDTO remoteResult = facadeFactory.getReviewTemplateFacade()
        .listReviewTemplate(orgId, pageNumber, pageSize, statuses, templateListType, isHr, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    List<ReviewTemplateDTO> reviewTemplateDTOs = remoteResult.getReviewTemplateDTOs();
    List<ReviewTemplateVO> reviewTemplateVOs = new ArrayList();

    for(ReviewTemplateDTO reviewTemplateDTO: reviewTemplateDTOs) {

      ReviewTemplateVO reviewTemplateVO = new ReviewTemplateVO();
      BeanUtils.copyProperties(reviewTemplateDTO, reviewTemplateVO);
      List<TeamVO> teamVOs = new ArrayList<>();
      for (Long teamId: reviewTemplateDTO.getTeamIds()) {
        teamVOs.add(teamDTOMap.get(teamId));
      }
      reviewTemplateVO.setInvitedTeamVOs(teamVOs);
      reviewTemplateVO.setQuestions(reviewTemplateDTO.getQuestions());

      reviewTemplateVOs.add(reviewTemplateVO);
    }


//    LongDTO amount = facadeFactory.getReviewTemplateFacade().countReviewTemplate(orgId);
//    if (ServiceStatus.COMMON_OK.getCode() != amount.getServiceStatusDTO().getCode()) {
//      throw new ServiceStatusException(amount.getServiceStatusDTO().getCode());
//    }

    ReviewTemplateListVO reviewTemplateListVO = new ReviewTemplateListVO();
    reviewTemplateListVO.setReviewTemplates(reviewTemplateVOs);
//    reviewTemplateListVO.setAmount(amount.getData());

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));
    result.setData(reviewTemplateListVO);
    
    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/templates/{templateId}/report", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getTemplateReport(
          @PathVariable(value = "templateId") String encryptedTemplateId
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));

    boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
            ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.READ.getCode());
    if(!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    ReviewReportDTO remoteResult = facadeFactory.getReviewTemplateFacade()
            .getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));
    result.setData(remoteResult);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/templates/{templateId}/all-activities", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getTemplateActivities(
          @PathVariable(value = "templateId") String encryptedTemplateId,
          @RequestParam(value = "orderBy", required = false) String requestOrderBy
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));

    boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
            ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.READ.getCode());
    if(!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    String orderBy;
    String orderDirection;

    if(null == requestOrderBy) {
      requestOrderBy = "name";
    }

    switch (requestOrderBy) {
      case "name":
        orderBy = "name";
        orderDirection = "ASC";
        break;
      case "-name":
        orderBy = "name";
        orderDirection = "DESC";
        break;
      case "score":
        orderBy = "score";
        orderDirection = "ASC";
        break;
      case "-score":
        orderBy = "score";
        orderDirection = "DESC";
        break;
      default:
        orderBy = "name";
        orderDirection = "ASC";
        break;
    }

    ReviewActivityUserListDTO remoteResult = facadeFactory.getReviewTemplateFacade()
            .getActivitiesOfTemplate(orgId, templateId, orderBy, orderDirection, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    List<ReviewActivityUserVO> reviewActivityUserVOs = new ArrayList<>();
    List<ReviewActivityUserDTO> reviewActivityUserDTOs = remoteResult.getActivityUserDTOs();

    Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = reviewUtils.getCoreUserProfileMapFromReviewActivityUserDTOList(
            orgId, reviewActivityUserDTOs, actorUserId, adminUserId);

    setReviewActivityUserVOs(reviewActivityUserVOs, reviewActivityUserDTOs, coreUserProfileDTOMap);

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));
    result.setData(reviewActivityUserVOs);

    return result;
  }

  @LogAround

  private void setReviewActivityUserVOs(List<ReviewActivityUserVO> reviewActivityUserVOs,
                                        List<ReviewActivityUserDTO> reviewActivityUserDTOs,
                                        Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap) {
    for(ReviewActivityUserDTO reviewActivityUserDTO: reviewActivityUserDTOs) {
      ReviewActivityUserVO reviewActivityUserVO = new ReviewActivityUserVO();


      ReviewActivityDTO reviewActivityDTO = reviewActivityUserDTO.getActivityDTO();
      ReviewActivityVO reviewActivityVO = new ReviewActivityVO();
      reviewActivityVO.setActivityId(reviewActivityDTO.getActivityId());
      reviewActivityVO.setIsSubmitted(reviewActivityDTO.getIsSubmitted());
      Integer activityDisplayType = ReviewActivityHelper.getActivityDisplayType(
          reviewActivityUserDTO.getActivityDTO().getIsSubmitted(),
          reviewActivityUserDTO.getActivityDTO().getSelfReviewDeadline(),
          reviewActivityUserDTO.getActivityDTO().getPeerReviewDeadline());
      reviewActivityVO.setActivityDisplayType(activityDisplayType);
      if (coreUserProfileDTOMap.containsKey(reviewActivityDTO.getRevieweeId())) {
        CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
        BeanUtils.copyProperties(coreUserProfileDTOMap.get(reviewActivityDTO.getRevieweeId()), coreUserProfileVO);
        reviewActivityVO.setRevieweeUserProfile(coreUserProfileVO);
        reviewActivityUserVO.setActivity(reviewActivityVO);
      } else {
        continue;
      }

      ReviewInvitationDTO managerInvitationDTO = reviewActivityUserDTO.getManagerInvitationDTO();
      if(managerInvitationDTO != null) {
        ReviewInvitationVO managerInvitationVO = getReviewInvitationVO(managerInvitationDTO,
                coreUserProfileDTOMap);
        reviewActivityUserVO.setManagerInvitation(managerInvitationVO);
      }

      List<ReviewInvitationVO> staffInvitationVOs = new ArrayList<>();
      List<ReviewInvitationDTO> staffInvitationDTOs = reviewActivityUserDTO.getStaffInvitationDTOs();
      if(staffInvitationDTOs != null) {
        for (ReviewInvitationDTO reviewInvitationDTO : staffInvitationDTOs) {
          ReviewInvitationVO reviewInvitationVO = getReviewInvitationVO(reviewInvitationDTO,
                  coreUserProfileDTOMap);
          staffInvitationVOs.add(reviewInvitationVO);
        }
        reviewActivityUserVO.setStaffInvitations(staffInvitationVOs);
      }

      reviewActivityUserVOs.add(reviewActivityUserVO);
    }
  }

  private ReviewInvitationVO getReviewInvitationVO(ReviewInvitationDTO reviewInvitationDTO,
                                                   Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap) {

    ReviewInvitationVO reviewInvitationVO = new ReviewInvitationVO();

    reviewInvitationVO.setIsSubmitted(reviewInvitationDTO.getIsSubmitted());
    reviewInvitationVO.setIsCanceled(reviewInvitationDTO.getIsCanceled());
    CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
    coreUserProfileVO.setUserId(reviewInvitationDTO.getReviewerId());
    if (coreUserProfileDTOMap.containsKey(reviewInvitationDTO.getReviewerId())) {
      BeanUtils.copyProperties(coreUserProfileDTOMap.get(reviewInvitationDTO.getReviewerId()), coreUserProfileVO);
    }
    reviewInvitationVO.setRevieweeUserProfile(coreUserProfileVO);

    if(reviewInvitationDTO.getIsManager() == 1) {
      reviewInvitationVO.setScore(reviewInvitationDTO.getScore());
    }

    Integer invitationDisplayType = ReviewInvitationHelper.getInvitationDisplayType(
        reviewInvitationDTO.getIsInActive(), reviewInvitationDTO.getIsManager(), reviewInvitationDTO.getIsSubmitted(),
        reviewInvitationDTO.getIsCanceled(), reviewInvitationDTO.getSelfReviewDeadline(),
        reviewInvitationDTO.getPeerReviewDeadline(), reviewInvitationDTO.getPublicDeadline());
    reviewInvitationVO.setInvitationDisplayType(invitationDisplayType);

    return reviewInvitationVO;
  }

//*********************************************************************************************************************

  @LogAround

  @RequestMapping(value = "/reviews/templates/activities-for-homepage", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<ReviewTemplateListVO> listActivities() throws Exception {
    // 定义最终返回值
    Result<ReviewTemplateListVO> result = new Result<>();
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));

    ReviewTemplateListVO reviewTemplateListVO = new ReviewTemplateListVO();

    List<ReviewTemplateVO> finalReviewTemplateVOs = new ArrayList<>();
    reviewTemplateListVO.setReviewTemplates(finalReviewTemplateVOs);
    result.setData(reviewTemplateListVO);

    long orgID = AuthenticationInterceptor.orgId.get();
    long actorUserID = AuthenticationInterceptor.actorUserId.get();

    ReviewTemplateListDTO reviewTemplateListDTO = facadeFactory
            .getReviewTemplateFacade().listAllValidTemplatesForActivitiesOfHomepage(orgID, actorUserID);

    if (ServiceStatus.COMMON_OK.getCode() != reviewTemplateListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewTemplateListDTO.getServiceStatusDTO().getCode());
    }

    List<ReviewTemplateDTO> reviewTemplateDTOs = reviewTemplateListDTO.getReviewTemplateDTOs();
    if (CollectionUtils.isEmpty(reviewTemplateDTOs)) {
      return result;
    }

    HashSet<Long> allTeamIds = new HashSet<>();
    for (ReviewTemplateDTO reviewTemplateDTO : reviewTemplateDTOs) {
      List<Long> teamIds = reviewTemplateDTO.getTeamIds();
      if (CollectionUtils.isEmpty(teamIds)) {
        continue;
      }
      allTeamIds.addAll(teamIds);
    }

    if (CollectionUtils.isEmpty(allTeamIds)) {
      return result;
    }

    TeamListDTO userAndTeamListDTO = facadeFactory.getUserFacade().fetchTeamAndUserProfiles(
            orgID, new ArrayList<>(allTeamIds), new ArrayList<>()
    );

    if (ServiceStatus.COMMON_OK.getCode() != userAndTeamListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(userAndTeamListDTO.getServiceStatusDTO().getCode());
    }

    List<TeamDTO> teamDTOList = userAndTeamListDTO.getTeamDTOList();
    if (CollectionUtils.isEmpty(teamDTOList)) {
      return result;
    }

    HashMap<Long, TeamDTO> mapTeamIdToProfile = new HashMap<>();
    for (TeamDTO teamDTO : teamDTOList) {
      mapTeamIdToProfile.put(teamDTO.getTeamId(), teamDTO);
    }

    for (ReviewTemplateDTO reviewTemplateDTO : reviewTemplateDTOs) {
      ReviewTemplateVO reviewTemplateVO = new ReviewTemplateVO();
      BeanUtils.copyProperties(reviewTemplateDTO, reviewTemplateVO);

      List<Long> teamIds = reviewTemplateDTO.getTeamIds();
      if (CollectionUtils.isEmpty(teamIds)) {
        continue;
      }

      List<TeamVO> teamVOs = new ArrayList<>();
      for (long teamId : teamIds) {
        if (!mapTeamIdToProfile.containsKey(teamId)) {
          continue;
        }
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(mapTeamIdToProfile.get(teamId), teamVO);
        teamVOs.add(teamVO);
      }
      reviewTemplateVO.setInvitedTeamVOs(teamVOs);

      finalReviewTemplateVOs.add(reviewTemplateVO);
    }

    return result;
  }

  /**
   * Introductions:
   *     1. 根据 {orgID/state/isDeleted} 遍历 Template 表得到列表 allTemplatesList;
   *        TODO: 由于脏数据的存在, 按理应该改为判断"当前时间在流程结束之前"的 Template? 这样似乎也不太合理;
   *     2. 根据 {orgID/actorUserID/isCanceled/isDeleted} 遍历 Activity 表中得到 excludedTemplatesSet;
   *        TODO: 由于脏数据的存在, 这里只是判断 isCanceled 字段是不合理的, 但是没有好的办法;
   *     3. 将 allTemplatesList 中去除 excludedTemplatesSet 中的 Template, 得到 newTemplatesList;
   *     4. 根据 {orgID/newTemplatesList/actorUserID/isCanceled/isDeleted} 遍历 Invitation 表得到 templateToRevieweesMap<templateID, revieweeIDsList>;
   *        TODO: 由于脏数据的存在, 这里只是判断 isCanceled 字段是不合理的, 但是没有好的办法;
   *     5. 依次遍历 templateToRevieweesMap, 得到每一个 Template 的所有信息, 并且获取其对应的 revieweeIDsList, 据此得到"被评价"人员信息, 最终生成 resultTemplatesList;
   *     6. 对 resultTemplatesList 按照 peerDeadline 排序;
   *     7. 包装好 result 结构并返回.
   * @return
   *     @throws Exception
   */
  @LogAround

  @RequestMapping(value = "/reviews/templates/invitations-for-homepage", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<ReviewTemplateContainRevieweeProfilesListVO> listInvitations() throws Exception {

    // 定义最终返回值
    Result<ReviewTemplateContainRevieweeProfilesListVO> result = new Result<>();
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));

    ReviewTemplateContainRevieweeProfilesListVO reviewTemplateListContainRevieweeProfilesVO =
            new ReviewTemplateContainRevieweeProfilesListVO();

    List<ReviewTemplateContainRevieweeProfilesVO> finalReviewTemplateVOs = new ArrayList<>();
    reviewTemplateListContainRevieweeProfilesVO.setReviewTemplates(finalReviewTemplateVOs);
    result.setData(reviewTemplateListContainRevieweeProfilesVO);

    long orgID = AuthenticationInterceptor.orgId.get();
    long actorUserID = AuthenticationInterceptor.actorUserId.get();

    ReviewTemplateContainUserProfileListDTO reviewTemplateContainUserProfileListDTO = facadeFactory
            .getReviewTemplateFacade().listAllValidTemplatesForInvitationsOfHomepage(orgID, actorUserID);

    if (ServiceStatus.COMMON_OK.getCode() != reviewTemplateContainUserProfileListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewTemplateContainUserProfileListDTO.getServiceStatusDTO().getCode());
    }

    List<ReviewTemplateContainUserProfileDTO> reviewTemplateContainUserProfileDTOs =
            reviewTemplateContainUserProfileListDTO.getReviewTemplateContainUserProfileDTOs();
    if (CollectionUtils.isEmpty(reviewTemplateContainUserProfileDTOs)) {
      return result;
    }

    HashSet<Long> allTeamIds = new HashSet<>();
    HashSet<Long> allRevieweeIds = new HashSet<>();
    for (ReviewTemplateContainUserProfileDTO reviewTemplateContainUserProfileDTO: reviewTemplateContainUserProfileDTOs) {
      List<Long> teamIds = reviewTemplateContainUserProfileDTO.getTeamIds();
      if (CollectionUtils.isEmpty(teamIds)) {
        continue;
      }
      allTeamIds.addAll(teamIds);

      List<Long> revieweeIds = reviewTemplateContainUserProfileDTO.getRevieweeIds();
      if (CollectionUtils.isEmpty(revieweeIds)) {
        continue;
      }
      allRevieweeIds.addAll(revieweeIds);
    }

    if (CollectionUtils.isEmpty(allTeamIds) || CollectionUtils.isEmpty(allRevieweeIds)) {
      return result;
    }

    TeamListDTO userAndTeamListDTO = facadeFactory.getUserFacade().fetchTeamAndUserProfiles(
            orgID, new ArrayList<>(allTeamIds), new ArrayList<>(allRevieweeIds)
    );

    if (ServiceStatus.COMMON_OK.getCode() != userAndTeamListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(userAndTeamListDTO.getServiceStatusDTO().getCode());
    }

    List<TeamDTO> teamDTOList = userAndTeamListDTO.getTeamDTOList();
    List<CoreUserProfileDTO> coreUserProfileDTOs = userAndTeamListDTO.getCoreUserProfileDTOs();

    if (CollectionUtils.isEmpty(teamDTOList) || CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      return result;
    }

    HashMap<Long, TeamDTO> mapTeamIdToProfile = new HashMap<>();
    for (TeamDTO teamDTO: teamDTOList) {
      mapTeamIdToProfile.put(teamDTO.getTeamId(), teamDTO);
    }

    HashMap<Long, CoreUserProfileDTO> mapUserIdToProfile = new HashMap<>();
    for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
      mapUserIdToProfile.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
    }

    for (ReviewTemplateContainUserProfileDTO reviewTemplateContainUserProfileDTO: reviewTemplateContainUserProfileDTOs) {
      ReviewTemplateContainRevieweeProfilesVO reviewTemplateContainRevieweeProfilesVO =
              new ReviewTemplateContainRevieweeProfilesVO();
      BeanUtils.copyProperties(reviewTemplateContainUserProfileDTO, reviewTemplateContainRevieweeProfilesVO);

      List<Long> teamIds = reviewTemplateContainUserProfileDTO.getTeamIds();
      if (CollectionUtils.isEmpty(teamIds)) {
        continue;
      }
      List<TeamVO> teamVOs = new ArrayList<>();
      for (long teamId: teamIds) {
        if (!mapTeamIdToProfile.containsKey(teamId)) {
          continue;
        }
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(mapTeamIdToProfile.get(teamId), teamVO);
        teamVOs.add(teamVO);
      }
      reviewTemplateContainRevieweeProfilesVO.setInvitedTeamVOs(teamVOs);

      List<Long> revieweeIds = reviewTemplateContainUserProfileDTO.getRevieweeIds();
      if (CollectionUtils.isEmpty(revieweeIds)) {
        continue;
      }
      List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
      for (long revieweeId: revieweeIds) {
        if (!mapUserIdToProfile.containsKey(revieweeId)) {
          continue;
        }
        CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
        BeanUtils.copyProperties(mapUserIdToProfile.get(revieweeId), coreUserProfileVO);
        coreUserProfileVOs.add(coreUserProfileVO);
      }
      reviewTemplateContainRevieweeProfilesVO.setRevieweeProfileList(coreUserProfileVOs);

      finalReviewTemplateVOs.add(reviewTemplateContainRevieweeProfilesVO);
    }

    return result;
  }
}
