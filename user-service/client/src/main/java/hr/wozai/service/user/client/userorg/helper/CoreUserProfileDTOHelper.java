// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.client.userorg.helper;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-18
 */
public class CoreUserProfileDTOHelper {

//  public static List<CoreUserProfileDTO> removeSuperAdminFromList(List<CoreUserProfileDTO> coreUserProfileDTOs) {
//    if (CollectionUtils.isEmpty(coreUserProfileDTOs)) {
//      return coreUserProfileDTOs;
//    }
//    List<CoreUserProfileDTO> coreUserProfileDTOsWithoutSuperAdmin = new ArrayList<>();
//    for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
//      if (null != coreUserProfileDTO.getUserEmploymentDTO()
//          && UserStatus.SUPER_ADMIN.getCode() != coreUserProfileDTO.getUserEmploymentDTO().getUserStatus()) {
//        coreUserProfileDTOsWithoutSuperAdmin.add(coreUserProfileDTO);
//      }
//    }
//    return coreUserProfileDTOsWithoutSuperAdmin;
//  }

}
