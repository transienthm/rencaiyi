// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-17
 */
@Data
@NoArgsConstructor
public class UniqueFieldValue<T> {

  private Long uniqueFieldValueId;

  private Long orgId;

  private Long profileFieldId;

  private Integer dataType;

  private T dataValue;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
