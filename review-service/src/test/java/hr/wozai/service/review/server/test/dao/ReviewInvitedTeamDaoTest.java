// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.review.server.dao.ReviewActivityDao;
import hr.wozai.service.review.server.dao.ReviewInvitedTeamDao;
import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewInvitedTeam;
import hr.wozai.service.review.server.test.base.TestBase;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewInvitedTeamDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitedTeamDaoTest.class);

  @Autowired
  ReviewInvitedTeamDao reviewInvitedTeamDao;

  long mockOrgId = 19999999L;
  long mockReviewTemplateId = 29999999L;
  long mockTeamId = 39999999L;

  @Test
  public void testBatchInsertReviewInvitedTeam() {

    // prepare

    int teamCount = 10;
    List<ReviewInvitedTeam> reviewInvitedTeams = new ArrayList<>();
    for (int i = 0; i < teamCount; i++) {
      ReviewInvitedTeam reviewInvitedTeam = new ReviewInvitedTeam();
      reviewInvitedTeam.setOrgId(mockOrgId);
      reviewInvitedTeam.setReviewTemplateId(mockReviewTemplateId);
      reviewInvitedTeam.setTeamId(mockTeamId + i);
      reviewInvitedTeams.add(reviewInvitedTeam);
    }
    reviewInvitedTeamDao.batchInsertReviewInvitedTeam(reviewInvitedTeams);

    // verify

    List<ReviewInvitedTeam> insertedReviewInvitedTeams =
        reviewInvitedTeamDao.listInvitedTeamIdByOrgIdAndReviewTemplateId(mockOrgId, mockReviewTemplateId);
    Assert.assertEquals(teamCount, insertedReviewInvitedTeams.size());

  }


}
