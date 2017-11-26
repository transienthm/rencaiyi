// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.component.FeZkClient;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.vo.user.OnboardingUrlVO;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.PasswordUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;

import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.CookieUtils;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.util.ExternalUrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2015-1-5
 */
@Controller("loginController")
public class LoginController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);


  @Value("${url.host}")
  String host;

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  FeZkClient feZkClient;


  @RequestMapping(value = "/auths/login", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> loginByEmail(@RequestBody JSONObject payload, HttpServletResponse response) {
    Result<Object> result = new Result<>();

    String email = payload.getString(ParamName.PAYLOAD_PARAM_EMAIL);
    String password = payload.getString(ParamName.PAYLOAD_PARAM_PASSWORD);
    Integer remember_me = payload.getInteger(ParamName.PAYLOAD_PARAM_LOGIN_REMEMBER_ME);
    Long timestamp = payload.getLong(ParamName.PAYLOAD_PARAM_CAPTCHA_TIMESTAMP);
    String captcha = payload.getString(ParamName.PAYLOAD_PARAM_CAPTCHA_TEXT);
    boolean allowedRememberMe = (remember_me.intValue() == 1) ? true : false;

    LOGGER.info("loginByMobile()-request: mobilePhone=" + email
            + ", rememberMe=" + remember_me
            + ", captcha=" + captcha
    );

    // check param
    if (!PasswordUtils.isValidPassword(password)
            || !EmailUtils.isValidEmailAddressByApache(email)) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
    }
    boolean captchaSuccess = false;
    // check captcha
    if (null != timestamp && !StringUtils.isNullOrEmpty(captcha)) {
      BooleanDTO captchaResult = facadeFactory.getCaptchaFacade().verifyCaptcha(timestamp, captcha);
      if (!captchaResult.getData()) {
        throw new ServiceStatusException(ServiceStatus.AS_INVALID_CAPTCHA_VERIFICATION);
      }
      captchaSuccess = true;
    }

    // verify mobile and password
    VoidDTO remoteResult = facadeFactory.getUserFacade().loginWithEmail(email, password, captchaSuccess);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    // add token to cookie
    UserAccountDTO userAccountDTO = facadeFactory.getUserFacade().getUserAccountByEmail(email, 0L, 0L);
    long userId = userAccountDTO.getUserId();

    LongDTO orgIdDTO = facadeFactory.getUserFacade().getOrgIdByUserId(userId, userId);

    CoreUserProfileDTO coreUserProfileDTO = facadeFactory.getUserProfileFacade()
            .getCoreUserProfile(orgIdDTO.getData(), userId, -1L, -1L);
    if (ServiceStatus.getEnumByCode(coreUserProfileDTO.getServiceStatusDTO().getCode()) != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(ServiceStatus.getEnumByCode(coreUserProfileDTO.getServiceStatusDTO().getCode()));
    }
    // ???
    if (null != coreUserProfileDTO.getUserEmploymentDTO() &&
            OnboardingStatus.ONBOARDING.getCode() == coreUserProfileDTO.getUserEmploymentDTO().getOnboardingStatus()) {
        UuidInfoListDTO uuidInfoListDTO = facadeFactory.getTokenFacade().listUUIDInfosByUserIdAndUsage(
                orgIdDTO.getData(), userId, UuidUsage.ONBOARDING.getCode(),
                System.currentTimeMillis(), -1L, -1L);
      serviceStatus = ServiceStatus.getEnumByCode(uuidInfoListDTO.getServiceStatusDTO().getCode());
      if (serviceStatus == ServiceStatus.COMMON_OK && !CollectionUtils.isEmpty(uuidInfoListDTO.getUuidInfoDTOList())) {
        int size = uuidInfoListDTO.getUuidInfoDTOList().size();
        UuidInfoDTO uuidInfoDTO = uuidInfoListDTO.getUuidInfoDTOList().get(size - 1);
        String invitationUrl = ExternalUrlUtils.generateInvitationUrlOfOnboardingFlowForStaff(host, uuidInfoDTO.getUuid());
        OnboardingUrlVO onboardingUrlVO = new OnboardingUrlVO();
        onboardingUrlVO.setOnboardingUrl(invitationUrl);
        result.setCodeAndMsg(serviceStatus);
        result.setData(onboardingUrlVO);
      } else {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }
    } else {
      TokenPairDTO tokenPairDTO = facadeFactory.getTokenFacade().getTokenPairByUserIdAndOrgId(
              orgIdDTO.getData(), allowedRememberMe, userId, 0L);

      if (tokenPairDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(tokenPairDTO.getServiceStatusDTO().getCode()));
      }
      if (!isNullOrIncompleteTokenPairDTO(tokenPairDTO)) {
        CookieUtils.setTokenPairInResponseCookie(response, tokenPairDTO);
      }
      result.setCodeAndMsg(ServiceStatus.COMMON_OK);
      result.setData(tokenPairDTO);
    }

    return result;
  }


  @RequestMapping(value = "/auths/verify-password", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> loginByEmail(@RequestBody JSONObject payload) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    String password = payload.getString(ParamName.PAYLOAD_PARAM_PASSWORD);

    BooleanDTO remoteResult = facadeFactory.getUserFacade().verifyUserAccountWithPassword(
            orgId, actorUserId, password, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(remoteResult.getData());

    return result;
  }

  private boolean isNullOrIncompleteTokenPairDTO(TokenPairDTO tokenPairDTO) {
    return (null == tokenPairDTO
            || StringUtils.isNullOrEmpty(tokenPairDTO.getAccessToken())
            || StringUtils.isNullOrEmpty(tokenPairDTO.getRefreshToken()));
  }


  @RequestMapping(value = "/auths/need-verify", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> needVerify(@RequestBody JSONObject payload) {
    Result<Object> result = new Result<>();
    String mobilePhone = payload.getString(ParamName.PAYLOAD_PARAM_MOBILE_NUMBER);

    UserAccountDTO userAccountDTO = facadeFactory.getUserFacade().getUserAccountByEmail(mobilePhone, 0L, 0L);
    String extend = userAccountDTO.getExtend();
    JSONObject jsonObject = JSON.parseObject(extend);
    boolean needVerify = false;
    if (jsonObject != null
            && jsonObject.containsKey(ParamName.LOGIN_FAIL_TIME)
            && jsonObject.getIntValue(ParamName.LOGIN_FAIL_TIME) >= 7) {
      needVerify = true;
    }
    result.setData(needVerify);
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    return result;
  }

}
