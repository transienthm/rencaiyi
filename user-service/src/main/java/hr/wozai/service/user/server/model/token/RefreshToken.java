package hr.wozai.service.user.server.model.token;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/28
 */
@Data
@NoArgsConstructor
public class RefreshToken {
  private Long refreshTokenId;

  private String refreshTokenKey;

  private String refreshTokenValue;

  private Integer isDeleted;
}
