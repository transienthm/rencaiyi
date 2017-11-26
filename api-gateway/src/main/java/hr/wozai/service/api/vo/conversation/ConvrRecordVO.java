// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.vo.conversation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.SimpleUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@Data
@NoArgsConstructor
public class ConvrRecordVO {

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long convrRecordId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long convrScheduleId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long sourceUserId;

  private SimpleUserProfileVO sourceUserProfileVO;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long targetUserId;

  private SimpleUserProfileVO targetUserProfileVO;

  private Long convrDate;

  private String topicProgress;

  private String topicPlan;

  private String topicObstacle;

  private String topicHelp;

  private String topicCareer;

  private String topicElse;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long createdUserId;

  private Long createdTime;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long lastModifiedUserId;

  private Long lastModifiedTime;

//  private Integer isDeleted;

}
