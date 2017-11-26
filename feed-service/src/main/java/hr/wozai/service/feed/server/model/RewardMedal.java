package hr.wozai.service.feed.server.model;

import hr.wozai.service.feed.client.enums.RewardMedalTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/11/17.
 */
@Data
@NoArgsConstructor
public class RewardMedal {
    private Long rewardMedalId;

    private Long orgId;

    private String medalIcon;

    private Integer medalType;

    private String medalName;

    private String description;

    private Long createdUserId;

    private Long createdTime;

    private Long lastModifiedUserId;

    private Long lastModifiedTime;

    private Integer isDeleted;

    public void setRewardMedalDTOByRewardMedalTemplate(RewardMedalTemplate rewardMedalTemplate) {
        this.setMedalName(rewardMedalTemplate.getMedalName());
        this.setMedalType(rewardMedalTemplate.getMedalType());
        this.setMedalIcon(rewardMedalTemplate.getMedalIcon());
        this.setDescription(rewardMedalTemplate.getMedalDescription());
    }
}
