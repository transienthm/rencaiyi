// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.document;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
@Data
@NoArgsConstructor
public class Document {

  private Long documentId;

  private Long orgId;

  private Integer scenario;

  private String documentKey;

  private String documentName;

  private String documentType;

  private String md5Hash;

  private String description;

  private Long documentSize;

  private Integer storageStatus;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
