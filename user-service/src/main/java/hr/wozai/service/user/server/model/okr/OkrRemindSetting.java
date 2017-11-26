package hr.wozai.service.user.server.model.okr;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/10/9
 */
@Data
@NoArgsConstructor
public class OkrRemindSetting {
  private Long okrRemindSettingId;

  private Long orgId;

  private Integer remindType;

  private Integer frequency;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
