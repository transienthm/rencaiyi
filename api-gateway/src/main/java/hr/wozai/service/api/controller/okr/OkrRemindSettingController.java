package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.okr.LevelOneTimeSpanVO;
import hr.wozai.service.api.vo.okr.ObjectivePeriodVO;
import hr.wozai.service.api.vo.okr.OkrRemindSettingVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.okr.dto.OkrRemindSettingDTO;
import hr.wozai.service.user.client.okr.dto.OkrRemindSettingListDTO;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpanType;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/10/11
 */
@Controller("okrRemindSettingController")
public class OkrRemindSettingController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrRemindSettingController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/okrs/okr-remind-setting",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listOkrRemindSetting() {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    OkrRemindSettingListDTO remoteResult = facadeFactory.getOkrFacade()
            .listOkrRemindSettingsByOrgId(orgId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<OkrRemindSettingVO> okrRemindSettingVOs = new ArrayList<>();
    for (OkrRemindSettingDTO okrRemindSettingDTO : remoteResult.getOkrRemindSettingDTOList()) {
      OkrRemindSettingVO okrRemindSettingVO = new OkrRemindSettingVO();
      BeanUtils.copyProperties(okrRemindSettingDTO, okrRemindSettingVO);
      okrRemindSettingVOs.add(okrRemindSettingVO);
    }
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(okrRemindSettingVOs);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/okr-remind-setting",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> listLevelTwoPeriodTimeSpan(
          @RequestBody List<OkrRemindSettingVO> remindSettingVOs) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    List<OkrRemindSettingDTO> okrRemindSettingDTOs = new ArrayList<>();
    for (OkrRemindSettingVO okrRemindSettingVO : remindSettingVOs) {
      OkrRemindSettingDTO okrRemindSettingDTO = new OkrRemindSettingDTO();
      BeanUtils.copyProperties(okrRemindSettingVO, okrRemindSettingDTO);
      okrRemindSettingDTO.setOrgId(orgId);
      okrRemindSettingDTO.setCreatedUserId(actorUserId);
      okrRemindSettingDTOs.add(okrRemindSettingDTO);
    }
    VoidDTO remoteResult = facadeFactory.getOkrFacade()
            .batchUpdateOkrRemindSettings(orgId, okrRemindSettingDTOs, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    return result;
  }

}
