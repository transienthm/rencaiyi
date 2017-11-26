package hr.wozai.service.user.server.model.okr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OkrLog {
  private Long okrLogId;

  private Long orgId;

  private Long objectiveId;

  private Integer type;

  private String content;

  private List<String> atUsers;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
