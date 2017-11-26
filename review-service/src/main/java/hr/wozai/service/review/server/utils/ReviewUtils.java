// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.utils;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.service.ReviewProjectService;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-28
 */
@Component
public class ReviewUtils {

  private static int INVITATION_FINISH = 1;
  private static int INVITATION_IN_PROCESS = 2;
  private static int INVITATION_NOT_BEGIN = 3;

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  @Autowired
  @Qualifier("userProfileFacadeProxy")
  private ThriftClientProxy userProfileFacadeProxy;

  private UserProfileFacade userProfileFacade;

  @PostConstruct
  public void init() throws Exception {
    userProfileFacade = (UserProfileFacade)userProfileFacadeProxy.getObject();
  }

  @LogAround
  public List<Long> getSubmittedReviewerIds(long orgId, long templateId, long revieweeId) {

    List<Long> reviewerIds = new ArrayList<>();

    List<ReviewInvitation> reviewInvitations =
        reviewInvitationService.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, revieweeId);

    for (ReviewInvitation reviewInvitation : reviewInvitations) {
      int isSubmitted = reviewInvitation.getIsSubmitted();
      if (isSubmitted == 1) {
        reviewerIds.add(reviewInvitation.getReviewerId());
      }
    }
    return reviewerIds;
  }

  @LogAround
  public List<ReviewCommentDTO> getReviewCommentDTOs(long orgId, long templateId,
                                                     int itemType, long itemId,
                                                     long revieweeId, List<Long> reviewerIds) {

    List<ReviewCommentDTO> reviewCommentDTOs = new ArrayList<>();

    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
            itemType, itemId, revieweeId, reviewerIds);

    for (ReviewComment reviewComment : reviewComments) {

      ReviewCommentDTO reviewCommentDTO = new ReviewCommentDTO();
      BeanUtils.copyProperties(reviewComment, reviewCommentDTO);

      reviewCommentDTOs.add(reviewCommentDTO);
    }

    return reviewCommentDTOs;
  }


  @LogAround
  public boolean isSelfReviewDeadline(ReviewTemplate reviewTemplate) {

    boolean result = false;
    long currentTime = System.currentTimeMillis();
    if (currentTime > reviewTemplate.getSelfReviewDeadline()) {
      result = true;
    }
    return result;
  }

  @LogAround
  public boolean isPeerReviewDeadline(ReviewTemplate reviewTemplate) {

    boolean result = false;
    long currentTime = System.currentTimeMillis();
    if (currentTime > reviewTemplate.getPeerReviewDeadline()) {
      result = true;
    }
    return result;
  }

  @LogAround
  public boolean isTemplatePublic(ReviewTemplate reviewTemplate) {

    boolean result = false;
    long currentTime = System.currentTimeMillis();
    if (currentTime > reviewTemplate.getPublicDeadline()) {
      result = true;
    }
    return result;
  }

  @LogAround
  public List<ReviewProjectDTO> getReviewProjectDTOs(long orgId, long templateId, long revieweeId) {

    List<ReviewProjectDTO> projects = new ArrayList<>();

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    for (ReviewProject reviewProject : reviewProjects) {
      ReviewProjectDTO reviewProjectDTO = new ReviewProjectDTO();
      BeanUtils.copyProperties(reviewProject, reviewProjectDTO);
      projects.add(reviewProjectDTO);
    }

    return projects;
  }

  @LogAround
  public ReviewInvitedUserListDTO getReviewInvitedUsers(long orgId, long templateId,
                                                        long revieweeId, long managerUserId) throws Exception {

    ReviewInvitedUserListDTO reviewInvitedUserListDTO = new ReviewInvitedUserListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    reviewInvitedUserListDTO.setServiceStatusDTO(serviceStatusDTO);

    List<ReviewInvitation> reviewInvitations =
            reviewInvitationService.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, revieweeId);

    List<ReviewInvitedUserDTO> users = new ArrayList<>();
    for (ReviewInvitation reviewInvitation: reviewInvitations) {

      int isCanceled = reviewInvitation.getIsCanceled();
      if (1 == isCanceled)
        continue;

      long reviewerId = reviewInvitation.getReviewerId();
      int reviewStatus;

      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      long amount = reviewCommentService.countReviewAllCommentByReviewer(orgId, templateId,
              revieweeId, reviewerId);

      if (isSubmitted) { reviewStatus = INVITATION_FINISH;
      } else if (amount > 0) { reviewStatus = INVITATION_IN_PROCESS;
      } else { reviewStatus = INVITATION_NOT_BEGIN; }

      ReviewInvitedUserDTO invitedUserDTO = new ReviewInvitedUserDTO();
      invitedUserDTO.setUserId(reviewerId);
      invitedUserDTO.setStatus(reviewStatus);

      if (reviewInvitation.getIsManager() == 1) {
        reviewInvitedUserListDTO.setManagerUserDTO(invitedUserDTO);
      } else {
        users.add(invitedUserDTO);
      }
    }

    //TODO: managerUserDTO cannot be null
    if(null == reviewInvitedUserListDTO.getManagerUserDTO()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    reviewInvitedUserListDTO.setReviewInvitedUserDTOs(users);

    return reviewInvitedUserListDTO;
  }

  @LogAround
  public void fillManagerNameInReviewActivityUserDTOs(long orgId, List<ReviewActivityUserDTO> reviewActivityUserDTOs) {
    List<Long> managerUserIds = new ArrayList<>();
    for (ReviewActivityUserDTO reviewActivityUserDTO : reviewActivityUserDTOs) {
      if (reviewActivityUserDTO.getManagerInvitationDTO() != null) {
        managerUserIds.add(reviewActivityUserDTO.getManagerInvitationDTO().getReviewerId());
      }
    }

    if (CollectionUtils.isEmpty(managerUserIds)) {
      return;
    }

    CoreUserProfileListDTO coreUserProfileListDTO = userProfileFacade.listCoreUserProfile(orgId, managerUserIds, -1L, -1L);
    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(coreUserProfileListDTO.getServiceStatusDTO().getCode());
    }
    Map<Long, CoreUserProfileDTO> map = new HashMap<>();
    if (!CollectionUtils.isEmpty(coreUserProfileListDTO.getCoreUserProfileDTOs())) {
      for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileListDTO.getCoreUserProfileDTOs()) {
        map.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
      }
    }

    for (ReviewActivityUserDTO reviewActivityUserDTO : reviewActivityUserDTOs) {
      Long managerId = reviewActivityUserDTO.getManagerInvitationDTO().getReviewerId();
      if (map.containsKey(managerId)) {
        reviewActivityUserDTO.setManagerFullName(map.get(managerId).getFullName());
      } else {
        reviewActivityUserDTO.setManagerFullName("");
      }
    }
  }

}
