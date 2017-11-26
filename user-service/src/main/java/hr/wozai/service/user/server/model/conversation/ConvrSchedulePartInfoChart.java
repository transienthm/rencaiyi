package hr.wozai.service.user.server.model.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by wangbin on 2016/12/7.
 */
@Data
@NoArgsConstructor
public class ConvrSchedulePartInfoChart {
    private String date;
    private Integer periodType;
    private Long amount;
}
