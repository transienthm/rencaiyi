// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.user.server.model.userorg.UserEmployment;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-19
 */
public class UserEmploymentHelper {

  public static boolean isValidAddUserEmploymentRequest(UserEmployment userEmployment) {
    if (null == userEmployment
        || null == userEmployment.getOrgId()
        || null == userEmployment.getUserId()
        || (null == userEmployment.getUserStatus()
            || null == UserStatus.getEnumByCode(userEmployment.getUserStatus()))
        || (null == userEmployment.getOnboardingStatus()
            || null == OnboardingStatus.getEnumByCode(userEmployment.getOnboardingStatus()))
        || (null != userEmployment.getContractType()
            && null == ContractType.getEnumByCode(userEmployment.getContractType()))
        || (null != userEmployment.getEmploymentStatus()
            && null == EmploymentStatus.getEnumByCode(userEmployment.getEmploymentStatus()))
        || null == userEmployment.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateUserEmploymentRequest(UserEmployment userEmployment) {
    if (null == userEmployment
        || null == userEmployment.getOrgId()
        || null == userEmployment.getUserId()
        || (null != userEmployment.getUserStatus()
            && null == UserStatus.getEnumByCode(userEmployment.getUserStatus()))
        || (null != userEmployment.getOnboardingStatus()
            && null == OnboardingStatus.getEnumByCode(userEmployment.getOnboardingStatus()))
        || (null != userEmployment.getContractType()
            && null == ContractType.getEnumByCode(userEmployment.getContractType()))
        || (null != userEmployment.getEmploymentStatus()
            && null == EmploymentStatus.getEnumByCode(userEmployment.getEmploymentStatus()))
        || null == userEmployment.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static void setContractTypeAndEnrollDateAndResignDate(
      UserEmployment userEmployment, int contractType, Long enrollDate, Long resignDate) {
    if (null == userEmployment
        || null == ContractType.getEnumByCode(contractType)) {
      return;
    }
    userEmployment.setContractType(contractType);
    if (ContractType.INTERNSHIP.getCode() == contractType) {
      userEmployment.setInternshipEnrollDate(enrollDate);
      userEmployment.setInternshipResignDate(resignDate);
    } else if (ContractType.FULLTIME.getCode() == contractType) {
      userEmployment.setFulltimeEnrollDate(enrollDate);
      userEmployment.setFulltimeResignDate(resignDate);
    } else if (ContractType.PARTTIME.getCode() == contractType) {
      userEmployment.setParttimeEnrollDate(enrollDate);
      userEmployment.setParttimeResignDate(resignDate);
    }
  }

  public static Long getEnrollDate(UserEmployment userEmployment) {
    if (null == userEmployment.getContractType()) {
      return null;
    }
    int contractType = userEmployment.getContractType();
    Long enrollDate = null;
    if (contractType == ContractType.INTERNSHIP.getCode()) {
      enrollDate = userEmployment.getInternshipEnrollDate();
    } else if (contractType == ContractType.FULLTIME.getCode()) {
      enrollDate = userEmployment.getFulltimeEnrollDate();
    } else {
      enrollDate = userEmployment.getParttimeEnrollDate();
    }
    return enrollDate;
  }

  public static boolean isValidContractTypeAndEnrollDateAndResignDate(UserEmployment userEmployment) {
    if (null == userEmployment
        || null == ContractType.getEnumByCode(userEmployment.getContractType())) {
      return false;
    }
    if (ContractType.INTERNSHIP.getCode() == userEmployment.getContractType().intValue()) {
      if (null == userEmployment.getInternshipEnrollDate()) {
        return false;
      }
      if (null != userEmployment.getInternshipResignDate()
          && userEmployment.getInternshipResignDate() < userEmployment.getInternshipEnrollDate()) {
        return false;
      }
    } else if (ContractType.FULLTIME.getCode() == userEmployment.getContractType().intValue()) {
      if (null == userEmployment.getFulltimeEnrollDate()) {
        return false;
      }
      if (null != userEmployment.getFulltimeResignDate()
          && userEmployment.getFulltimeResignDate() < userEmployment.getFulltimeEnrollDate()) {
        return false;
      }
    } else if (ContractType.PARTTIME.getCode() == userEmployment.getContractType().intValue()) {
      if (null == userEmployment.getParttimeEnrollDate()) {
        return false;
      }
      if (null != userEmployment.getParttimeResignDate()
          && userEmployment.getParttimeResignDate() < userEmployment.getParttimeEnrollDate()) {
        return false;
      }
    }

    return true;
  }

}
