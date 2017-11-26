// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.onboarding;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
@Data
@NoArgsConstructor
public class OnboardingDocument {

  private Long onboardingDocumentId;

  private Long orgId;

  private Long onboardingTemplateId;

  private Long documentId;

  private Integer logicalIndex;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
