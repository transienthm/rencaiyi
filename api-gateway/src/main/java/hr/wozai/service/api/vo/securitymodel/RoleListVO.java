package hr.wozai.service.api.vo.securitymodel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/3
 */
@Data
@NoArgsConstructor
public class RoleListVO {
  private List<RoleVO> roles;
}
