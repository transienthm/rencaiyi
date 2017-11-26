package hr.wozai.service.review.server.utils;

import hr.wozai.service.review.client.dto.ReviewActivityUserDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-15
 */
@Component
public class ReviewActivityScoreSortUtils implements Comparator<ReviewActivityUserDTO> {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityScoreSortUtils.class);

  @Override
  public int compare(ReviewActivityUserDTO activityUserDTO1, ReviewActivityUserDTO activityUserDTO2) {

    ReviewInvitationDTO managerInvitation1 = activityUserDTO1.getManagerInvitationDTO();
    ReviewInvitationDTO managerInvitation2 = activityUserDTO2.getManagerInvitationDTO();

    if(null == managerInvitation1 && null == managerInvitation2) {
      return 0;
    }
    if(null == managerInvitation1) {
      return +1;
    }
    if(null == managerInvitation2) {
      return -1;
    }

    int score1 = managerInvitation1.getScore();
    int score2 = managerInvitation2.getScore();

    if(score1 < score2) {
      return -1;
    }
    else if(score1 == score2) {
      return 0;
    }
    else {
      return 1;
    }
  }

}
