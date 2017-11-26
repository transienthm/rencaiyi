package hr.wozai.service.user.server.model.okr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/17
 */
@Data
@NoArgsConstructor
public class OkrLogComment {
  private Long commentId;

  private Long orgId;

  private Long okrLogId;

  private Long userId;

  private String content;

  private List<String> atUsers;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
