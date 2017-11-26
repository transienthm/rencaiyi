// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.UserNameListDTO;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016/3/2
 */
@Controller("userNameController")
public class UserNameController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserNameController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/usernames/search", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getUserListByKeyword(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                             @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "20")int pageSize,
                                             @RequestParam(value = "type", required = false, defaultValue = "1") int type) {
    Result<Object> result = new Result<>();

    Map<String, Object> map = new HashMap<>();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    UserNameListDTO remoteResult = facadeFactory.getCommonToolFacade().searchUsersByKeywordAndType(orgId, keyword, type,
            pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<Long> userIdList = remoteResult.getIdList();
    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();

    if (userIdList.isEmpty()) {
      map.put("UserProfiles", coreUserProfileVOs);
      map.put("totalRecordNum", remoteResult.getTotalRecordNum());
      result.setData(map);
      return result;
    }

    CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade().
            listCoreUserProfile(orgId, userIdList, actorUserId, adminUserId);
    List<CoreUserProfileDTO> coreUserProfileDTOs = coreUserProfileListDTO.getCoreUserProfileDTOs();

    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
      coreUserProfileVOs.add(coreUserProfileVO);
    }

    map.put("UserProfiles", coreUserProfileVOs);
    map.put("totalRecordNum", remoteResult.getTotalRecordNum());

    result.setData(map);

    return result;
  }
}
