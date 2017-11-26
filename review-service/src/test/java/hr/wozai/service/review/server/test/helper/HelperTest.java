// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.helper;

import hr.wozai.service.review.server.helper.*;
import hr.wozai.service.review.server.model.ReviewInvitedTeam;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import org.junit.Test;
import org.springframework.jdbc.UncategorizedSQLException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class HelperTest extends TestBase {

  @Test
  public void testSetServiceStatusForFacadeResult() throws Throwable {
    FacadeExceptionHelper facadeExceptionHelper = new FacadeExceptionHelper();
    ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    UncategorizedSQLException e = new UncategorizedSQLException("Incorrect string value", "select", new SQLException());
    facadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
  }

  @Test
  public void testSetServiceStatusForCanceledActivity() throws Throwable {
    FacadeExceptionHelper facadeExceptionHelper = new FacadeExceptionHelper();
    ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    facadeExceptionHelper.setServiceStatusForCanceledActivity(serviceStatusDTO);
  }

  @Test
  public void testIsReviewSubmissionCancellable() throws Throwable {
    ReviewInvitationHelper reviewInvitationHelper = new ReviewInvitationHelper();
    reviewInvitationHelper.isReviewSubmissionCancellable(0, TimeUtils.getNowTimestmapInMillis());
    reviewInvitationHelper.isReviewSubmissionCancellable(1, TimeUtils.getNowTimestmapInMillis());
  }

  @Test
  public void testChectReviewProjectInsertParams() {
    ReviewProjectHelper reviewProjectHelper = new ReviewProjectHelper();

    ReviewProject reviewProject = new ReviewProject();
    reviewProject.setOrgId(0L);
    reviewProject.setTemplateId(0L);
    reviewProject.setRevieweeId(0L);
    reviewProject.setName("");
    reviewProject.setRole("");
    reviewProject.setScore(6);
    reviewProject.setComment("");
    reviewProject.setLastModifiedUserId(0L);

    try {
      reviewProjectHelper.chectReviewProjectInsertParams(reviewProject);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewProject.setName("Name");
    try {
      reviewProjectHelper.chectReviewProjectInsertParams(reviewProject);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewProject.setRole("Role");
    try {
      reviewProjectHelper.chectReviewProjectInsertParams(reviewProject);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewProject.setScore(1);
    try {
      reviewProjectHelper.chectReviewProjectInsertParams(reviewProject);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testReviewTemplateHelper() {
    ReviewTemplateHelper reviewTemplateHelper = new ReviewTemplateHelper();
    try {
      reviewTemplateHelper.checkReviewTemplateInsertParams(null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    long currentTime = System.currentTimeMillis();
    ReviewTemplate reviewTemplate = new ReviewTemplate();
    reviewTemplate.setOrgId(0L);
    reviewTemplate.setTemplateName("");
    reviewTemplate.setStartTime(0L);
    reviewTemplate.setEndTime(currentTime - 100);
    reviewTemplate.setSelfReviewDeadline(currentTime + 1000);
    reviewTemplate.setPeerReviewDeadline(currentTime + 2000);
    reviewTemplate.setPublicDeadline(currentTime + 3000);
    reviewTemplate.setIsReviewerAnonymous(0);
    reviewTemplate.setLastModifiedUserId(0L);

    try {
      reviewTemplateHelper.checkReviewTemplateInsertParams(reviewTemplate);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewTemplate.setQuestions(new ArrayList<>(Arrays.asList("")));
    try {
      reviewTemplateHelper.checkReviewTemplateInsertParams(reviewTemplate);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewTemplateHelper.isValidBatchAddReviewInvitedTeamRequest(null);
    List<ReviewInvitedTeam> reviewInvitedTeams = new ArrayList<>();
    ReviewInvitedTeam reviewInvitedTeam = new ReviewInvitedTeam();
    reviewInvitedTeams.add(reviewInvitedTeam);
    reviewTemplateHelper.isValidBatchAddReviewInvitedTeamRequest(reviewInvitedTeams);
  }
}
