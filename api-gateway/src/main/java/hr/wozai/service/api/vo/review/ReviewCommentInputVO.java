// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-08-09
 */
@Data
@NoArgsConstructor
public class ReviewCommentInputVO {
  @StringLengthConstraint(lengthConstraint = 2000)
  private String content;

}
