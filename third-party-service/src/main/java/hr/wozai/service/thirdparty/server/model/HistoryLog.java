package hr.wozai.service.thirdparty.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wang bin on 16/4/25.
 */
@Data
@NoArgsConstructor
public class HistoryLog {

    private Long historyLogId;

    private Long orgId;

    private Long userId;

    private Long actorUserId;

    /**
     * 1:职级的调整
     * 2:职位的调整
     * 3.获得新评价
     * 4.汇报对象的变更
     */
    private Integer logType;

    private String content;

    private String preValue;

    private String curValue;

    private Long createdTime;

    private Long createdUserId;

    private Integer isDeleted;

}
