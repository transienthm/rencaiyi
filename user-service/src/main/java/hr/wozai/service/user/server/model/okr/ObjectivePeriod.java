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
public class ObjectivePeriod {
  private Long objectivePeriodId;

  private Long orgId;

  private Integer type;

  private Long teamId;

  private Long ownerId;

  private Integer periodTimeSpanId;

  private Integer year;

  private String name;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
