// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.helper;

import org.springframework.util.CollectionUtils;

import java.util.List;

import hr.wozai.service.review.server.model.ReviewInvitedTeam;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-08
 */
public class ReviewTemplateHelper {

  public static void checkReviewTemplateInsertParams(ReviewTemplate reviewTemplate) {
    Long currentTime = System.currentTimeMillis();

    if (null == reviewTemplate ||
        null == reviewTemplate.getOrgId() ||
        null == reviewTemplate.getTemplateName() ||
        null == reviewTemplate.getStartTime() ||
        null == reviewTemplate.getEndTime() ||
        null == reviewTemplate.getSelfReviewDeadline() ||
        null == reviewTemplate.getPeerReviewDeadline() ||
        null == reviewTemplate.getPublicDeadline() ||
        !BooleanUtils.isValidBooleanValue(reviewTemplate.getIsReviewerAnonymous()) ||
        null == reviewTemplate.getLastModifiedUserId() ||
        reviewTemplate.getStartTime() >= reviewTemplate.getEndTime() ||
        reviewTemplate.getEndTime() >= currentTime ||
        currentTime >= reviewTemplate.getSelfReviewDeadline() ||
        reviewTemplate.getSelfReviewDeadline() >= reviewTemplate.getPeerReviewDeadline() ||
        reviewTemplate.getPeerReviewDeadline() >= reviewTemplate.getPublicDeadline()
        ) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    List<String> questions = reviewTemplate.getQuestions();
    if(null == questions || questions.size() == 0) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    for(String question: questions) {
      if(question.trim().isEmpty()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    }

  }

  public static boolean isValidBatchAddReviewInvitedTeamRequest(List<ReviewInvitedTeam> reviewInvitedTeams) {
    if (CollectionUtils.isEmpty(reviewInvitedTeams)) {
      return false;
    }
    for (ReviewInvitedTeam reviewInvitedTeam: reviewInvitedTeams) {
      if (null == reviewInvitedTeam.getOrgId()
          || null == reviewInvitedTeam.getReviewTemplateId()
          || null == reviewInvitedTeam.getTeamId()) {
        return false;
      }
    }
    return true;
  }

}
