// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-13
 */
@Data
@NoArgsConstructor
public class UserSysNotification {

  private Long userSysNotificationId;

  private Long orgId;

  private Long objectId;

  private Integer objectType;

  private Long notifyUserId;

  private Integer logicalIndex;

  private Integer needEmail;

  private Integer needMessageCenter;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
