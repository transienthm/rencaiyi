package hr.wozai.service.user.server.model.securitymodel;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/22
 */
@Data
@NoArgsConstructor
public class UserPermission {
  private Long userPermissionId;

  private Long orgId;

  private Long userId;

  private Long permissionId;

  private Integer status;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private Integer isDeleted;
}
