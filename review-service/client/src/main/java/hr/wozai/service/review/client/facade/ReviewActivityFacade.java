// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.ReviewActivityDTO;
import hr.wozai.service.review.client.dto.ReviewActivityListDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.ReportLineDTO;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@ThriftService
public interface ReviewActivityFacade {

  /**
   * Batch insert review activities
   * @param orgId
   * @param templateId
   * @param reportLineDTOs
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO batchInsertReviewActivities(
      long orgId, long templateId, List<ReportLineDTO> reportLineDTOs, long actorUserId, long adminUserId);

  /**
   * Find review activity
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewActivityDTO findReviewActivity(long orgId, long activityId, long actorUserId, long adminUserId);

  /**
   * Find reviewActivity by templateId and userId
   *
   * @param orgId
   * @param templateId
   * @param userId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  public ReviewActivityDTO findReviewActivityByTemplateIdAndUserId(
      long orgId, long templateId, long userId, long actorUserId, long adminUserId);

  /**
   * List unSubmitted review activity as reviewee
   * @param orgId
   * @param revieweeId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewActivityListDTO listUnSubmittedReviewActivity(
      long orgId, long revieweeId, long actorUserId, long adminUserId);

  /**
   * List other review activity as reviewee
   * @param orgId
   * @param revieweeId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewActivityListDTO listOtherReviewActivity(
      long orgId, long revieweeId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  /**
   * Count other review activity
   * @param orgId
   * @param revieweeId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO countOtherReviewActivity(long orgId, long revieweeId, long actorUserId, long adminUserId);
}
