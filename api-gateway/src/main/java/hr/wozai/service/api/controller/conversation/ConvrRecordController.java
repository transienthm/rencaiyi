// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.conversation;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.wozai.service.api.component.ProfileMetaPermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.conversation.ConvrRecordListVO;
import hr.wozai.service.api.vo.conversation.ConvrRecordVO;
import hr.wozai.service.api.vo.conversation.ConvrScheduleListVO;
import hr.wozai.service.api.vo.conversation.ConvrScheduleVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.SimpleUserProfileVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.conversation.dto.ConvrRecordDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrRecordListDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-29
 */
@Controller("convrRecordController")
public class ConvrRecordController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConvrRecordController.class);

  @Autowired
  ProfileMetaPermissionChecker profileMetaPermissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround
  @RequestMapping(
      value = "/conversations/records",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result<IdVO> addConvrRecord(
      @RequestBody ConvrRecordVO convrRecordVO
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    // TODO: SecModel

    try {
      // rpc
      ConvrRecordDTO convrRecordDTO = new ConvrRecordDTO();
      BeanUtils.copyProperties(convrRecordVO, convrRecordDTO);
      LongDTO addResult = facadeFactory.getConvrFacade().addConvrRecord(
          authedOrgId, convrRecordDTO, authedActorUserId, authedAdminUserId);
      // handle result
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(addResult.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addConvrRecord()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/conversations/records/{convrRecordId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<ConvrRecordVO> getConvrRecord(
    @PathVariable("convrRecordId") String encryptedConvrRecordId
  ) {

    Result<ConvrRecordVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long convrRecordId = -1;
    try {
      convrRecordId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedConvrRecordId));
    } catch (Exception e) {
      LOGGER.error("getConvrRecord()-error: invalid param");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // TODO: SecModel

    try {
      // rpc
      ConvrRecordDTO convrRecordDTO = facadeFactory.getConvrFacade()
          .getConvrRecord(authedOrgId, convrRecordId, authedActorUserId, authedAdminUserId);
      ServiceStatus convrRecordDTOStatus =
          ServiceStatus.getEnumByCode(convrRecordDTO.getServiceStatusDTO().getCode());
      if (convrRecordDTOStatus.equals(ServiceStatus.COMMON_OK)) {
        ConvrRecordVO convrRecordVO = new ConvrRecordVO();
        BeanUtils.copyProperties(convrRecordDTO, convrRecordVO);
        // set userProfiles
        List<Long> userIds = new ArrayList<>();
        userIds.add(convrRecordVO.getSourceUserId());
        userIds.add(convrRecordVO.getTargetUserId());
        CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory
            .getUserProfileFacade().listCoreUserProfile(authedOrgId, userIds, authedActorUserId, authedAdminUserId);
        if (ServiceStatus.COMMON_OK.getCode() == coreUserProfileListDTO.getServiceStatusDTO().getCode()) {
          for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileListDTO.getCoreUserProfileDTOs()) {
            if (convrRecordVO.getSourceUserId().longValue() == coreUserProfileDTO.getUserId().longValue()) {
              SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
              BeanUtils.copyProperties(coreUserProfileDTO, simpleUserProfileVO);
              convrRecordVO.setSourceUserProfileVO(simpleUserProfileVO);
            } else if (convrRecordVO.getTargetUserId().longValue() == coreUserProfileDTO.getUserId().longValue()) {
              SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
              BeanUtils.copyProperties(coreUserProfileDTO, simpleUserProfileVO);
              convrRecordVO.setTargetUserProfileVO(simpleUserProfileVO);
            }
          }
        }
        result.setData(convrRecordVO);
      }
      result.setCodeAndMsg(convrRecordDTOStatus);
    } catch (Exception e) {
      LOGGER.info("getConvrRecord()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround
  @RequestMapping(
      value = "/conversations/schedules/{convrScheduleId}/records",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<ConvrRecordListVO> listConvrRecordOfScheduleAsSourceUser(
      @PathVariable("convrScheduleId") String encryptedConvrScheduleId,
      @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
  ) {

    if (!PageUtils.isPageParamValid(pageNumber, pageSize)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    Result<ConvrRecordListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long convrScheduleId = -1;
    try {
      convrScheduleId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedConvrScheduleId));
    } catch (Exception e) {
      LOGGER.error("listConvrRecordAsSource()-error: invalid param");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // TODO: SecModel

    try {
      // rpc
      ConvrRecordListDTO convrRecordListDTO = facadeFactory.getConvrFacade().listConvrRecordOfScheduleAsSourceUser(
          authedOrgId, convrScheduleId, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
      ServiceStatus convrRecordListDTOStatus =
          ServiceStatus.getEnumByCode(convrRecordListDTO.getServiceStatusDTO().getCode());
      if (convrRecordListDTOStatus.equals(ServiceStatus.COMMON_OK)) {
        ConvrRecordListVO convrRecordListVO = new ConvrRecordListVO();
        // set totalNumber
        convrRecordListVO.setTotalNumber(convrRecordListDTO.getTotalNumber());
        // set convrRecordVOs
        Set<Long> userIds = new HashSet<>();
        List<ConvrRecordVO> convrRecordVOs = new ArrayList<>();
        for (ConvrRecordDTO convrRecordDTO: convrRecordListDTO.getConvrRecordDTOs()) {
          userIds.add(convrRecordDTO.getSourceUserId());
          userIds.add(convrRecordDTO.getTargetUserId());
          ConvrRecordVO convrRecordVO = new ConvrRecordVO();
          BeanUtils.copyProperties(convrRecordDTO, convrRecordVO);
          convrRecordVOs.add(convrRecordVO);
        }
        convrRecordListVO.setConvrRecordVOs(convrRecordVOs);
        // set userProfiles
        CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade()
            .listCoreUserProfile(authedOrgId, new ArrayList<>(userIds), authedActorUserId, authedAdminUserId);
        List<SimpleUserProfileVO> coreUserProfileVOs = CoreUserProfileDTOHelper
            .convertCoreUserProfileDTOsToSimpleVOs(coreUserProfileListDTO.getCoreUserProfileDTOs());
        convrRecordListVO.setUserProfileVOs(coreUserProfileVOs);
        // set userProfile
        result.setData(convrRecordListVO);
      }
      result.setCodeAndMsg(convrRecordListDTOStatus);
    } catch (Exception e) {
      LOGGER.info("listConvrRecordOfScheduleAsSourceUser()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/conversations/records/as-target",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<ConvrRecordListVO> listConvrRecordOfAsTargetUser(
      @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
  ) {

    if (!PageUtils.isPageParamValid(pageNumber, pageSize)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    Result<ConvrRecordListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    // TODO: SecModel

    try {
      ConvrRecordListDTO convrRecordListDTO = facadeFactory.getConvrFacade().listConvrRecordByTargetUserId(
          authedOrgId, authedActorUserId, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
      ServiceStatus convrRecordListDTOStatus =
          ServiceStatus.getEnumByCode(convrRecordListDTO.getServiceStatusDTO().getCode());
      if (convrRecordListDTOStatus.equals(ServiceStatus.COMMON_OK)) {
        ConvrRecordListVO convrRecordListVO = new ConvrRecordListVO();
        // set totalNumber
        convrRecordListVO.setTotalNumber(convrRecordListDTO.getTotalNumber());
        // set convrRecordVOs
        Set<Long> userIds = new HashSet<>();
        List<ConvrRecordVO> convrRecordVOs = new ArrayList<>();
        for (ConvrRecordDTO convrRecordDTO: convrRecordListDTO.getConvrRecordDTOs()) {
          userIds.add(convrRecordDTO.getSourceUserId());
          userIds.add(convrRecordDTO.getTargetUserId());
          ConvrRecordVO convrRecordVO = new ConvrRecordVO();
          BeanUtils.copyProperties(convrRecordDTO, convrRecordVO);
          convrRecordVOs.add(convrRecordVO);
        }
        // set userProfiles
        if (!CollectionUtils.isEmpty(userIds)) {
          CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade()
              .listCoreUserProfile(authedOrgId, new ArrayList<>(userIds), authedActorUserId, authedAdminUserId);
          List<SimpleUserProfileVO> coreUserProfileVOs = CoreUserProfileDTOHelper
              .convertCoreUserProfileDTOsToSimpleVOs(coreUserProfileListDTO.getCoreUserProfileDTOs());
          convrRecordListVO.setUserProfileVOs(coreUserProfileVOs);
          // set userProfile
          convrRecordListVO.setConvrRecordVOs(convrRecordVOs);
        }
        result.setData(convrRecordListVO);
      }
      result.setCodeAndMsg(convrRecordListDTOStatus);
    } catch (Exception e) {
      LOGGER.info("listConvrRecordOfAsTargetUser()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/conversations/records/{convrRecordId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result<VoidDTO> updateConvrRecord(
      @PathVariable("convrRecordId") String encryptedConvrRecordId,
      @RequestBody ConvrRecordVO convrRecordVO
  ) {

    Result<VoidDTO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long convrRecordId = -1;
    try {
      convrRecordId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedConvrRecordId));
    } catch (Exception e) {
      LOGGER.error("updateConvrRecord()-error: invalid param");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // TODO: SecModel

    try {
      ConvrRecordDTO convrRecordDTO = new ConvrRecordDTO();
      BeanUtils.copyProperties(convrRecordVO, convrRecordDTO);
      convrRecordDTO.setConvrRecordId(convrRecordId);
      VoidDTO updateRpcResult = facadeFactory.getConvrFacade()
          .updateConvrRecord(authedOrgId, convrRecordDTO, authedActorUserId, authedAdminUserId);
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(updateRpcResult.getServiceStatusDTO().getCode()));
    } catch (Exception e) {
      LOGGER.info("updateConvrRecord()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


}
