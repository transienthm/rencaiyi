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
 * @Created: 2016-03-30
 */
@Data
@NoArgsConstructor
public class ReviewProjectSimpleVO {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long projectId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long templateId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long revieweeId;

  private String name;

}
