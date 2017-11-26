package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/2
 */
@Data
@NoArgsConstructor
public class ReportLine {
  private Long reportLineId;

  private Long orgId;

  private Long userId;

  private Long reportUserId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;


}
