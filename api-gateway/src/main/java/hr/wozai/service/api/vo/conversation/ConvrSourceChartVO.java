// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.vo.conversation;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@Data
@NoArgsConstructor
public class ConvrSourceChartVO {

  private CoreUserProfileVO sourceUser;

  private Integer convrTimesInThisMonth;

  private Integer convrTimesInThisQuarter;

  private Integer totalCount;

  private String lastDate;

}
