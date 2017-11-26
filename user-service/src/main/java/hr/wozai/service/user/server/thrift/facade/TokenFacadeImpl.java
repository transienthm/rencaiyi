// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.userorg.dto.UuidInfoDTO;
import hr.wozai.service.user.client.userorg.dto.UuidInfoListDTO;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.model.token.UuidInfo;
import hr.wozai.service.user.server.service.NavigationService;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.client.userorg.facade.TokenFacade;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("tokenFacade")
public class TokenFacadeImpl implements TokenFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenFacadeImpl.class);

  @Autowired
  TokenService tokenService;

  @Autowired
  NavigationService navigationService;

  @Override
  public TokenPairDTO getTokenPairByUserIdAndOrgId(long orgId, boolean allowedRememberMe, long actorUserId,
                                                   long adminUserId) {

    LOGGER.info("getTokenPairByMobileAndPassword()-request: userId=" + actorUserId
                + ", orgId=" + orgId
                + ", adminUserId=" + adminUserId
                + ", allowedRememberMe=" + allowedRememberMe);

    TokenPairDTO result = new TokenPairDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      /*Navigation navigation = navigationService.findNavigationByOrgIdAndUserId(orgId, actorUserId);
      TokenPairDTO tokenPair;
      if (navigation != null) {
        tokenPair = tokenService.addAccessTokenAndRefreshToken(
                navigation.getNaviOrgId(), allowedRememberMe, navigation.getNaviUserId(), adminUserId);
      } else {
        tokenPair = tokenService.addAccessTokenAndRefreshToken(orgId, allowedRememberMe, actorUserId, adminUserId);
      }*/
      TokenPairDTO tokenPair = tokenService.addAccessTokenAndRefreshToken(orgId, allowedRememberMe, actorUserId, adminUserId);
      result.setAccessToken(tokenPair.getAccessToken());
      result.setRefreshToken(tokenPair.getRefreshToken());
    } catch (Exception e) {
      LOGGER.error("getTokenPairByMobileAndPassword()-fail: userId=" + actorUserId
                   + ", allowedRememberMe=" + allowedRememberMe, e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  public StringDTO refreshAccessToken(TokenPairDTO tokenPairDTO, long actorUserId, long adminUserId) {

    LOGGER.info("refreshAccessToken()-request: tokenPairDTO= " + tokenPairDTO.toString());

    StringDTO result = new StringDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      String newAccessToekn = tokenService.refreshAccessToken(tokenPairDTO.getAccessToken(),
              tokenPairDTO.getRefreshToken());

      result.setData(newAccessToekn);
    } catch (Exception e) {
      LOGGER.error("refreshAccessToken()-fail: tokenPairDTO= " + tokenPairDTO.toString(), e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  public BooleanDTO deleteAccessTokenWhenLogout(long orgId, String accessToken, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      tokenService.deleteAccessToken(accessToken, actorUserId);
      result.setData(true);
    } catch (Exception e) {
      LOGGER.error("deleteTokenPairWhenLogout-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  public UuidInfoDTO addUUIDInfo(UuidInfoDTO uuidInfoDTO, long actorUserId, long adminUserId) {
    UuidInfoDTO result = new UuidInfoDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UuidInfo uuidInfo = new UuidInfo();
      BeanUtils.copyProperties(uuidInfoDTO, uuidInfo);
      uuidInfo.setUUIDValue();

      UuidInfo u = tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      BeanUtils.copyProperties(u, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  public UuidInfoListDTO listUUIDInfosByUserIdAndUsage(long orgId, long userId, int uuidUsage, long expireTime, long actorUserId, long adminUserId) {
    UuidInfoListDTO result = new UuidInfoListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<UuidInfo> uuidInfoList = tokenService.listUuidInfosByUserIdAndUsage(orgId, userId, uuidUsage, expireTime);
      List<UuidInfoDTO> uuidInfoDTOs = new ArrayList<>();
      for (UuidInfo uuidInfo : uuidInfoList) {
        UuidInfoDTO uuidInfoDTO = new UuidInfoDTO();
        BeanUtils.copyProperties(uuidInfo, uuidInfoDTO);
        uuidInfoDTOs.add(uuidInfoDTO);
      }
      result.setUuidInfoDTOList(uuidInfoDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  public StringDTO getTemporaryTokenByUUID(String uuid) {
    StringDTO result = new StringDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      String temporaryToken = tokenService.getTemporaryTokenByUuid(uuid);

      result.setData(temporaryToken);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  public VoidDTO deleteAllUUIDAndTemporaryToken(long orgId, long userId, int uuidUsage,
                                                long actorUserId, long adminUserId) {
    LOGGER.info("deleteAllUUIDAndTemporaryToken()-request:actorUserId:{},adminUserId:{}",
            actorUserId, adminUserId);
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      tokenService.deleteTemporaryToken(orgId, userId, uuidUsage);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

}
