package hr.wozai.service.api.vo.historylog;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 16/5/2.
 */
@Data
@NoArgsConstructor
public class HistoryLogListVO {

    List<HistoryLogVO> historyLogVOs;
}
