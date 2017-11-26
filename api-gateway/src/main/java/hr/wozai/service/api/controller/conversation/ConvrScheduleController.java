// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.conversation;

import hr.wozai.service.api.util.ConvrScheduleChartHelper;
import hr.wozai.service.api.vo.conversation.*;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleChartDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleChartListDTO;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.utils.ConvrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import hr.wozai.service.api.component.ProfileMetaPermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.conversation.dto.ConvrScheduleDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-29
 */
@Controller("convrScheduleController")
public class ConvrScheduleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvrScheduleController.class);

    @Autowired
    ProfileMetaPermissionChecker profileMetaPermissionChecker;

    @Autowired
    FacadeFactory facadeFactory;

    @Autowired
    ConvrScheduleChartHelper convrScheduleChartHelper;

    @LogAround

    @RequestMapping(
            value = "/conversations/schedules",
            method = RequestMethod.POST,
            produces = "application/json")
    @ResponseBody
    public Result<IdVO> addConvrSchedule(
            @RequestBody ConvrScheduleVO convrScheduleVO
    ) {

        Result<IdVO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();

        // TODO: SecModel

        try {
            // rpc
            ConvrScheduleDTO convrScheduleDTO = new ConvrScheduleDTO();
            BeanUtils.copyProperties(convrScheduleVO, convrScheduleDTO);
            LongDTO addResult = facadeFactory.getConvrFacade().addConvrSchedule(
                    authedOrgId, convrScheduleDTO, authedActorUserId, authedAdminUserId);
            // handle result
            ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
            if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
                IdVO idVO = new IdVO();
                idVO.setIdValue(addResult.getData());
                result.setData(idVO);
            }
            result.setCodeAndMsg(rpcStatus);
        } catch (Exception e) {
            LOGGER.info("addConvrSchedule()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @LogAround

    @RequestMapping(
            value = "/conversations/schedules/{convrScheduleId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public Result<ConvrScheduleVO> getConvrSchedule(
            @PathVariable("convrScheduleId") String encryptedConvrScheduleId
    ) {

        Result<ConvrScheduleVO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        long convrScheduleId = -1;
        try {
            convrScheduleId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedConvrScheduleId));
        } catch (Exception e) {
            LOGGER.error("getConvrSchedule()-error: invalid param");
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        // TODO: SecModel

        try {
            // rpc
            ConvrScheduleDTO convrScheduleDTO = facadeFactory.getConvrFacade()
                    .getConvrSchedule(authedOrgId, convrScheduleId, authedActorUserId, authedAdminUserId);
            ServiceStatus convrScheduleDTOStatus =
                    ServiceStatus.getEnumByCode(convrScheduleDTO.getServiceStatusDTO().getCode());
            if (convrScheduleDTOStatus.equals(ServiceStatus.COMMON_OK)) {
                ConvrScheduleVO convrScheduleVO = new ConvrScheduleVO();
                BeanUtils.copyProperties(convrScheduleDTO, convrScheduleVO);
                // set user profile
                CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade().listCoreUserProfile(
                        authedOrgId, Arrays.asList(convrScheduleVO.getTargetUserId()), authedActorUserId, authedAdminUserId);
                if (ServiceStatus.COMMON_OK.getCode() == coreUserProfileListDTO.getServiceStatusDTO().getCode()) {
                    CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                    BeanUtils.copyProperties(coreUserProfileListDTO.getCoreUserProfileDTOs().get(0), coreUserProfileVO);
                    convrScheduleVO.setTargetUserProfile(coreUserProfileVO);
                }
                result.setData(convrScheduleVO);
            }
            result.setCodeAndMsg(convrScheduleDTOStatus);
        } catch (Exception e) {
            LOGGER.info("getConvrSchedule()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @LogAround

    @RequestMapping(
            value = "/conversations/schedules",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public Result<ConvrScheduleListVO> listConvrSchedule(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {

        if (!PageUtils.isPageParamValid(pageNumber, pageSize)) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        Result<ConvrScheduleListVO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();

        // TODO: SecModel

        try {
            // rpc
            ConvrScheduleListDTO convrScheduleListDTO = facadeFactory.getConvrFacade().listConvrScheduleBySourceUserId(
                    authedOrgId, authedActorUserId, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
            ServiceStatus convrScheduleListDTOStatus =
                    ServiceStatus.getEnumByCode(convrScheduleListDTO.getServiceStatusDTO().getCode());
            if (convrScheduleListDTOStatus.equals(ServiceStatus.COMMON_OK)) {
                ConvrScheduleListVO convrScheduleListVO = new ConvrScheduleListVO();
                convrScheduleListVO.setTotalNumber(convrScheduleListDTO.getTotalNumber());
                List<ConvrScheduleVO> convrScheduleVOs = new ArrayList<>();
                List<Long> targetUserIds = new ArrayList<>();
                for (ConvrScheduleDTO convrScheduleDTO: convrScheduleListDTO.getConvrScheduleDTOs()) {
                    ConvrScheduleVO convrScheduleVO = new ConvrScheduleVO();
                    BeanUtils.copyProperties(convrScheduleDTO, convrScheduleVO);
                    targetUserIds.add(convrScheduleDTO.getTargetUserId());
                    convrScheduleVOs.add(convrScheduleVO);
                }
                // set user profiles
                CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade().listCoreUserProfile(
                        authedOrgId, targetUserIds, authedActorUserId, authedAdminUserId);
                if (ServiceStatus.COMMON_OK.getCode() == coreUserProfileListDTO.getServiceStatusDTO().getCode()) {
                    Map<Long, CoreUserProfileVO> profileMap =
                            CoreUserProfileDTOHelper.convertDTOListToVOMap(coreUserProfileListDTO.getCoreUserProfileDTOs());
                    for (ConvrScheduleVO convrScheduleVO: convrScheduleVOs) {
                        if (profileMap.containsKey(convrScheduleVO.getTargetUserId())) {
                            convrScheduleVO.setTargetUserProfile(profileMap.get(convrScheduleVO.getTargetUserId()));
                        }
                    }
                }
                convrScheduleListVO.setConvrScheduleVOs(convrScheduleVOs);
                result.setData(convrScheduleListVO);
            }
            result.setCodeAndMsg(convrScheduleListDTOStatus);
        } catch (Exception e) {
            LOGGER.info("listConvrSchedule()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @LogAround

    @RequestMapping(
            value = "/conversations/schedules/{convrScheduleId}",
            method = RequestMethod.PUT,
            produces = "application/json")
    @ResponseBody
    public Result<VoidDTO> updateConvrSchedule(
            @PathVariable("convrScheduleId") String encryptedConvrScheduleId,
            @RequestBody ConvrScheduleVO convrScheduleVO
    ) {

        Result<VoidDTO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        long convrScheduleId = -1;
        try {
            convrScheduleId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedConvrScheduleId));
        } catch (Exception e) {
            LOGGER.error("updateConvrSchedule()-error: invalid param");
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        // TODO: SecModel

        try {
            ConvrScheduleDTO convrScheduleDTO = new ConvrScheduleDTO();
            BeanUtils.copyProperties(convrScheduleVO, convrScheduleDTO);
            convrScheduleDTO.setConvrScheduleId(convrScheduleId);
            VoidDTO updateRpcResult = facadeFactory.getConvrFacade()
                    .updateConvrSchedule(authedOrgId, convrScheduleDTO, authedActorUserId, authedAdminUserId);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(updateRpcResult.getServiceStatusDTO().getCode()));
        } catch (Exception e) {
            LOGGER.info("updateConvrSchedule()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @LogAround

    @RequestMapping(
            value = "/conversations/schedules/remind",
            method = RequestMethod.POST,
            produces = "application/json")
    @ResponseBody
    public Result<String> getConvrScheduleNextRemindDay(
            @RequestBody ConvrScheduleVO convrScheduleVO
    ) {

        Result<String> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        long convrScheduleId = -1;

        try {
            Date date = new Date();
            Integer remindDay = convrScheduleVO.getRemindDay();
            Integer periodTypeCode = convrScheduleVO.getPeriodType();
            PeriodType periodType = PeriodType.getEnumByCode(periodTypeCode);
            String nextTimeToRemind = ConvrUtils.nextTimeToRemind(date, remindDay, periodType);
            result.setData(nextTimeToRemind);
        } catch (Exception e) {
            LOGGER.info("getConvrSchedule()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
        return result;
    }





//  // Note: comment out according to product design on 2016-08-08
////  @LogAround
////
////  @RequestMapping(
////      value = "/profile-templates",
////      method = RequestMethod.POST,
////      produces = "application/json")
////  @ResponseBody
//  public Result<IdVO> addCustomProfileTemplate(
//      @RequestBody ProfileTemplateVO profileTemplateVO
//  ) {
//
//    Result<IdVO> result = new Result<>();
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();
//    String displayName = null;
//
//    try {
//      displayName = profileTemplateVO.getDisplayName();
//    } catch (Exception e) {
//      LOGGER.info("addCustomProfileTemplate()-error: invalid param", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//    }
//
//    if (!profileMetaPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    try {
//      // rpc
//      LongDTO addResult = facadeFactory.getProfileTemplateFacade().addCustomProfileTemplate(
//          authedOrgId, displayName, authedActorUserId, authedAdminUserId);
//      // handle result
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
//      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
//        IdVO idVO = new IdVO();
//        idVO.setIdValue(addResult.getData());
//        result.setData(idVO);
//      }
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("addCustomProfileTemplate()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }
//
//  // Note: comment out according to product design on 2016-08-08
////  @LogAround
////
////  @RequestMapping(
////      value = "/profile-templates/{profileTemplateId}",
////      method = RequestMethod.GET,
////      produces = "application/json")
////  @ResponseBody
//  public Result<ProfileTemplateVO> getProfileTemplates(
//      @PathVariable("profileTemplateId") String encrypedProfileTemplateId
//  ) {
//
//    Result<ProfileTemplateVO> result = new Result<>();
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();
//    long profileTemplateId = 0;
//
//    try {
//      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
//    } catch (Exception e) {
//      LOGGER.error("getProfileField-error: invalid id", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//    }
//
//    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    try {
//      // rpc call
//      ProfileTemplateDTO getResult = facadeFactory.getProfileTemplateFacade().getProfileTemplate(
//          authedOrgId, profileTemplateId, authedActorUserId, authedAdminUserId);
//      // handle result
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
//      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
//        ProfileTemplateVO profileTemplateVO = new ProfileTemplateVO();
//        BeanUtils.copyProperties(getResult, profileTemplateVO);
//        result.setData(profileTemplateVO);
//      }
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("profileTemplateId()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }
//
//
//  // Note: comment out according to product design on 2016-08-08
////  @LogAround
////
////  @RequestMapping(
////      value = "/profile-templates/{profileTemplateId}",
////      method = RequestMethod.PUT,
////      produces = "application/json")
////  @ResponseBody
//  public Result updateDisplayNameOfProfileTemplate(
//      @PathVariable("profileTemplateId") String encrypedProfileTemplateId,
//      @RequestBody ProfileTemplateVO profileTemplateVO
//  ) {
//
//    Result result = new Result();
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();
//    long profileTemplateId = 0;
//    String displayName = null;
//
//    try {
//      displayName = profileTemplateVO.getDisplayName();
//      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
//    } catch (Exception e) {
//      LOGGER.error("updateDisplayNameOfProfileTemplate-error: invalid param", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//    }
//
//    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    try {
//      // rpc call
//      VoidDTO updateResult = facadeFactory.getProfileTemplateFacade().updateProfileTemplateDisplayName(
//          authedOrgId, profileTemplateId, displayName, authedActorUserId, authedAdminUserId);
//      // handle result
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(updateResult.getServiceStatusDTO().getCode());
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("updateDisplayNameOfProfileTemplate()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }
//
//  // Note: comment out according to product design on 2016-08-08
////  @LogAround
////
////  @RequestMapping(
////      value = "/profile-templates/{profileTemplateId}",
////      method = RequestMethod.DELETE,
////      produces = "application/json")
////  @ResponseBody
//  public Result deleteProfileTemplate(
//      @PathVariable("profileTemplateId") String encrypedProfileTemplateId
//  ) {
//
//    Result result = new Result();
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();
//    long profileTemplateId = 0;
//    String displayName = null;
//
//    try {
//      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
//    } catch (Exception e) {
//      LOGGER.error("deleteProfileTemplate-error: invalid param", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//    }
//
//    if (!profileMetaPermissionChecker.canDelete(authedOrgId, authedActorUserId)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    try {
//      // rpc call
//      VoidDTO deleteResult = facadeFactory.getProfileTemplateFacade().deleteProfileTemplate(
//          authedOrgId, profileTemplateId, authedActorUserId, authedAdminUserId);
//      // handle result
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(deleteResult.getServiceStatusDTO().getCode());
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("deleteProfileTemplate()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }
//
//  // Note: comment out according to product design on 2016-08-08
////  @LogAround
////
////  @RequestMapping(
////      value = "/profile-templates",
////      method = RequestMethod.GET,
////      produces = "application/json")
////  @ResponseBody
//  public Result<ProfileTemplateListVO> listProfileTemplate() {
//
//    Result<ProfileTemplateListVO> result = new Result<>();
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();
//
//    if (!profileMetaPermissionChecker.canList(authedOrgId, authedActorUserId)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    try {
//      // rpc
//      ProfileTemplateListDTO listResult = facadeFactory.getProfileTemplateFacade().listProfileTemplate(
//          authedOrgId, authedActorUserId, authedAdminUserId);
//      // handle result
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
//      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
//        List<ProfileTemplateDTO> profileTemplateDTOs = listResult.getProfileTemplateDTOs();
//        List<ProfileTemplateVO> profileTemplateVOs = null;
//        if (!CollectionUtils.isEmpty(profileTemplateDTOs)) {
//          profileTemplateVOs = new ArrayList<>();
//          for (ProfileTemplateDTO profileTemplateDTO : profileTemplateDTOs) {
//            ProfileTemplateVO profileTemplateVO = new ProfileTemplateVO();
//            BeanHelper.copyPropertiesHandlingJSON(profileTemplateDTO, profileTemplateVO);
//            profileTemplateVOs.add(profileTemplateVO);
//          }
//        } else {
//          profileTemplateVOs = Collections.EMPTY_LIST;
//        }
//        ProfileTemplateListVO profileTemplateListVO = new ProfileTemplateListVO();
//        profileTemplateListVO.setProfileTemplateVOs(profileTemplateVOs);
//        result.setData(profileTemplateListVO);
//      }
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("listProfileTemplate()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }
//
//  @LogAround
//
//  @RequestMapping(
//      value = "/org-pick-options",
//      method = RequestMethod.GET,
//      produces = "application/json")
//  @ResponseBody
//  public Result<OrgPickOptionListVO> listOrgPickOption(
//      @RequestParam("configType") Integer configType
//  ) {
//
//    Result<OrgPickOptionListVO> result = new Result<>();
//    long authedActorUserId =
//        (null == AuthenticationInterceptor.actorUserId.get()) ? 0 : AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId =
//        (null == AuthenticationInterceptor.adminUserId.get()) ? 0 : AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId =
//        (null == AuthenticationInterceptor.orgId.get()) ? 0 : AuthenticationInterceptor.orgId.get();
//    long tempOrgId =
//        (null == AuthenticationInterceptor.tempOrgId.get()) ? 0 : AuthenticationInterceptor.tempOrgId.get();
//    long tempUserId =
//        (null == AuthenticationInterceptor.tempUserId.get()) ? 0 : AuthenticationInterceptor.tempUserId.get();
//
//
////    // TODO: check with @Lepujiu
////    if (!profileMetaPermissionChecker.canList(authedOrgId, authedActorUserId)) {
////      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
////    }
//
//    try {
//      long orgId = (authedOrgId == 0) ? tempOrgId : authedOrgId;
//      OrgPickOptionListDTO orgPickOptionListDTO =
//          facadeFactory.getProfileTemplateFacade().listOrgPickOptionOfConfigType(orgId, configType);
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(orgPickOptionListDTO.getServiceStatusDTO().getCode());
//      if (ServiceStatus.COMMON_OK.equals(rpcStatus)) {
//        OrgPickOptionListVO orgPickOptionListVO = new OrgPickOptionListVO();
//        List<OrgPickOptionDTO> orgPickOptionDTOs = orgPickOptionListDTO.getOrgPickOptionDTOs();
//        List<OrgPickOptionVO> orgPickOptionVOs = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(orgPickOptionDTOs)) {
//          for (OrgPickOptionDTO orgPickOptionDTO: orgPickOptionDTOs) {
//            OrgPickOptionVO orgPickOptionVO = new OrgPickOptionVO();
//            BeanUtils.copyProperties(orgPickOptionDTO, orgPickOptionVO);
//            orgPickOptionVOs.add(orgPickOptionVO);
//          }
//        }
//        orgPickOptionListVO.setOrgPickOptionVOs(orgPickOptionVOs);
//        result.setData(orgPickOptionListVO);
//      }
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("listOrgPickOption()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }
//
//
//  @RequestMapping(
//      value = "/org-pick-options",
//      method = RequestMethod.PUT,
//      produces = "application/json")
//  @ResponseBody
//  public Result updateOrgPickOption(
//      @RequestBody OrgPickOptionListVO orgPickOptionListVO
//  ) {
//
//    Result result = new Result<>();
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();
//
//    // TODO: check with @Lepujiu
//    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
//    }
//
//    try {
//      OrgPickOptionListDTO orgPickOptionListDTO = new OrgPickOptionListDTO();
//      List<OrgPickOptionDTO> orgPickOptionDTOs = new ArrayList<>();
//      if (null != orgPickOptionListVO
//          && !CollectionUtils.isEmpty(orgPickOptionListVO.getOrgPickOptionVOs())) {
//        for (OrgPickOptionVO orgPickOptionVO: orgPickOptionListVO.getOrgPickOptionVOs()) {
//          OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
//          BeanUtils.copyProperties(orgPickOptionVO, orgPickOptionDTO);
//          orgPickOptionDTOs.add(orgPickOptionDTO);
//        }
//      }
//      orgPickOptionListDTO.setOrgPickOptionDTOs(orgPickOptionDTOs);
//      VoidDTO voidDTO = facadeFactory.getProfileTemplateFacade()
//          .batchUpdateOrgPickOptions(authedOrgId, orgPickOptionListDTO, authedActorUserId);
//      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(voidDTO.getServiceStatusDTO().getCode());
//      result.setCodeAndMsg(rpcStatus);
//    } catch (Exception e) {
//      LOGGER.info("listOrgPickOption()-error", e);
//      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//    }
//
//    return result;
//  }

}
