package hr.wozai.service.user.server.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/15
 */
@Data
@NoArgsConstructor
public class RemindSetting {
  private Long remindSettingId;

  private Long orgId;

  private Long userId;

  private Integer remindType;

  private Integer status;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
