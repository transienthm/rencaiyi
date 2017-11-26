// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.feed;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-25
 */
@Data
@NoArgsConstructor
public class FeedListVO {

  List<FeedVO> feeds;

  Long amount;

}
