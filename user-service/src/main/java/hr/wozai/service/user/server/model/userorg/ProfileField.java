// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-07
 */
@Data
@NoArgsConstructor
public class ProfileField {

  private Long profileFieldId;

  private Long orgId;

  private Long profileTemplateId;

  private Long containerId;

  private String displayName;

  private String referenceName;

  /**
   * The index of field in logical schema
   * Range [0, 179] (2016-03-10)
   */
  private Integer logicalIndex;

  /**
   * The index of field in Item
   * Range [0, 179], referring to val[0, 199] in Item table (2016-03-10)
   */
  private Integer physicalIndex;

  private Integer dataType;

  private JSONObject typeSpec;

  private String promptInfo;

  private String dataValue;

  private Integer isTypeSpecEditable;

  private Integer isSystemRequired;

  private Integer isOnboardingStaffEditable;

  private Integer isActiveStaffEditable;

  private Integer isPublicVisible ;

  private Integer isPublicVisibleEditable;

  private Integer isEnabled;

  private Integer isEnabledEditable;

  private Integer isMandatory;

  private Integer isMandatoryEditable;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
