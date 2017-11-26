// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.helper;

import org.springframework.beans.BeanUtils;

import hr.wozai.service.api.vo.user.UserEmploymentSimpleVO;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.user.client.userorg.dto.UserEmploymentDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-18
 */
public class UserEmploymentHelper {

  public static void copyPropertiesFromDTOToVO(
      UserEmploymentDTO userEmploymentDTO, UserEmploymentSimpleVO userEmploymentSimpleVO) {

    if (null == userEmploymentDTO
        || null == userEmploymentSimpleVO) {
      return;
    }

    BeanUtils.copyProperties(userEmploymentDTO, userEmploymentSimpleVO);

    if (ContractType.FULLTIME.getCode() == userEmploymentDTO.getContractType()) {
      userEmploymentSimpleVO.setEnrollDate(userEmploymentDTO.getFulltimeEnrollDate());
      userEmploymentSimpleVO.setResignDate(userEmploymentDTO.getFulltimeResignDate());
    } else if (ContractType.PARTTIME.getCode() == userEmploymentDTO.getContractType()) {
      userEmploymentSimpleVO.setEnrollDate(userEmploymentDTO.getParttimeEnrollDate());
      userEmploymentSimpleVO.setResignDate(userEmploymentDTO.getParttimeResignDate());
    } else {
      userEmploymentSimpleVO.setEnrollDate(userEmploymentDTO.getInternshipEnrollDate());
      userEmploymentSimpleVO.setResignDate(userEmploymentDTO.getInternshipResignDate());
    }

  }

}
