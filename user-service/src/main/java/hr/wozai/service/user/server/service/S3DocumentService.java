// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public interface S3DocumentService {

  String generatePresignedPutUrl(String documentKey, long effectiveTime);

  String generatePresignedGetUrl(String documentKey, String documentName, long effectiveTime);

}
