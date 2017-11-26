// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hr.wozai.service.servicecommons.utils.bean.BeanHelper;

/**
 * @Author: Zich Liu
 * @Version: 1.0
 * @Created: 2016-11-16
 */
@Data
@NoArgsConstructor
public class ReviewObject {

  private long orgId;
  private long userId;
  private long currentTime;

  private ReviewTemplate reviewTemplate;
  private List<ReviewActivity> reviewActivities;
  private List<ReviewComment> reviewComments;
  private List<ReviewInvitation> reviewInvitations;
  private List<ReviewProject> reviewProjects;
  private List<ReviewQuestion> reviewQuestions;
  private List<ReviewInvitedTeam> reviewInvitedTeams;

  public ReviewObject(long orgId, long userId) {
    this.orgId = orgId;
    this.userId = userId;
    this.currentTime = System.currentTimeMillis();

    reviewTemplate = new ReviewTemplate();
    reviewActivities = new ArrayList<>();
    reviewComments = new ArrayList<>();
    reviewInvitations = new ArrayList<>();
    reviewProjects = new ArrayList<>();
    reviewQuestions = new ArrayList<>();
    reviewInvitedTeams = new ArrayList<>();
  }

  public void setReviewTemplate(ReviewTemplate reviewTemplate) {
    this.initReviewTemplate(this.reviewTemplate);
    BeanHelper.copyPropertiesHandlingJSONIgnoreNull(reviewTemplate, this.reviewTemplate);
  }

  public void setReviewActivities(List<ReviewActivity> reviewActivities) {
    for (ReviewActivity item : reviewActivities) {
      ReviewActivity reviewActivity = new ReviewActivity();
      this.initReviewActivity(reviewActivity);
      BeanHelper.copyPropertiesHandlingJSONIgnoreNull(item, reviewActivity);
      this.reviewActivities.add(reviewActivity);
    }
  }

  public void setReviewInvitations(List<ReviewInvitation> reviewInvitations) {
    for (ReviewInvitation item : reviewInvitations) {
      ReviewInvitation reviewInvitation = new ReviewInvitation();
      this.initReviewInvitation(reviewInvitation);
      BeanHelper.copyPropertiesHandlingJSONIgnoreNull(item, reviewInvitation);
      this.reviewInvitations.add(reviewInvitation);
    }
  }

  public void setReviewComments(List<ReviewComment> reviewComments) {
    for (ReviewComment item : reviewComments) {
      ReviewComment reviewComment = new ReviewComment();
      this.initReviewComment(reviewComment);
      BeanHelper.copyPropertiesHandlingJSONIgnoreNull(item, reviewComment);
      this.reviewComments.add(reviewComment);
    }
  }

  public void setReviewProjects(List<ReviewProject> reviewProjects) {
    for (ReviewProject item : reviewProjects) {
      ReviewProject reviewProject = new ReviewProject();
      this.initReviewProject(reviewProject);
      BeanHelper.copyPropertiesHandlingJSONIgnoreNull(item, reviewProject);
      this.reviewProjects.add(reviewProject);
    }
  }

  public void setReviewQuestions(List<ReviewQuestion> reviewQuestions) {
    for (ReviewQuestion item : reviewQuestions) {
      ReviewQuestion reviewQuestion = new ReviewQuestion();
      this.initReviewQuestion(reviewQuestion);
      BeanHelper.copyPropertiesHandlingJSONIgnoreNull(item, reviewQuestion);
      this.reviewQuestions.add(reviewQuestion);
    }
  }

  public void setReviewInvitedTeams(List<ReviewInvitedTeam> reviewInvitedTeams) {
    for (ReviewInvitedTeam item : reviewInvitedTeams) {
      ReviewInvitedTeam reviewInvitedTeam = new ReviewInvitedTeam();
      this.initReviewInvitedTeam(reviewInvitedTeam);
      BeanHelper.copyPropertiesHandlingJSONIgnoreNull(item, reviewInvitedTeam);
      this.reviewInvitedTeams.add(reviewInvitedTeam);
    }
  }

  public void initReviewTemplate(ReviewTemplate reviewTemplate) {
    reviewTemplate.setTemplateId(-1L);
    reviewTemplate.setOrgId(this.orgId);
    reviewTemplate.setTemplateName("");
    reviewTemplate.setPublishedTime(this.currentTime);
    reviewTemplate.setStartTime(this.currentTime - 86400000 * 20);
    reviewTemplate.setEndTime(this.currentTime - 86400000 * 10);
    reviewTemplate.setSelfReviewDeadline(this.currentTime + 86400000 * 3);
    reviewTemplate.setPeerReviewDeadline(this.currentTime + 86400000 * 6);
    reviewTemplate.setPublicDeadline(this.currentTime + 86400000 * 9);
    reviewTemplate.setIsReviewerAnonymous(0);
    reviewTemplate.setState(2);
    reviewTemplate.setCreatedTime(this.currentTime);
    reviewTemplate.setLastModifiedUserId(this.userId);
    reviewTemplate.setLastModifiedTime(this.currentTime);
    reviewTemplate.setExtend(null);
    reviewTemplate.setIsDeleted(0);
  }

  public void initReviewActivity(ReviewActivity reviewActivity) {
    reviewActivity.setActivityId(-1L);
    reviewActivity.setOrgId(this.orgId);
    reviewActivity.setTemplateId(this.reviewTemplate.getTemplateId());
    reviewActivity.setRevieweeId(0L);
    reviewActivity.setIsReaded(0);
    reviewActivity.setIsSubmitted(0);
    reviewActivity.setIsCanceled(0);
    reviewActivity.setIsBackuped(0);
    reviewActivity.setCreatedTime(this.currentTime);
    reviewActivity.setLastModifiedUserId(this.userId);
    reviewActivity.setLastModifiedTime(this.currentTime);
    reviewActivity.setExtend(null);
    reviewActivity.setIsDeleted(0);
  }

  public void initReviewQuestion(ReviewQuestion reviewQuestion) {
    reviewQuestion.setQuestionId(-1L);
    reviewQuestion.setOrgId(this.orgId);
    reviewQuestion.setTemplateId(this.reviewTemplate.getTemplateId());
    reviewQuestion.setName("");
    reviewQuestion.setCreatedTime(this.currentTime);
    reviewQuestion.setLastModifiedUserId(this.userId);
    reviewQuestion.setLastModifiedTime(this.currentTime);
    reviewQuestion.setExtend(null);
    reviewQuestion.setIsDeleted(0);
  }

  public void initReviewComment(ReviewComment reviewComment) {
    reviewComment.setCommentId(-1L);
    reviewComment.setOrgId(this.orgId);
    reviewComment.setTemplateId(this.reviewTemplate.getTemplateId());
    reviewComment.setItemType(0);
    reviewComment.setItemId(0L);
    reviewComment.setRevieweeId(0L);
    reviewComment.setReviewerId(0L);
    reviewComment.setContent("");
    reviewComment.setCreatedTime(this.currentTime);
    reviewComment.setLastModifiedUserId(this.userId);
    reviewComment.setLastModifiedTime(this.currentTime);
    reviewComment.setExtend(null);
    reviewComment.setIsDeleted(0);
  }

  public void initReviewInvitation(ReviewInvitation reviewInvitation) {
    reviewInvitation.setInvitationId(-1L);
    reviewInvitation.setOrgId(this.orgId);
    reviewInvitation.setTemplateId(this.reviewTemplate.getTemplateId());
    reviewInvitation.setRevieweeId(0L);
    reviewInvitation.setReviewerId(0L);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setScore(0);
    reviewInvitation.setIsSubmitted(0);
    reviewInvitation.setIsCanceled(0);
    reviewInvitation.setIsBackuped(0);
    reviewInvitation.setCreatedTime(this.currentTime);
    reviewInvitation.setLastModifiedUserId(this.userId);
    reviewInvitation.setLastModifiedTime(this.currentTime);
    reviewInvitation.setExtend(null);
    reviewInvitation.setIsDeleted(0);
  }

  public void initReviewProject(ReviewProject reviewProject) {
    reviewProject.setProjectId(-1L);
    reviewProject.setOrgId(this.orgId);
    reviewProject.setTemplateId(this.reviewTemplate.getTemplateId());
    reviewProject.setRevieweeId(0L);
    reviewProject.setName("");
    reviewProject.setRole("");
    reviewProject.setScore(0);
    reviewProject.setComment("");
    reviewProject.setCreatedTime(this.currentTime);
    reviewProject.setLastModifiedUserId(this.userId);
    reviewProject.setLastModifiedTime(this.currentTime);
    reviewProject.setExtend(null);
    reviewProject.setIsDeleted(0);
  }

  public void initReviewInvitedTeam(ReviewInvitedTeam reviewInvitedTeam) {
    reviewInvitedTeam.setReviewInvitedTeamId(-1L);
    reviewInvitedTeam.setOrgId(this.orgId);
    reviewInvitedTeam.setReviewTemplateId(this.reviewTemplate.getTemplateId());
    reviewInvitedTeam.setTeamId(0L);
    reviewInvitedTeam.setIsDeleted(0);
  }
}
