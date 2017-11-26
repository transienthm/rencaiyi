// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.server.model.ReviewProject;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-11
 */
public class ReviewProjectHelper {

  public static void chectReviewProjectInsertParams(ReviewProject reviewProject) {

    String content = reviewProject.getName();
    if (null == content || content.trim().isEmpty()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    String role = reviewProject.getRole();
    if (null == role || role.trim().isEmpty()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    Integer score = reviewProject.getScore();
    if (null == score || score.intValue() < 0 || score.intValue() > 5) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    String comment = reviewProject.getComment();
    if (null == comment || comment.trim().isEmpty()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

}
