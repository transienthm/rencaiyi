package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/12
 */
@Data
@NoArgsConstructor
public class LoginResult {
  private boolean loginSuccess;

  private boolean needCaptcha;
}
