package hr.wozai.service.api.controller.securitymodel;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.RoleUtil;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/3
 */
@Controller("pageController")
public class PageController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround
  @RequestMapping(value = "/sidebars/homepage", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listHomepageSidebarByRole() {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade()
            .getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    List<String> items = new ArrayList<>();

    if (RoleUtil.isHR(remoteResult.getRoleDTOList())) {
      items.add("组织架构管理");
      items.add("入职管理");
      items.add("评价管理");
    }

    if (RoleUtil.isOrgAdmin(remoteResult.getRoleDTOList())) {
      //items.add("OKR管理");
      items.add("系统设置");
    }

    result.setData(items);
    return result;
  }

  @LogAround
  @RequestMapping(value = "/sidebars/review", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listReviewSidebarByRole() {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    List<String> items = new ArrayList<>();

    items.add("同事");

    CoreUserProfileDTO coreUserProfileDTO = facadeFactory.getUserFacade()
            .getReportorByUserIdAndOrgId(orgId, actorUserId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode() &&
        ServiceStatus.UP_USER_NOT_FOUND.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(coreUserProfileDTO.getServiceStatusDTO().getCode());
    }

    //If not found, no self review
    if (ServiceStatus.COMMON_OK.getCode() == coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      items.add("我自己");
    }

    if (RoleUtil.isHR(remoteResult.getRoleDTOList())) {
      items.add("评价管理");
    }

    result.setData(items);
    return result;
  }

}
