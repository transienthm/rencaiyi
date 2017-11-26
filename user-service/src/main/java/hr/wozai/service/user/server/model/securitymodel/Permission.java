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
public class Permission {
  private Long permissionId;

  private String resourceName;

  private String resourceCode;

  private Integer resourceType;

  private Integer actionCode;

  private Integer scope;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private  Integer isDelete;
}
