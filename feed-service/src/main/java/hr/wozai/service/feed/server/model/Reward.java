package hr.wozai.service.feed.server.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Created by wangbin on 2016/11/16.
 */
@Data
@NoArgsConstructor
public class Reward {
    private Long rewardId;

    private Long orgId;

    private Long userId;

    private Long feedId;

    private Integer rewardType;

    private Long rewardeeId;

    private Long rewardMedalId;

    private Long createdTime;

    private Long lastModifiedUserId;

    private Long lastModifiedTime;

    private JSONObject extend;

    private Integer isDeleted;
}
