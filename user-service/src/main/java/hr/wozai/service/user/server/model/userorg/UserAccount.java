// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserAccount implements Serializable {

  private Long userId;

  private String emailAddress;

  @JsonIgnore
  private String encryptedPassword;

  private Integer loginFailTime;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private JSONObject extend;

  private Integer isDeleted;

}
