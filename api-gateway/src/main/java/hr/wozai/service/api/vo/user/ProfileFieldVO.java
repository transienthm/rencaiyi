// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;

import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-29
 */
@Data
@NoArgsConstructor
public class ProfileFieldVO {

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long profileFieldId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long profileTemplateId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long containerId;

  @StringLengthConstraint(lengthConstraint = 20)
  private String displayName;

  private String referenceName;

  /**
   * The index of field in logical schema
   * Range [0, 199] (2016-02-19)
   */
  private Integer logicalIndex;

  /**
   * The index of field in Item
   * Range [0, 199], referring to val[0, 199] in Item table (2016-02-19)
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

  private Integer isEditable;

  private Integer isPublicVisible;

  private Integer isPublicVisibleEditable;

  private Integer isEnabled;

  private Integer isEnabledEditable;

  private Integer isMandatory;

  private Integer isMandatoryEditable;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long createdUserId;

  private Long createdTime;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
