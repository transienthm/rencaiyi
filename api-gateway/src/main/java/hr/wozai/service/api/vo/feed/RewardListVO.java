package hr.wozai.service.api.vo.feed;

import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 2016/11/21.
 */
@Data
@NoArgsConstructor
public class RewardListVO {
    private List<RewardVO> rewardVOs;
}
