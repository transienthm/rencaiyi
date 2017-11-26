// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;

import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-11
 */
@Data
@NoArgsConstructor
public class DocumentVO {

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long documentId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  private Integer scenario;

  private String documentKey;

  @StringLengthConstraint(lengthConstraint = 20)
  private String documentName;

  private String documentType;

  @StringLengthConstraint(lengthConstraint = 140)
  private String description;

  private String md5Hash;

  private Long documentSize;

  private Integer storageStatus;

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
