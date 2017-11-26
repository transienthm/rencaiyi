// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.review.server.helper;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-09-13
 */
public class ReviewInvitationHelper {

  /**
   * Steps:
   *  1) handle peerReview
   *  2) handle managerReview
   *
   * @param
   * @return
   */
  public static boolean isReviewSubmissionCancellable(int isSubmitted, long deadline) {

    long currTs = TimeUtils.getNowTimestmapInMillis();

    if (0 == isSubmitted
        || currTs > deadline) {
      return false;
    }

    return true;
  }

}
