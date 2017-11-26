package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/16
 */
@Data
@NoArgsConstructor
public class Team {

  private Long teamId;

  private Long orgId;

  private String teamName;

  private Long parentTeamId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private Integer isDeleted;

}
