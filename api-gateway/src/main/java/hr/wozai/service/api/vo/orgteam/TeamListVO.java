package hr.wozai.service.api.vo.orgteam;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/16
 */
@Data
@NoArgsConstructor
public class TeamListVO {
  List<TeamVO> teamVOs;

  List<CoreUserProfileVO> coreUserProfileVOs;

  List<ProjectTeamVO> projectTeamVOs;
}
