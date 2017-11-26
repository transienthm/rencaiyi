// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public interface OssAvatarService {

  String generatePresignedPutUrlFromAvatarKey(String avatarKey, long effectiveTime);

  String generatePresignedPutUrlFromPublicGetUrl(String publicGetUrl, long effectiveTime);

  String generatePublicGetUrlFromAvatarKey(String avatarKey);

}
