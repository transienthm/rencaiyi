// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.onboarding;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-04
 */
@Data
@NoArgsConstructor
public class OnboardingTemplate {

  private Long onboardingTemplateId;

  private Long orgId;

  private String displayName;

  private String prologue;

  private String epilogue;

  private Long profileTemplateId;

  protected List<OnboardingDocument> onboardingDocuments;

  private Integer isPreset;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
