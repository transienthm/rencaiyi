package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/26
 */
@Component
public class OkrUtils {

  @Autowired
  FacadeFactory facadeFactory;

  public long getDecryptValueFromString(String encryptValue) {
    long decryptValue = -1;
    try {
      decryptValue = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptValue));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return decryptValue;
  }

  public List<CoreUserProfileVO> fillAtUsers(List<String> atList, long orgId, long actorUserId, long adminUserId) {

    List<CoreUserProfileVO> result = new ArrayList<>();
    for (String userIdStr : atList) {
      if (userIdStr.isEmpty()) {
        continue;
      }
      Long userId = Long.parseLong(userIdStr);
      CoreUserProfileVO coreUserProfileVO = getCoreUserProfileVO(orgId, userId, actorUserId, adminUserId);
      result.add(coreUserProfileVO);
    }

    return result;
  }

  public CoreUserProfileVO getCoreUserProfileVO(long orgId, long userId, long actorUserId, long adminUserId) {
    CoreUserProfileDTO coreUserProfileDTO = facadeFactory.getUserProfileFacade().getCoreUserProfile(orgId, userId, actorUserId, adminUserId);

    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(coreUserProfileDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
    BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
    return coreUserProfileVO;
  }

  public void getUserIdString(List<CoreUserProfileVO> coreUserProfileVOs, List<String> result) {
    for (CoreUserProfileVO coreUserProfileVO : coreUserProfileVOs) {
      long userId = coreUserProfileVO.getUserId();
      result.add(String.valueOf(userId));
    }
  }
}
