// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-05
 */
@Data
@NoArgsConstructor
public class ReviewInputProjectVO {

  @StringLengthConstraint(lengthConstraint = 40)
  private String name;

  @StringLengthConstraint(lengthConstraint = 20)
  private String role;

  private Integer score;

  @StringLengthConstraint(lengthConstraint = 2000)
  private String comment;

}
