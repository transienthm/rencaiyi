// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
@Data
@NoArgsConstructor
public class Feed {

  private Long feedId;

  private Long orgId;

  private Long userId;

  private Long teamId;

  private String content;

  private List<String> atUsers;

  private List<String> images;

  private Long likeNumber;

  private Long commentNumber;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;


}
