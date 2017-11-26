package hr.wozai.service.user.server.model.okr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/15
 */
@Data
@NoArgsConstructor
public class OkrUpdateLog {
  private Long okrUpdateLogId;

  private Long orgId;

  private Long okrCommentId;

  private String attribute;

  private String beforeValue;

  private String afterValue;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
