// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringListDTO;
import hr.wozai.service.user.client.userorg.dto.AddressRegionListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferRequestDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferResponseDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferResponseListDTO;
import hr.wozai.service.user.client.userorg.dto.StatusUpdateDTO;
import hr.wozai.service.user.client.userorg.dto.StatusUpdateListDTO;
import hr.wozai.service.user.client.userorg.dto.UserEmploymentDTO;
import hr.wozai.service.user.client.userorg.dto.UserProfileDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-10
 */
@ThriftService
public interface UserProfileFacade {

  /*********************** User Profile relevant ***********************/

  @ThriftMethod
  UserProfileDTO getUserProfile(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateUserProfileStatus(long orgId, long userId, int userStatus, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateUserProfileField(
      long orgId, long userId, Map<String, String> fieldValues, long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileDTO getCoreUserProfile(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileListDTO listCoreUserProfile(long orgId, List<Long> userIds, long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileListDTO listCoreUserProfileOfNewStaffByOrgId(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileListDTO listCoreUserProfileOfEnrollAnniversaryByOrgId(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  AddressRegionListDTO listAddressRegion(long orgId, long parentId, long actorUserId, long adminUserId);

  /*********************** Employment management relevant ***********************/

  @ThriftMethod
  VoidDTO updateUserEmployment(long orgId, UserEmploymentDTO userEmploymentDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  LongDTO addJobTransfer(long orgId, JobTransferRequestDTO jobTransferRequestDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  JobTransferResponseDTO getJobTransfer(long orgId, long jobTransferid, long actorUserId, long adminUserId);

  @ThriftMethod
  JobTransferResponseListDTO listJobTransfer(
      long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  JobTransferResponseListDTO listJobTransferByJobTransferIds(
      long orgId, List<Long> jobTransferIds, long actorUserId, long adminUserId);

  @ThriftMethod
  LongDTO addPassProbationStatusUpdate(long orgId, StatusUpdateDTO statusUpdateDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public LongDTO addResignStatusUpdate(long orgId, StatusUpdateDTO statusUpdateDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  StatusUpdateDTO getStatusUpdate(long orgId, long statusUpdateId, long actorUserId, long adminUserId);

  @ThriftMethod
  StatusUpdateListDTO listStatusUpdate(
      long orgId, int statusType, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  StatusUpdateListDTO listStatusUpdateByStatusUpdateIds(
      long orgId, List<Long> statusUpdateIds, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO revokePassProbationStatusUpdate(
      long orgId, long statusUpdateId, int statusUpdateType, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO revokeResignStatusUpdate(
      long orgId, long statusUpdateId, int statusUpdateType, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO deleteUser(long orgId, long userId, long actorUserId);

  /*********************** Import roster data ***********************/

  @ThriftMethod
  StringDTO getHeaderOfRosterFile(long orgId, List<String> refefenceNames, long actorUserId, long adminUserId);

  @ThriftMethod
  StringListDTO listStaffOfRosterFile(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  IntegerDTO batchUpdateRosterData(
      long orgId, List<String> referenceNames, List<List<String>> rawFieldLists, long actorUserId, long adminUserId);

}

