// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
@Data
@NoArgsConstructor
public class SimpleUserProfileVO {

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long userId;

  private String fullName;

  private String avatarUrl;

  private Integer gender;

}
