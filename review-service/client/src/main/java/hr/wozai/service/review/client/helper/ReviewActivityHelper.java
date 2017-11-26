// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.review.client.helper;

import hr.wozai.service.review.client.enums.ActivityDisplayType;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-09-06
 */
public class ReviewActivityHelper {

  public static Integer getActivityDisplayType(int isSubmitted, long selfReviewDeadline, long peerReviewDeadline) {
    Integer activityDisplayType = null;
    long currTs = TimeUtils.getNowTimestmapInMillis();
    if (currTs < selfReviewDeadline) {
      if (1 == isSubmitted) {
        activityDisplayType = ActivityDisplayType.VIEW_OR_UPDATE.getCode();
      } else {
        activityDisplayType = ActivityDisplayType.EDIT.getCode();
      }
    } else if (currTs >= selfReviewDeadline
               && currTs < peerReviewDeadline) {
      if (1 == isSubmitted) {
        activityDisplayType = ActivityDisplayType.VIEW.getCode();
      } else {
        activityDisplayType = ActivityDisplayType.EXPIRED_CAN_EDIT.getCode();
      }
    } else {
      if (1 == isSubmitted) {
        activityDisplayType = ActivityDisplayType.VIEW.getCode();
      } else {
        activityDisplayType = ActivityDisplayType.EXPIRED_CANNOT_EDIT.getCode();
      }
    }
    return activityDisplayType;
  }

  public static boolean canEditReviewActivity(Integer activityDisplayTypeCode) {
    if (null == activityDisplayTypeCode) {
      return false;
    }
    ActivityDisplayType activityDisplayType = ActivityDisplayType.getEnumByCode(activityDisplayTypeCode);
    if (null == activityDisplayType) {
      return false;
    }
    if (ActivityDisplayType.EDIT.equals(activityDisplayType)
        || ActivityDisplayType.VIEW_OR_UPDATE.equals(activityDisplayType)
        || ActivityDisplayType.EXPIRED_CAN_EDIT.equals(activityDisplayType)) {
      return true;
    }
    return false;
  }

}
