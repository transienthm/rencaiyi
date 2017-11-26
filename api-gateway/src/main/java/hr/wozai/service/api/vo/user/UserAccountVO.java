package hr.wozai.service.api.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/27
 */
@Data
@NoArgsConstructor
public class UserAccountVO {
  private String emailAddress;

  private String password;
}
