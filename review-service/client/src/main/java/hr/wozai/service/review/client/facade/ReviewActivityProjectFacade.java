// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.ReviewProjectDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@ThriftService
public interface ReviewActivityProjectFacade {

  /**
   * Insert activity project
   * @param orgId
   * @param activityId
   * @param reviewProjectDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  public LongDTO insertProject(long orgId, long activityId,
                               ReviewProjectDTO reviewProjectDTO,
                               long actorUserId, long adminUserId);

  /**
   * Delete activity project
   * @param orgId
   * @param activityId
   * @param projectId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  public VoidDTO deleteProject(long orgId, long activityId, long projectId,
                               long actorUserId, long adminUserId);

  /**
   * Update activity project
   * @param orgId
   * @param activityId
   * @param reviewProjectDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  public VoidDTO updateProject(long orgId, long activityId,
                               ReviewProjectDTO reviewProjectDTO,
                               long actorUserId, long adminUserId);

  /**
   * Get activity project detail
   * @param orgId
   * @param activityId
   * @param projectId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  public ReviewProjectDetailDTO getActivityProjectDetail(long orgId, long activityId, long projectId,
                                                         long actorUserId, long adminUserId);

  /**
   * Get activity project detail by HR
   * @param orgId
   * @param activityId
   * @param projectId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewProjectDetailDTO getActivityProjectDetailByHR(long orgId, long activityId, long projectId,
                                                             long actorUserId, long adminUserId);

}
