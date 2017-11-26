package hr.wozai.service.api.vo.feed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 2016/11/22.
 */
@Data
@NoArgsConstructor
public class RewardVO {

    @JsonSerialize(using = EncodeSerializer.class)
    private Long feedId;

    private Integer rewardType;

    private List<CoreUserProfileVO> rewardedUsers;

    private List<TeamVO> rewardedTeams;

    //private List<IdVO> rewardMedalId;
    private RewardMedalListVO rewardMedalListVO;

}
