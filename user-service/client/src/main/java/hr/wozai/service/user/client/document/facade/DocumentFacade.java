// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.document.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@ThriftService
public interface DocumentFacade {

  /**
   * 添加Document
   *
   * @param orgId
   * @param documentDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  S3DocumentRequestDTO addDocument(long orgId, DocumentDTO documentDTO, long actorUserId, long adminUserId);

  /**
   * 获取单个Document
   *
   * @param orgId
   * @param documentId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  DocumentDTO getDocument(long orgId, long documentId, long actorUserId, long adminUserId);

  /**
   * Request GET url
   *
   * @param orgId
   * @param documentId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  S3DocumentRequestDTO downloadDocument(long orgId, long documentId, long actorUserId, long adminUserId);

  /**
   * 获取Org下的document列表
   *
   * @param orgId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  DocumentListDTO listDocument(long orgId, long actorUserId, long adminUserId);

  /**
   * 更新文件名
   *
   * @param orgId
   * @param documentDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateDocument(long orgId, DocumentDTO documentDTO, long actorUserId, long adminUserId);

  /**
   * 删除文件
   *
   * @param orgId
   * @param documentId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO  deleteDocument(long orgId, long documentId, long actorUserId, long adminUserId);

}
