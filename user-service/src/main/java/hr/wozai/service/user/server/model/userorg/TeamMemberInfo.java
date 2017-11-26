package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/18
 */
@Data
@NoArgsConstructor
public class TeamMemberInfo {
  private Long userId;

  private Long teamId;

  private String teamName;

  private Integer isTeamAdmin;
}
