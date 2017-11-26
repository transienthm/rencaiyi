// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-29
 */
@Data
@NoArgsConstructor
public class ReviewPastInvitationVO {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long invitationId;

  private String templateName;

}
