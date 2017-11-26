// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-24
 */
@Data
@NoArgsConstructor
public class ReviewQuestionVO {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long questionId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long templateId;

  private String name;

  private ReviewCommentVO revieweeComment;

  private List<ReviewCommentVO> submittedComment;

  private ReviewCommentVO reviewerComment;

  private Integer isEditable;

}
