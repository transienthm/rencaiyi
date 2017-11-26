package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/15
 */
@Data
@NoArgsConstructor
public class ProjectTeam {
  private Long projectTeamId;

  private Long orgId;

  private Long teamId;

  private String projectTeamName;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
