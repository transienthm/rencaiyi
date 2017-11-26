package hr.wozai.service.review.server.utils;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.ReviewActivityUserDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.Collator;
import java.util.Comparator;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-15
 */
@Component
public class ReviewActivityNameSortUtils implements Comparator<ReviewActivityUserDTO> {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityNameSortUtils.class);

  @Autowired
  @Qualifier("userProfileFacadeProxy")
  private ThriftClientProxy userProfileFacadeProxy;

  private UserProfileFacade userProfileFacade;

  @PostConstruct
  public void init() throws Exception {
    userProfileFacade = (UserProfileFacade)userProfileFacadeProxy.getObject();
  }

  Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

  @Override
  public int compare(ReviewActivityUserDTO activityUserDTO1, ReviewActivityUserDTO activityUserDTO2) {

    ReviewInvitationDTO managerInvitation1 = activityUserDTO1.getManagerInvitationDTO();

    ReviewInvitationDTO managerInvitation2 = activityUserDTO2.getManagerInvitationDTO();

    if(null == managerInvitation1 && null == managerInvitation2) {
      return 0;
    }
    if(null == managerInvitation1) {
      return +1;
    }
    if(null == managerInvitation2) {
      return -1;
    }

    /*long orgId1 = managerInvitation1.getOrgId();
    long managerId1 = managerInvitation1.getReviewerId();
    CoreUserProfileDTO coreUserProfileDTO1 = getCoreUserProfileVO(orgId1, managerId1);

    long orgId2 = managerInvitation2.getOrgId();
    long managerId2 = managerInvitation2.getReviewerId();
    CoreUserProfileDTO coreUserProfileDTO2 = getCoreUserProfileVO(orgId2, managerId2);*/

    return cmp.compare(activityUserDTO1.getManagerFullName(), activityUserDTO2.getManagerFullName());
  }

//  private CoreUserProfileDTO getCoreUserProfileVO(long orgId, long userId) {
//
//    CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(orgId, userId,
//            0L, 0L);
//    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode()) {
//      LOGGER.error("getCoreUserProfileVO error: " + orgId + ":" + userId);
//      throw new ServiceStatusException(coreUserProfileDTO.getServiceStatusDTO().getCode());
//    }
//    return coreUserProfileDTO;
//  }
}
