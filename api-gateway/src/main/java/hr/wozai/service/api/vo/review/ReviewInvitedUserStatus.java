// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-12
 */
@Data
@NoArgsConstructor
public class ReviewInvitedUserStatus {

  CoreUserProfileVO invitedUserProfile;

  //1. finish  2. in progress 3. not begin
  Integer status;

}
