// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.onboarding.dto.OnboardingRequestDTO;
import hr.wozai.service.user.server.model.userorg.Org;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-10
 */
public class OrgAccountHelper {

  public static boolean isValidAddOrgRequest(Org org) {
    if (null == org
        || null == org.getFullName()
        || null == org.getShortName()
        || null == org.getTimeZone()) {
      return false;
    }
    return true;
  }

  public static boolean isValidAddFirstUserRequest(Map<String, String> userFieldValues) {
    if (null == userFieldValues
        || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME))
        || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME))
        || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.MOBILE_PHONE_REF_NAME))) {
//        || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.CITIZEN_ID_REF_NAME))) {
      return false;
    }
    return true;
  }

  public static boolean isValidBatchAddStaffRequest( List<Map<String, String>> rawFieldList) {

    if (CollectionUtils.isEmpty(rawFieldList)) {
      return false;
    }

    Set<String> emailAddresses = new HashSet<>();
    for (Map<String, String> userFieldValues: rawFieldList) {
      if (null == userFieldValues
          || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME))
          || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME))
          || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.MOBILE_PHONE_REF_NAME))) {
//          || StringUtils.isNullOrEmpty(userFieldValues.get(SystemFieldConsts.CITIZEN_ID_REF_NAME))) {
        return false;
      }
      emailAddresses.add(userFieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME));
    }

    if (emailAddresses.size() < rawFieldList.size()) {
      return false;
    }

    return true;
  }

  public static List<OnboardingRequestDTO> convertToOnboardingRequestDTOs(List<Map<String, String>> rawFieldList) {

    if (CollectionUtils.isEmpty(rawFieldList)) {
      return Collections.EMPTY_LIST;
    }

    List<OnboardingRequestDTO> onboardingRequestDTOs = new ArrayList<>();
    for (Map<String, String> userFieldValues : rawFieldList) {
      OnboardingRequestDTO onboardingRequestDTO = new OnboardingRequestDTO();
      onboardingRequestDTO.setFullName(userFieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME));
      onboardingRequestDTO.setEmailAddress(userFieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME));
      onboardingRequestDTO.setMobilePhone(userFieldValues.get(SystemFieldConsts.MOBILE_PHONE_REF_NAME));
//      onboardingRequestDTO.setCitizenId(userFieldValues.get(SystemFieldConsts.CITIZEN_ID_REF_NAME));
      onboardingRequestDTOs.add(onboardingRequestDTO);
    }

    return onboardingRequestDTOs;
  }

}
