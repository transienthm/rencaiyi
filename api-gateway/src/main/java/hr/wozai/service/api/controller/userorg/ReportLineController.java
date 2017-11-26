// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.orgteam.ReportLineVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016/3/2
 */
@Controller("reportLineController")
public class ReportLineController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportLineController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private PermissionChecker permissionChecker;

  @LogAround

  @RequestMapping(value = "/report-lines", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Object addReportLine(@RequestBody ReportLineVO reportLineVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.CREATE.getCode());


    long reportorUserId = reportLineVO.getReportor();
    List<Long> reporteeUserIds = new ArrayList<>();
    for(IdVO id : reportLineVO.getReportees()) {
      reporteeUserIds.add(id.getIdValue());
    }

    VoidDTO remoteResult = facadeFactory.getUserFacade().batchInsertReportLine(orgId, reporteeUserIds, reportorUserId, actorUserId,adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/report-lines", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> transferReportor(@RequestBody ReportLineVO transferReportVO) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    List<Long> userIds = new ArrayList<>();
    for (IdVO id : transferReportVO.getReportees()) {
      userIds.add(id.getIdValue());
    }

    VoidDTO remoteResult = facadeFactory.getUserFacade().batchUpdateReportLine(orgId, userIds,
            transferReportVO.getReportor(), actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/report-lines/reportees/{userId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Object getReporteesByUserId(@PathVariable String userId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptUserId = getDecryptValueFromString(userId);

    Result<Object> result = new Result<>();
    CoreUserProfileListDTO remoteResult = facadeFactory.getUserFacade().listReporteesByUserIdAndOrgId(orgId, decryptUserId,actorUserId,adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
    for (CoreUserProfileDTO coreUserProfileDTO : remoteResult.getCoreUserProfileDTOs()) {
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
      coreUserProfileVOs.add(coreUserProfileVO);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(coreUserProfileVOs);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/report-lines/reportors/{userId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Object getReportorByUserId(@PathVariable String userId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptUserId = -1;
    try {

      decryptUserId = Long.parseLong(EncryptUtils.symmetricDecrypt(userId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    Result<Object> result = new Result<>();
    CoreUserProfileDTO remoteResult = facadeFactory.getUserFacade().getReportorByUserIdAndOrgId(orgId, decryptUserId,actorUserId,adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
    BeanUtils.copyProperties(remoteResult, coreUserProfileVO);

    result.setCodeAndMsg(serviceStatus);
    result.setData(coreUserProfileVO);
    return result;
  }

  private long getDecryptValueFromString(String encryptValue) {
    long decryptValue = -1;
    try {
      decryptValue = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptValue));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return decryptValue;
  }
}
