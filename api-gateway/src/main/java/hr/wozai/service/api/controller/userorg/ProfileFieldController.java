// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldListDTO;
import hr.wozai.service.api.component.ProfileMetaPermissionChecker;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.user.ProfileFieldListVO;
import hr.wozai.service.api.vo.user.ProfileFieldVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-29
 */
@Controller("profileFieldController")
public class ProfileFieldController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileFieldController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ProfileMetaPermissionChecker profileMetaPermissionChecker;

  /**************** Methods after refraction(2016-08-08) below ****************/

  @LogAround

  @RequestMapping(
      value = "/profile-templates/all-profile-fields",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<ProfileFieldListVO> listAllProfileFieldOfOrg() {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      // rpc call
      ProfileFieldListDTO listResult = facadeFactory.getProfileTemplateFacade().listAllProfileFieldOfOrg(
          authedOrgId, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        List<ProfileFieldDTO> profileFieldDTOs = listResult.getProfileFieldDTOs();
        List<ProfileFieldVO> profileFieldVOs = null;
        if (!CollectionUtils.isEmpty(profileFieldDTOs)) {
          profileFieldVOs = new ArrayList<>();
          for (ProfileFieldDTO profileFieldDTO: profileFieldDTOs) {
            ProfileFieldVO profileFieldVO = new ProfileFieldVO();
            BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileFieldVO);
            profileFieldVOs.add(profileFieldVO);
          }
        } else {
          profileFieldVOs = Collections.EMPTY_LIST;
        }
        ProfileFieldListVO profileFieldListVO = new ProfileFieldListVO();
        profileFieldListVO.setProfileFieldVOs(profileFieldVOs);
        result.setData(profileFieldListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("listAllProfileFieldOfOrg()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/profile-templates/core-basic-profile-fields",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result updateCoreBasicProfileField(
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
      if (null != profileFieldVO) {
        BeanUtils.copyProperties(profileFieldVO ,profileFieldDTO);
      }
      VoidDTO voidDTO = facadeFactory.getProfileTemplateFacade()
          .updateUserProfileConfig(authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(voidDTO.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateCoreBasicProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

    @LogAround

  @RequestMapping(
      value = "/profile-templates/meta-profile-fields",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
    @BindingResultMonitor
  public Result<IdVO> addProfileFieldOfMetaUserProfile(
      @RequestBody @Valid ProfileFieldVO profileFieldVO, BindingResult bindingResult
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    DataType dataType = null;

    try {
      dataType = DataType.getEnumByCode(profileFieldVO.getDataType().intValue());
    } catch (Exception e) {
      LOGGER.info("addCustomProfileField()-error: invalid param", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!profileMetaPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      LongDTO addResult = null;
      ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
      BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);
      // rpc
      if (dataType == DataType.CONTAINER) {
        addResult = facadeFactory.getProfileTemplateFacade().addContainerProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      } else {
        addResult = facadeFactory.getProfileTemplateFacade().addDataProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(addResult.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addCustomProfileTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/profile-templates/meta-profile-fields/{profileFieldId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<ProfileFieldVO> getProfileFieldOfMetaUserProfile(
      @PathVariable("profileFieldId") String encrypedProfileFieldId)
  {

    Result<ProfileFieldVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileFieldId = 0;


    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileFieldId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileFieldId));
    } catch (Exception e) {
      LOGGER.error("getProfileField-error: invalid param", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      // rpc call
      ProfileFieldDTO getResult = facadeFactory.getProfileTemplateFacade().getProfileField(
          authedOrgId, profileFieldId, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        ProfileFieldVO profileFieldVO = new ProfileFieldVO();
        BeanHelper.copyPropertiesHandlingJSON(getResult, profileFieldVO);
        result.setData(profileFieldVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getProfileFieldOfMetaUserProfile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/profile-templates/meta-profile-fields/reorder",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result moveProfileFieldOfMetaUserProfile(
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    if (null == profileFieldVO.getDataType()) {
      LOGGER.error("moveProfileField-error: invalid dataType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);

    try {
      DataType dataType = DataType.getEnumByCode(profileFieldDTO.getDataType());
      VoidDTO moveResult = null;
      if (dataType == DataType.CONTAINER) {
        moveResult = facadeFactory.getProfileTemplateFacade().moveContainerProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      } else {
        moveResult = facadeFactory.getProfileTemplateFacade().moveDataProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(moveResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("moveProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/profile-templates/meta-profile-fields/{profileFieldId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result updateProfileFieldOfMetaUserProfile(
      @PathVariable("profileFieldId") String encrypedProfileFieldId,
      @RequestBody @Valid ProfileFieldVO profileFieldVO, BindingResult bindingResult
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileFieldId = 0;

    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileFieldId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileFieldId));
    } catch (Exception e) {
      LOGGER.error("updateProfileFieldOfMetaUserProfile-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (null == profileFieldVO.getDataType()
        || null == DataType.getEnumByCode(profileFieldVO.getDataType())) {
      LOGGER.error("updateProfileField-error: invalid dataType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);
    profileFieldDTO.setProfileFieldId(profileFieldId);

    try {
      DataType dataType = DataType.getEnumByCode(profileFieldDTO.getDataType());
      VoidDTO moveResult = null;
      if (dataType == DataType.CONTAINER) {
        moveResult = facadeFactory.getProfileTemplateFacade().updateContainerProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      } else {
        moveResult = facadeFactory.getProfileTemplateFacade().updateDataProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(moveResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateProfileFieldOfMetaUserProfile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/profile-templates/meta-profile-fields/{profileFieldId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseBody
  public Result deleteProfileFieldOfMetaUserProfile(
      @PathVariable("profileFieldId") String encrypedProfileFieldId,
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;
    long profileFieldId = 0;

    try {
      profileFieldId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileFieldId));
    } catch (Exception e) {
      LOGGER.error("deleteProfileFieldOfMetaUserProfile()-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!profileMetaPermissionChecker.canDelete(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    if (null == profileFieldVO.getDataType()
        || null == DataType.getEnumByCode(profileFieldVO.getDataType())) {
      LOGGER.error("deleteProfileFieldOfMetaUserProfile()-error: invalid dataType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);
    profileFieldDTO.setProfileFieldId(profileFieldId);
    profileFieldDTO.setProfileTemplateId(profileTemplateId);

    try {
      DataType dataType = DataType.getEnumByCode(profileFieldDTO.getDataType());
      VoidDTO moveResult = null;
      if (dataType == DataType.CONTAINER) {
        moveResult = facadeFactory.getProfileTemplateFacade().deleteContainerProfileField(
            authedOrgId, profileFieldId, authedActorUserId, authedAdminUserId);
      } else {
        moveResult = facadeFactory.getProfileTemplateFacade().deleteDataProfileField(
            authedOrgId, profileFieldId, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(moveResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("deleteProfileFieldOfMetaUserProfile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  /**************** Methods before refraction(2016-08-08) below ****************/

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields",
//      method = RequestMethod.POST,
//      produces = "application/json")
//  @ResponseBody
  public Result<IdVO> addCustomProfileField(
      @PathVariable("profileTemplateId") String encryptedProfileTemplateId,
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    String displayName = null;
    DataType dataType = null;

    try {
      long profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedProfileTemplateId));
      profileFieldVO.setProfileTemplateId(profileTemplateId);
      dataType = DataType.getEnumByCode(profileFieldVO.getDataType().intValue());
    } catch (Exception e) {
      LOGGER.info("addCustomProfileField()-error: invalid param", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!profileMetaPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      LongDTO addResult = null;
      ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
      BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);
      // rpc
      if (dataType == DataType.CONTAINER) {
        addResult = facadeFactory.getProfileTemplateFacade().addContainerProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      } else {
        addResult = facadeFactory.getProfileTemplateFacade().addDataProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(addResult.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addCustomProfileTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields/{profileFieldId}",
//      method = RequestMethod.GET,
//      produces = "application/json")
//  @ResponseBody
  public Result<ProfileFieldVO> getProfileField(
      @PathVariable("profileTemplateId") String encrypedProfileTemplateId,
      @PathVariable("profileFieldId") String encrypedProfileFieldId)
  {

    Result<ProfileFieldVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;
    long profileFieldId = 0;


    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileFieldId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileFieldId));
      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
    } catch (Exception e) {
      LOGGER.error("getProfileField-error: invalid param", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      // rpc call
      ProfileFieldDTO getResult = facadeFactory.getProfileTemplateFacade().getProfileField(
          authedOrgId, profileFieldId, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        ProfileFieldVO profileFieldVO = new ProfileFieldVO();
        BeanHelper.copyPropertiesHandlingJSON(getResult, profileFieldVO);
        result.setData(profileFieldVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields/find",
//      method = RequestMethod.GET,
//      produces = "application/json")
//  @ResponseBody
  public Result<ProfileFieldVO> getProfileFieldByReferenceName(
      @PathVariable("profileTemplateId") String encrypedProfileTemplateId,
      @RequestParam("referenceName") String referenceName
  ) {

    Result<ProfileFieldVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;
    long profileFieldId = 0;

    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
    } catch (Exception e) {
      LOGGER.error("getProfileFieldByReferenceName()-error: invalid param", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      // rpc call
      ProfileFieldDTO getResult = facadeFactory.getProfileTemplateFacade().getProfileFieldByReferenceName(
          authedOrgId, profileTemplateId, referenceName, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        ProfileFieldVO profileFieldVO = new ProfileFieldVO();
        BeanHelper.copyPropertiesHandlingJSON(getResult, profileFieldVO);
        result.setData(profileFieldVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getProfileFieldByReferenceName()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields",
//      method = RequestMethod.GET,
//      produces = "application/json")
//  @ResponseBody
  public Result<ProfileFieldListVO> listProfileField(
      @PathVariable("profileTemplateId") String encrypedProfileTemplateId
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;

    if (!profileMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
    } catch (Exception e) {
      LOGGER.error("listProfileField-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      // rpc call
      ProfileFieldListDTO listResult = facadeFactory.getProfileTemplateFacade().listProfileField(
          authedOrgId, profileTemplateId, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        List<ProfileFieldDTO> profileFieldDTOs = listResult.getProfileFieldDTOs();
        List<ProfileFieldVO> profileFieldVOs = null;
        if (!CollectionUtils.isEmpty(profileFieldDTOs)) {
          profileFieldVOs = new ArrayList<>();
          for (ProfileFieldDTO profileFieldDTO: profileFieldDTOs) {
            ProfileFieldVO profileFieldVO = new ProfileFieldVO();
            BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileFieldVO);
            profileFieldVOs.add(profileFieldVO);
          }
        } else {
          profileFieldVOs = Collections.EMPTY_LIST;
        }
        ProfileFieldListVO profileFieldListVO = new ProfileFieldListVO();
        profileFieldListVO.setProfileFieldVOs(profileFieldVOs);
        result.setData(profileFieldListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("profileTemplateId()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields/reorder",
//      method = RequestMethod.PUT,
//      produces = "application/json")
//  @ResponseBody
  public Result moveProfileField(
      @PathVariable("profileTemplateId") String encrypedProfileTemplateId,
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;

    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
    } catch (Exception e) {
      LOGGER.error("moveProfileField-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (null == profileFieldVO.getDataType()) {
      LOGGER.error("moveProfileField-error: invalid dataType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);

    try {
      DataType dataType = DataType.getEnumByCode(profileFieldDTO.getDataType());
      VoidDTO moveResult = null;
      if (dataType == DataType.CONTAINER) {
        moveResult = facadeFactory.getProfileTemplateFacade().moveContainerProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      } else {
        moveResult = facadeFactory.getProfileTemplateFacade().moveDataProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(moveResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("moveProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields/{profileFieldId}",
//      method = RequestMethod.PUT,
//      produces = "application/json")
//  @ResponseBody
  public Result updateProfileField(
      @PathVariable("profileTemplateId") String encrypedProfileTemplateId,
      @PathVariable("profileFieldId") String encrypedProfileFieldId,
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;
    long profileFieldId = 0;

    if (!profileMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
      profileFieldId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileFieldId));
    } catch (Exception e) {
      LOGGER.error("updateProfileField-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (null == profileFieldVO.getDataType()
        || null == DataType.getEnumByCode(profileFieldVO.getDataType())) {
      LOGGER.error("updateProfileField-error: invalid dataType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);
    profileFieldDTO.setProfileFieldId(profileFieldId);
    profileFieldDTO.setProfileTemplateId(profileTemplateId);

    try {
      DataType dataType = DataType.getEnumByCode(profileFieldDTO.getDataType());
      VoidDTO moveResult = null;
      if (dataType == DataType.CONTAINER) {
        moveResult = facadeFactory.getProfileTemplateFacade().updateContainerProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      } else {
        moveResult = facadeFactory.getProfileTemplateFacade().updateDataProfileField(
            authedOrgId, profileFieldDTO, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(moveResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @LogAround
//
//  @RequestMapping(
//      value = "/profile-templates/{profileTemplateId}/profile-fields/{profileFieldId}",
//      method = RequestMethod.DELETE,
//      produces = "application/json")
//  @ResponseBody
  public Result deleteProfileField(
      @PathVariable("profileTemplateId") String encrypedProfileTemplateId,
      @PathVariable("profileFieldId") String encrypedProfileFieldId,
      @RequestBody ProfileFieldVO profileFieldVO
  ) {

    Result<ProfileFieldListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long profileTemplateId = 0;
    long profileFieldId = 0;

    try {
      profileTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileTemplateId));
      profileFieldId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedProfileFieldId));
    } catch (Exception e) {
      LOGGER.error("deleteProfileField-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!profileMetaPermissionChecker.canDelete(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    if (null == profileFieldVO.getDataType()
        || null == DataType.getEnumByCode(profileFieldVO.getDataType())) {
      LOGGER.error("deleteProfileField-error: invalid dataType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanHelper.copyPropertiesHandlingJSON(profileFieldVO, profileFieldDTO);
    profileFieldDTO.setProfileFieldId(profileFieldId);
    profileFieldDTO.setProfileTemplateId(profileTemplateId);

    try {
      DataType dataType = DataType.getEnumByCode(profileFieldDTO.getDataType());
      VoidDTO moveResult = null;
      if (dataType == DataType.CONTAINER) {
        moveResult = facadeFactory.getProfileTemplateFacade().deleteContainerProfileField(
            authedOrgId, profileFieldId, authedActorUserId, authedAdminUserId);
      } else {
        moveResult = facadeFactory.getProfileTemplateFacade().deleteDataProfileField(
            authedOrgId, profileFieldId, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(moveResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("deleteProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }





}
