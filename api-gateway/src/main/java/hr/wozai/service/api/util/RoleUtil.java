package hr.wozai.service.api.util;

import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/6
 */
public class RoleUtil {
  public static boolean isHR(List<RoleDTO> roleDTOList) {
    String hr = "HR";
    for (RoleDTO roleDTO : roleDTOList) {
      if (roleDTO.getRoleName().equals(hr)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isOrgAdmin(List<RoleDTO> roleDTOList) {
    String orgAdmin = "orgAdmin";
    for (RoleDTO roleDTO : roleDTOList) {
      if (roleDTO.getRoleName().equals(orgAdmin)) {
        return true;
      }
    }
    return false;
  }
}
