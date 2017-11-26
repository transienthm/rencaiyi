package hr.wozai.service.api.vo.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/12/7.
 */
@Data
@NoArgsConstructor
public class ConvrScheduleChartInputVO {
    private Integer period;
    private Integer minTimeUnit;
}
