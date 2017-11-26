package hr.wozai.service.api.controller.auth;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.user.client.userorg.dto.UserInfoDTO;
import hr.wozai.service.user.client.userorg.dto.UuidInfoDTO;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.api.vo.user.UserAccountVO;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/27
 */
@Controller("authController")
public class AuthController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SignupController.class);

  private static final String HTTP_ENDPOINT_PREFIX = "u?uuid=";
  private static final String HTTP_ENDPOINT_SUFFIX = "#/find-password";

  private static final String BODY_PARAM_PASSWORD = "password";
  private static final long MILLS_ONE_HOUR = 3600 * 1000L;

  @Autowired
  FacadeFactory facadeFactory;

/*  @Autowired
  SqsProducer sqsProducer;*/

  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Value("${url.host}")
  private String host;


  @RequestMapping(value = "/auths/init-password",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result initPassword(
      @RequestBody JSONObject jsonObject
  ) {

    Result result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();
    String passwordPlainText = jsonObject.getString(BODY_PARAM_PASSWORD);

    try {
      VoidDTO rpcUpdateResult = facadeFactory.getUserFacade().initPassword(tempOrgId, tempUserId, passwordPlainText);
      ServiceStatus rpcUpdateStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcUpdateStatus);
    } catch (Exception e) {
      LOGGER.info("initPassword()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
//
//    facadeFactory.getUserFacade() facadeFactory.getUserFacade() = facadeFactory.getfacadeFactory.getUserFacade()();
//    Result<Object> result = new Result<>();
//
//    long orgId = AuthenticationInterceptor.tempOrgId.get();
//    long actorUserId = AuthenticationInterceptor.tempUserId.get();
//
//    String password = userAccountVO.getPassword();
//    BooleanDTO remoteResult = facadeFactory.getUserFacade().resetPasswordWhenOnboarding(orgId, actorUserId, password);
//    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
//    if (serviceStatus != ServiceStatus.COMMON_OK) {
//      throw new ServiceStatusException(serviceStatus);
//    }
//
//    result.setCodeAndMsg(serviceStatus);
//    result.setData(remoteResult.getData());
//
//    return result;
  }


  @RequestMapping(value = "/auths/reset-password", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> resetPassword(@RequestBody UserAccountVO userAccountVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.tempOrgId.get();
    long actorUserId = AuthenticationInterceptor.tempUserId.get();

    String password = userAccountVO.getPassword();
    VoidDTO remoteResult = facadeFactory.getUserFacade().resetPasswordWhenOnboarding(orgId, actorUserId, password);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/auths/logout", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> logout(HttpServletRequest request) {
    String accessTokenString = request.getHeader("X-Access-Token");
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    BooleanDTO remoteResult = facadeFactory.getTokenFacade().deleteAccessTokenWhenLogout(orgId,
            accessTokenString, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }


  @RequestMapping(value = "/auths/change-password", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> changePassword(@RequestBody JSONObject payload) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    String oldPassword = payload.getString(ParamName.PAYLOAD_PARAM_PASSWORD_OLD);
    String newPassword = payload.getString(ParamName.PAYLOAD_PARAM_PASSWORD_NEW);

    VoidDTO remoteResult = facadeFactory.getUserFacade().changePassword(orgId, actorUserId, oldPassword,
            newPassword, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }


    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;

  }

  @LogAround

  @RequestMapping(value = "/auths/find-password/send-url", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> sendUrlForFindPassword(@RequestBody JSONObject payload) {
    String email = payload.getString(ParamName.PAYLOAD_PARAM_EMAIL);

    if (!EmailUtils.isValidEmailAddressByApache(email)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    UserInfoDTO remoteResult = facadeFactory.getUserFacade().getUserInfoByEmail(email);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    long orgId = remoteResult.getCoreUserProfileDTO().getOrgId();
    long userId = remoteResult.getCoreUserProfileDTO().getUserId();
    String userName = remoteResult.getCoreUserProfileDTO().getFullName();
    String orgName = remoteResult.getOrgDTO().getShortName();

    UuidInfoDTO uuidInfoDTO = new UuidInfoDTO();
    uuidInfoDTO.setOrgId(orgId);
    uuidInfoDTO.setUserId(userId);
    uuidInfoDTO.setUuidUsage(UuidUsage.RESET_PWD.getCode());
    uuidInfoDTO.setExpireTime(TimeUtils.getNowTimestmapInMillis() + MILLS_ONE_HOUR * 48);
    uuidInfoDTO.setCreatedUserId(userId);
    UuidInfoDTO rpcResult = facadeFactory.getTokenFacade().addUUIDInfo(uuidInfoDTO, 0L, 0L);

    String url = host + HTTP_ENDPOINT_PREFIX + rpcResult.getUuid() + HTTP_ENDPOINT_SUFFIX;

    String emailJson = EmailTemplate.getResetPasswordEmailContent(EmailTemplate.RESET_PASSWORD,
            userName, url, orgName, email);
    rabbitMQProducer.sendMessage(emailJson);

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(url);

    return result;
  }


  @RequestMapping(value = "/auths/find-password/reset", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> findPasswordWithUuid(@RequestBody JSONObject payload) {
    long orgId = AuthenticationInterceptor.tempOrgId.get();
    long userId = AuthenticationInterceptor.tempUserId.get();

    String password = payload.getString(ParamName.PAYLOAD_PARAM_PASSWORD);

    VoidDTO remoteResult = facadeFactory.getUserFacade().resetPasswordWhenMissingPwd(orgId, userId, password);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }
}
