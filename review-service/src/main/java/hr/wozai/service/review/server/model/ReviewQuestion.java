// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-04
 */
@Data
@NoArgsConstructor
public class ReviewQuestion {

  private Long questionId;

  private Long orgId;

  private Long templateId;

  private String name;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;

}
