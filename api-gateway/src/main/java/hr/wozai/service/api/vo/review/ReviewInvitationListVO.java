// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-14
 */
@Data
@NoArgsConstructor
public class ReviewInvitationListVO {

  private List<ReviewInvitationVO> reviewInvitations;

  private Long amount;

}
