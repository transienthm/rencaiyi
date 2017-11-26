// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.conversation.dto.*;
import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@ThriftService
public interface ConvrFacade {


  /****************************** ConvrSchedule ******************************/


  /**
   * 创建 ConvrSchedule
   *
   * @param orgId
   * @param convrScheduleDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO addConvrSchedule(long orgId, ConvrScheduleDTO convrScheduleDTO, long actorUserId, long adminUserId);

  /**
   * 获取单个 ConvrSchedule
   *
   * @param orgId
   * @param convrScheduleId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ConvrScheduleDTO getConvrSchedule(long orgId, long convrScheduleId, long actorUserId, long adminUserId);

  /**
   * 获取本人的 ConvrSchedule 列表
   *
   * @param orgId
   * @param sourceUserId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ConvrScheduleListDTO listConvrScheduleBySourceUserId(
      long orgId, long sourceUserId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  /**
   * 更新 ConvrSchedule
   *
   * @param orgId
   * @param convrScheduleDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateConvrSchedule(long orgId, ConvrScheduleDTO convrScheduleDTO, long actorUserId, long adminUserId);

  /**
   * 获取该用户所有 ConvrSchedule 下的受邀 userId 列表
   *
   * @param orgId
   * @param sourceUserId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongListDTO listTargetUserIdsOfSourceUser(long orgId, long sourceUserId, long actorUserId, long adminUserId);



  /****************************** ConvrRecord ******************************/


  /**
   * 创建 ConvrRecord
   *
   * @param orgId
   * @param convrRecordDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO addConvrRecord(long orgId, ConvrRecordDTO convrRecordDTO, long actorUserId, long adminUserId);

  /**
   * 获取单个 ConvrRecord
   *
   * @param orgId
   * @param convrRecordId
   * @param actorUserId
   * @param amdinUserId
   * @return
   */
  @ThriftMethod
  ConvrRecordDTO getConvrRecord(long orgId, long convrRecordId, long actorUserId, long amdinUserId);

  /**
   * 更新 ConvrRecord
   *
   * @param orgId
   * @param convrRecordDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateConvrRecord(long orgId, ConvrRecordDTO convrRecordDTO, long actorUserId, long adminUserId);

  /**
   * 获取发起人视角下的 ConvrRecord 记录列表
   * 包含本人发起和对方发起的 ConvrRecord
   *
   * @param orgId
   * @Param convrScheduleId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ConvrRecordListDTO listConvrRecordOfScheduleAsSourceUser(
      long orgId, long convrScheduleId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  /**
   * 获取受邀人视角下的 ConvrRecord 记录列表
   * 仅包含作为受邀人的 ConvrRecord
   *
   * @param orgId
   * @param targetUserId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ConvrRecordListDTO listConvrRecordByTargetUserId(
      long orgId, long targetUserId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  ConvrScheduleChartListDTO listConvrScheduleChartByOrgId(
          long orgId, int period, long actorUserId, long adminUserId);

  @ThriftMethod
  ConvrScheduleChartDTO getConvrScheduleChartInAMonth(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  ConvrSourceChartListDTO listConvrSourceChartListDTO(long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

}
