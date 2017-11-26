package hr.wozai.service.user.server.model.navigation;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/2
 */
@Data
@NoArgsConstructor
public class Navigation {
  public Long navigationId;

  public Long orgId;

  public Long userId;

  public Long naviOrgId;

  public Long naviUserId;

  public Integer naviModule;

  public Integer naviStep;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
