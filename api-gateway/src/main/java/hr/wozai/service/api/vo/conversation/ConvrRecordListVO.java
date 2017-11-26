// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.vo.conversation;

import java.util.List;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.SimpleUserProfileVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@Data
@NoArgsConstructor
public class ConvrRecordListVO {

  private List<ConvrRecordVO> convrRecordVOs;

  private Integer totalNumber;

  private List<SimpleUserProfileVO> userProfileVOs;

}
