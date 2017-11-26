package hr.wozai.service.user.server.model.token;

import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/14
 */
@Data
@NoArgsConstructor
public class UuidInfo {
  private Long uuidInfoId;

  private Long orgId;

  private Long userId;

  private String uuid;

  private Integer uuidUsage;

  private Long expireTime;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  public void setUUIDValue() throws Exception {
    this.uuid = EncryptUtils.symmetricEncrypt(userId.toString()) + "_" + UUID.randomUUID();
  }
}
