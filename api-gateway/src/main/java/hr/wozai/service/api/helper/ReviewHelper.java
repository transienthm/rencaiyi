// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.helper;

import hr.wozai.service.api.vo.review.ReviewCommentVO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-09-19
 */
public class ReviewHelper {

  public static void makeReviewCommentVOAnonymous(ReviewCommentVO reviewCommentVO) {
    if (null == reviewCommentVO) {
      return;
    }
    reviewCommentVO.setCommentId(null);
    reviewCommentVO.setUserProfile(null);
  }

}
