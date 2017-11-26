// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.feed;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Data
@NoArgsConstructor
public class ThumbupVO {

  private CoreUserProfileVO thumbupUser;

  private Long lastModifiedTime;

}
