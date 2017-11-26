// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.review.client.helper;

import hr.wozai.service.review.client.enums.InvitationDisplayType;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-09-06
 */
public class ReviewInvitationHelper {

  public static Integer getInvitationDisplayType(
      int isInActive, int isManager, int isSubmitted, int isCancelled,
      long selfReviewDeadline, long peerReviewDeadline, long publicDeadline) {

    Integer invitaitonDisplayType = null;
    long currTs = TimeUtils.getNowTimestmapInMillis();
    if (0 == isInActive
        && currTs < peerReviewDeadline) {
      invitaitonDisplayType = InvitationDisplayType.REVIEWEE_UNSUBMITTED_UNEXPIRED.getCode();
    } else if (0 == isInActive
               && currTs >= peerReviewDeadline) {
      invitaitonDisplayType = InvitationDisplayType.REVIEWEE_UNSUBMITTED_EXPIRED.getCode();
    } else if (0 == isManager) {
      if (1 == isCancelled) {
        invitaitonDisplayType = InvitationDisplayType.PEER_REVIEW_REJECTED.getCode();
      } else if (currTs < selfReviewDeadline) {
        invitaitonDisplayType = InvitationDisplayType.FLOW_PENDING.getCode();
      } else if (0 == isSubmitted
                 && currTs < peerReviewDeadline) {
        invitaitonDisplayType = InvitationDisplayType.EDIT.getCode();
      } else if (0 == isSubmitted
                 && currTs < peerReviewDeadline) {
        invitaitonDisplayType = InvitationDisplayType.EDIT.getCode();
      } else if (1 == isSubmitted
                 && currTs < peerReviewDeadline) {
        invitaitonDisplayType = InvitationDisplayType.VIEW_OR_UPDATE.getCode();
      } else if (0 ==  isSubmitted
                 && currTs < publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.EXPIRED_CAN_EDIT.getCode();
      } else if (1 == isSubmitted
                 && currTs < publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.VIEW.getCode();
      } else if (0 == isSubmitted
                 && currTs >= publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.EXPIRED_CANNOT_EDIT.getCode();
      } else if (1 == isSubmitted
                 && currTs >= publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.VIEW.getCode();
      }
    } else if (1 == isManager) {
      if (currTs < peerReviewDeadline) {
        invitaitonDisplayType = InvitationDisplayType.FLOW_PENDING.getCode();
      } else if (0 == isSubmitted
                 && currTs < publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.EDIT.getCode();
      } else if (1 == isSubmitted
                 && currTs < publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.VIEW_OR_UPDATE.getCode();
      } else if (0 == isSubmitted
                 && currTs >= publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.EXPIRED_CAN_EDIT.getCode();
      } else if (1 == isSubmitted
                 && currTs >= publicDeadline) {
        invitaitonDisplayType = InvitationDisplayType.VIEW.getCode();
      }
    }
    return invitaitonDisplayType;
  }

}