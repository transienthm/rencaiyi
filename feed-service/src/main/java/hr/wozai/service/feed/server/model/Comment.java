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
public class Comment {

  private Long commentId;

  private Long orgId;

  private Long feedId;

  private Long userId;

  private String content;

  private List<String> atUsers;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;

}
