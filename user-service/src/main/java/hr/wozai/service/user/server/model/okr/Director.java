package hr.wozai.service.user.server.model.okr;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/4
 */
@Data
@NoArgsConstructor
public class Director {
  private Long directorId;

  private Long orgId;

  private Long userId;

  // 1:objective 2:key result
  private Integer type;

  private Long objectId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
