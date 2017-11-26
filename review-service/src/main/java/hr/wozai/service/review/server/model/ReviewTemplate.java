// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-04
 */
@Data
@NoArgsConstructor
public class ReviewTemplate {

  private Long templateId;

  private Long orgId;

  private String templateName;

  private Long publishedTime;

  private Long startTime;

  private Long endTime;

  private Long selfReviewDeadline;

  private Long peerReviewDeadline;

  private Long publicDeadline;

  private Integer isReviewerAnonymous;

  private Integer state; //1.draft 2.in progress 3.finish 4.canceled

  private List<String> questions;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;

}
