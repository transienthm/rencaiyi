// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.helper;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.user.client.userorg.dto.UserEmploymentDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-07
 */
public class UserEmploymentDTOHelper {

  public static Long getEnrollDate(UserEmploymentDTO userEmployment) {
    if (null == userEmployment
        || null == userEmployment.getContractType()) {
      return null;
    }
    int contractType = userEmployment.getContractType();
    Long enrollDate = 0L;
    if (contractType == ContractType.INTERNSHIP.getCode()) {
      enrollDate = userEmployment.getInternshipEnrollDate();
    } else if (contractType == ContractType.FULLTIME.getCode()) {
      enrollDate = userEmployment.getFulltimeEnrollDate();
    } else {
      enrollDate = userEmployment.getParttimeEnrollDate();
    }
    return enrollDate;
  }

}
