package hr.wozai.service.user.server.model.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/12/6.
 */
@Data
@NoArgsConstructor
public class ConvrScheduleChart {
    private float planedTimes;
    private float actualTimes;
    private String date;
}
