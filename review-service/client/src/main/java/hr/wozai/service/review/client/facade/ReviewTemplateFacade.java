// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@ThriftService
public interface ReviewTemplateFacade {

  /**
   * Insert review template
   * @param orgId
   * @param reviewTemplateDTO
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  LongDTO insertReviewTemplate(long orgId, ReviewTemplateDTO reviewTemplateDTO, long actorUserId, long adminUserId);

  /**
   * Find review template
   * @param orgId
   * @param templateId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  ReviewTemplateDTO findReviewTemplate(long orgId, long templateId, long actorUserId, long adminUserId);

  /**
   * List review templates by orgId and reviewTemplateStatus
   *
   * @param orgId
   * @param pageNumber
   * @param pageSize
   * @param statuses
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ReviewTemplateListDTO listReviewTemplate(
      long orgId, int pageNumber, int pageSize, List<Integer> statuses,
      int templateListType, boolean isHr, long actorUserId, long adminUserId);

  /**
   * Count review template
   * @param orgId
   */
  @ThriftMethod
  LongDTO countReviewTemplate(long orgId);

//  /**
//   * Update review template
//   * @param orgId
//   * @param reviewTemplateDTO
//   * @param actorUserId
//   * @param adminUserId
//   */
//  @ThriftMethod
//  VoidDTO updateReviewTemplate(long orgId, ReviewTemplateDTO reviewTemplateDTO, long actorUserId, long adminUserId);

//  /**
//   * Publish review template
//   * @param orgId
//   * @param templateId
//   * @param lastModifiedUserId
//   * @param actorUserId
//   * @param adminUserId
//   */
//  @ThriftMethod
//  VoidDTO publishReviewTemplate(
//      long orgId, long templateId, long lastModifiedUserId, long actorUserId, long adminUserId);

  /**
   * Cancel review template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  VoidDTO cancelReviewTemplate(
      long orgId, long templateId, long lastModifiedUserId, long actorUserId, long adminUserId);

  /**
   * List review template by template ids
   * @param orgId
   * @param templateIds
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  ReviewTemplateListDTO listReviewTemplateByTemplateIds(
      long orgId, List<Long> templateIds, long actorUserId, long adminUserId);

  /**
   * Get review template report
   * @param orgId
   * @param templateId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  ReviewReportDTO getReviewTemplateReport(long orgId, long templateId, long actorUserId, long adminUserId);

  /**
   * Get review activities of template
   * @param orgId
   * @param templateId
   * @param orderBy
   * @param direction
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  ReviewActivityUserListDTO getActivitiesOfTemplate(
      long orgId, long templateId, String orderBy, String direction, long actorUserId, long adminUserId);

  /**
   *
   * @param orgID
   * @param actorUserID
   * @return
     */
  @ThriftMethod
  public ReviewTemplateListDTO listAllValidTemplatesForActivitiesOfHomepage(
          long orgID, long actorUserID);

  /**
   *
   * @param orgID
   * @param actorUserID
   * @return
     */
  @ThriftMethod
  public ReviewTemplateContainUserProfileListDTO listAllValidTemplatesForInvitationsOfHomepage(
          long orgID, long actorUserID);

  @ThriftMethod
  public VoidDTO initReviewGuide(
          long orgId,
          long userId,
          String stringJsonTemplatesInfo,
          HashMap<Long, Long> mapUserNumberToId,
          HashMap<Long, Long> mapTeamNumberToId
  );

}
