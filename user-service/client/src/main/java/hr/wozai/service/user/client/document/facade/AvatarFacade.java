// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.document.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.document.dto.OssAvatarPutRequestDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@ThriftService
public interface AvatarFacade {

  /**
   * 创建avatar
   * 申请 presignedPutUrl & publicGetUrl
   *
   * @param orgId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  OssAvatarPutRequestDTO addAvatar(long orgId, String x, String y, String e, long actorUserId, long adminUserId);

  /**
   * 更新avatar
   * 解析已有的 publicGetUrl, 并生成 presignedPutUrl
   *
   * @param publicGetUrl
   * @param orgId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  OssAvatarPutRequestDTO updateAvatar(
      long orgId, String publicGetUrl, String x, String y, String e, long actorUserId, long adminUserId);

}
