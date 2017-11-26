package hr.wozai.service.api.vo.feed;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 2016/11/18.
 */
@Data
@NoArgsConstructor
public class RewardMedalListVO {
    private List<RewardMedalVO> personalRewardMedalVOs;

    private List<RewardMedalVO> teamRewardMedalVOs;

    private Long amount;
}
