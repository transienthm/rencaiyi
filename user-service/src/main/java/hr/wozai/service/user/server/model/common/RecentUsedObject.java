package hr.wozai.service.user.server.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/21
 */
@Data
@NoArgsConstructor
public class RecentUsedObject {
  private Long recentUsedObjectId;

  private Long orgId;

  private Long userId;

  private Integer type;

  private List<String> usedObjectId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
