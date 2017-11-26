package hr.wozai.service.user.server.model.okr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/4
 */
@Data
@NoArgsConstructor
public class Objective {
  private Long objectiveId;

  private Long orgId;

  private Long parentObjectiveId;

  private Integer type;

  private Long ownerId;

  private String content;

  private Integer priority;

  private Long objectivePeriodId;

  private Integer isAutoCalc;

  private Integer progressMetricType;

  private BigDecimal startingAmount;

  private BigDecimal goalAmount;

  private BigDecimal currentAmount;

  private String unit;

  private Long deadline;

  private Integer orderIndex;

  private Integer isPrivate;

  private Integer regularRemindType;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
