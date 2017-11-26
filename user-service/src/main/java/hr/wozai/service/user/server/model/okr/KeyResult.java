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
public class KeyResult {
  private Long keyResultId;

  private Long orgId;

  private String content;

  private Long objectiveId;

  private Integer priority;

  private Integer progressMetricType;

  private BigDecimal startingAmount;

  private BigDecimal goalAmount;

  private BigDecimal currentAmount;

  private String unit;

  private Long deadline;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
