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
public class ReviewActivity {

  private Long activityId;

  private Long orgId;

  private Long templateId;

  private Long revieweeId;

  private Integer isReaded;

  private Integer isSubmitted;

  private Integer isCanceled;

  private Integer isBackuped;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;

}
