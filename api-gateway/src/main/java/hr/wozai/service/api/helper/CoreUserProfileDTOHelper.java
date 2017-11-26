// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.helper;

import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.SimpleUserProfileVO;
import hr.wozai.service.api.vo.user.UserEmploymentVO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-20
 */
public class CoreUserProfileDTOHelper {

  public static Map<Long, CoreUserProfileDTO> convertCUPListToMap(List<CoreUserProfileDTO> coreUserProfileDTOs) {
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
      for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
        coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
      }
      return coreUserProfileDTOMap;
    }
    return null;
  }

  public static Map<Long, CoreUserProfileVO> convertDTOListToVOMap(List<CoreUserProfileDTO> coreUserProfileDTOs) {
    Map<Long, CoreUserProfileVO> coreUserProfileVOMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
        CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
        BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
        coreUserProfileVOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileVO);
      }
    }
    return coreUserProfileVOMap;
  }

  public static List<CoreUserProfileVO> convertCoreUserProfileDTOsToVOs(List<CoreUserProfileDTO> coreUserProfileDTOs) {
    List<CoreUserProfileVO> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
        CoreUserProfileVO coreUserProfileVO = convertCoreUserProfileDTOToVO(coreUserProfileDTO);
        result.add(coreUserProfileVO);
      }
    }

    return result;
  }

  public static List<SimpleUserProfileVO> convertCoreUserProfileDTOsToSimpleVOs
      (List<CoreUserProfileDTO> coreUserProfileDTOs) {
    List<SimpleUserProfileVO> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
        SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
        BeanUtils.copyProperties(coreUserProfileDTO, simpleUserProfileVO);
        result.add(simpleUserProfileVO);
      }
    }

    return result;
  }

  public static CoreUserProfileVO convertCoreUserProfileDTOToVO(CoreUserProfileDTO coreUserProfileDTO) {
    CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
    BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);

    if (coreUserProfileDTO.getTeamMemberDTO() != null) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(coreUserProfileDTO.getTeamMemberDTO(), teamVO);
      coreUserProfileVO.setTeamVO(teamVO);
    }

    if (coreUserProfileDTO.getUserEmploymentDTO() != null) {
      UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
      BeanUtils.copyProperties(coreUserProfileDTO.getUserEmploymentDTO(), userEmploymentVO);
      coreUserProfileVO.setUserEmploymentVO(userEmploymentVO);
    }

    return coreUserProfileVO;
  }

}
