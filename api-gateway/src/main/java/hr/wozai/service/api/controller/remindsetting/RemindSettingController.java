package hr.wozai.service.api.controller.remindsetting;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.RemindSetting.RemindSettingVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.common.dto.RemindSettingDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/16
 */
@Controller("remindSettingController")
public class RemindSettingController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemindSettingController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround
  @RequestMapping(value = "/remind-settings", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listRemindSetting() {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    RemindSettingListDTO remoteResult = facadeFactory.getCommonToolFacade()
            .listRemindSettingByUserId(orgId, actorUserId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<RemindSettingVO> remindSettingVOs = new ArrayList<>();
    for (RemindSettingDTO remindSettingDTO : remoteResult.getRemindSettingDTOList()) {
      RemindSettingVO remindSettingVO = new RemindSettingVO();
      BeanUtils.copyProperties(remindSettingDTO, remindSettingVO);
      remindSettingVOs.add(remindSettingVO);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(remindSettingVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/remind-settings", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateRemindSetting(@RequestBody List<RemindSettingVO> remindSettingVOs) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    RemindSettingListDTO remindSettingListDTO = new RemindSettingListDTO();
    List<RemindSettingDTO> remindSettingDTOs = new ArrayList<>();
    for (RemindSettingVO remindSettingVO : remindSettingVOs) {
      RemindSettingDTO remindSettingDTO = new RemindSettingDTO();
      BeanUtils.copyProperties(remindSettingVO, remindSettingDTO);
      remindSettingDTO.setOrgId(orgId);
      remindSettingDTO.setUserId(actorUserId);
      remindSettingDTO.setLastModifiedUserId(actorUserId);
      remindSettingDTOs.add(remindSettingDTO);
    }
    remindSettingListDTO.setRemindSettingDTOList(remindSettingDTOs);

    VoidDTO remoteResult = facadeFactory.getCommonToolFacade()
            .batchUpdateRemindSetting(remindSettingListDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }
}
