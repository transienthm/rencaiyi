// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;

import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-20
 */
public class CoreUserProfileHelper {

  public static Map<Long, CoreUserProfile> convertCoreUserProfileListToMap(List<CoreUserProfile> coreUserProfiles) {
    Map<Long, CoreUserProfile> coreUserProfileMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(coreUserProfiles)) {
      coreUserProfileMap = new HashMap<>();
      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
        coreUserProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
      }
    }
    return coreUserProfileMap;
  }

  public static Map<Long, CoreUserProfileDTO> convertCoreUserProfileDTOListToMap(
      List<CoreUserProfileDTO> coreUserProfileDTOs) {
    Map<Long, CoreUserProfileDTO> coreUserProfileMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      coreUserProfileMap = new HashMap<>();
      for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
        coreUserProfileMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
      }
    }
    return coreUserProfileMap;
  }
}
