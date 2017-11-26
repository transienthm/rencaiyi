// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@Data
@NoArgsConstructor
public class ReviewComment {

  private Long commentId;

  private Long orgId;

  private Long templateId;

  private Long revieweeId;

  private Long reviewerId;

  private String content;

  private Integer itemType;

  private Long itemId;

  private Long updatedTime;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;

}

