// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-19
 */
@Data
@NoArgsConstructor
public class UserEmployment {

  private Long userEmploymentId;

  private Long orgId;

  private Long userId;

  private Integer userStatus;

  private Integer onboardingStatus;

  private Integer contractType;

  private Integer employmentStatus;

  private Long internshipEnrollDate;

  private Long internshipResignDate;

  private Long parttimeEnrollDate;

  private Long parttimeResignDate;

  private Long fulltimeEnrollDate;

  private Long fulltimeResignDate;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
