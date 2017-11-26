// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.helper;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.userorg.enums.ConfigType;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.model.userorg.PickOption;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-01
 */
public class OrgPickOptionHelper {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldHelper.class);

  /**
   * Must satisfy requirements:
   *  1) each pickOption is valid itself
   *  2) each optionIndex equals to list index
   *  3) all configType should be same
   *  4) non-deprecated optionValues should be unique
   *
   * @param orgPickOptions
   */
  public static boolean isValidSequenceOfPickOption(List<OrgPickOption> orgPickOptions) {

    LOGGER.info("isValidSequenceOfPickOption(): orgPickOptions={}", orgPickOptions);

    if (CollectionUtils.isEmpty(orgPickOptions)) {
      return false;
    }

    int configType = 0;
    int nonDeprecatedOptionCount = 0;
    Set<String> optionValues = new HashSet<>();
    for (int i = 0; i < orgPickOptions.size(); i++) {
      OrgPickOption orgPickOption = orgPickOptions.get(i);
      // 1)
      if (null == orgPickOption.getOrgPickOptionId()) {
        if (!isValidAddPickOptionRequest(orgPickOption)) {
          LOGGER.error("isValidSequenceOfPickOption(): 1-{}", orgPickOption);
          return false;
        }
      } else {
        if (!isValidUpdatePickOptionRequest(orgPickOption)) {
          LOGGER.info("isValidSequenceOfPickOption(): 2-{}", orgPickOption);
          return false;
        }
      }
      // 2)
      if (orgPickOption.getOptionIndex() != i) {
        LOGGER.info("isValidSequenceOfPickOption(): 3-{}", orgPickOption);
        return false;
      }
      // 3)
      if (i == 0) {
        configType = orgPickOption.getConfigType();
      } else if (orgPickOption.getConfigType() != configType) {
        LOGGER.info("isValidSequenceOfPickOption(): 4-{}", orgPickOption);
        return false;
      }
      // 4)
      if (orgPickOption.getIsDeprecated() != 1) {
        optionValues.add(orgPickOption.getOptionValue());
        nonDeprecatedOptionCount ++;
      }
    }

    //3)
    if (optionValues.size() < nonDeprecatedOptionCount) {
      LOGGER.info("isValidSequenceOfPickOption(): 5-: {}");
      return false;
    }

    return true;
  }

  public static boolean isValidAddPickOptionRequest(OrgPickOption orgPickOption) {
    if (null == orgPickOption
        || null == orgPickOption.getOrgId()
        || (null == orgPickOption.getConfigType()
            || null == ConfigType.getEnumByCode(orgPickOption.getConfigType()))
        || !isValidOptionValue(orgPickOption.getOptionValue())
        || null == orgPickOption.getOptionIndex()
//        || null == orgPickOption.getIsDefault()
        || null == orgPickOption.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdatePickOptionRequest(OrgPickOption orgPickOption) {
    if (null == orgPickOption
        || null == orgPickOption.getOrgPickOptionId()
        || null == orgPickOption.getOrgId()
        || (null == orgPickOption.getConfigType()
            || null == ConfigType.getEnumByCode(orgPickOption.getConfigType()))
        || !isValidOptionValue(orgPickOption.getOptionValue())
        || null == orgPickOption.getOptionIndex()
//        || null == orgPickOption.getIsDefault()
        || null == orgPickOption.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static List<OrgPickOption> listOrgPickOptionToAddUponUpdate(List<OrgPickOption> newOrgPickOptions) {

    List<OrgPickOption> orgPickOptionsToAdd = new ArrayList<>();
    for (int i = 0; i < newOrgPickOptions.size(); i++) {
      OrgPickOption newOrgPickOption = newOrgPickOptions.get(i);
      if (null == newOrgPickOption.getOrgPickOptionId()) {
        orgPickOptionsToAdd.add(newOrgPickOption);
      }
    }

    return orgPickOptionsToAdd;
  }

  public static List<Long> listOrgPickOptionIdsToDeprecatedUponUpdate(
      List<OrgPickOption> currOrgPickOptions, List<OrgPickOption> newOrgPickOptions) {

    Set<Long> newOrgPickOptionIds = new HashSet<>();
    for (OrgPickOption newOrgPickOption: newOrgPickOptions) {
      if (null != newOrgPickOption.getOrgPickOptionId()) {
        newOrgPickOptionIds.add(newOrgPickOption.getOrgPickOptionId());
      }
    }

    List<Long> orgPickOptionIdsToDeprecate = new ArrayList<>();
    for (int i = 0; i < currOrgPickOptions.size(); i++) {
      OrgPickOption currOrgPickOption = currOrgPickOptions.get(i);
      if (currOrgPickOption.getIsDeprecated() == 0
          && !newOrgPickOptionIds.contains(currOrgPickOption.getOrgPickOptionId())) {
        orgPickOptionIdsToDeprecate.add(currOrgPickOption.getOrgPickOptionId());
      }
    }

    return orgPickOptionIdsToDeprecate;
  }

  public static List<OrgPickOption> listOrgPickOptionToUpdateUponUpdate(
      List<OrgPickOption> currOrgPickOptions, List<OrgPickOption> newOrgPickOptions) {


    Set<Long> newPickOptionIds = new HashSet<>();
    for (OrgPickOption newOrgPickOption: newOrgPickOptions) {
      if (null != newOrgPickOption.getOrgPickOptionId()) {
        newPickOptionIds.add(newOrgPickOption.getOrgPickOptionId());
      }
    }

    List<OrgPickOption> orgPickOptionsToUpdate = new ArrayList<>();
    for (int i = 0; i < currOrgPickOptions.size(); i++) {
      OrgPickOption currOrgPickOption = currOrgPickOptions.get(i);
      if (currOrgPickOption.getIsDeprecated() == 0
          && newPickOptionIds.contains(currOrgPickOption.getOrgPickOptionId())) {
        orgPickOptionsToUpdate.add(currOrgPickOption);
      }
    }

    return orgPickOptionsToUpdate;
  }

  private static boolean isValidOptionValue(String optionValue) {
    if (StringUtils.isNullOrEmpty(optionValue)
        || optionValue.contains("/")) {
      return false;
    }
    return true;
  }

}
