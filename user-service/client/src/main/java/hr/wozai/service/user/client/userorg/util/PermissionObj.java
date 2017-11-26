package hr.wozai.service.user.client.userorg.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/27
 */
@Data
@NoArgsConstructor
public class PermissionObj {
  private Long id;

  private Integer resourceType;

  private Long ownerId;

  private boolean hasPermission;
}
