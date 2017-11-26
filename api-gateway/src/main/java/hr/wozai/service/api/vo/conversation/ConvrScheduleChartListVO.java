package hr.wozai.service.api.vo.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 2016/12/6.
 */
@Data
@NoArgsConstructor
public class ConvrScheduleChartListVO {
    List<ConvrScheduleChartVO> convrScheduleChartVOList;
}
