// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.feed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-17
 */

@Data
@NoArgsConstructor
public class FeedInputVO {

  @StringLengthConstraint(lengthConstraint = 2000)
  String content;

  List<IdVO> atUsers;

  List<String> images;

  private Integer rewardType;

  private List<IdVO> rewardeeIds;

  private List<IdVO> rewardMedalId;

}
