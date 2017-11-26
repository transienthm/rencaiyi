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
public class NameIndexVO {
  private List<TeamVO> teamVOs;

  private List<CoreUserProfileVO> coreUserProfileVOs;

  private List<ProjectTeamVO> projectTeamVOs;

  private long totalTeamNumber;

  private long totalUserNumber;

}
