// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.feed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.ImageVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-24
 */
@Data
@NoArgsConstructor
public class FeedVO {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long feedId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long teamId;

  private CoreUserProfileVO feedUser;

  private String content;

  private List<CoreUserProfileVO> atUsers;

  private List<ImageVO> images;

  private RewardVO rewardVO;

  private Long likeNumber;

  private Long commentNumber;

  private Long createdTime;

  Integer isLikable;

  Integer isDeletable;

}
