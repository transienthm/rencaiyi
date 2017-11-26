package hr.wozai.service.user.server.model.okr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/9/8
 */
@Data
@NoArgsConstructor
public class OkrComment {
  private Long okrCommentId;

  private Long orgId;

  private Long objectiveId;

  private Long keyResultId;

  private String keyResultContent;

  private Long userId;

  private String content;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
