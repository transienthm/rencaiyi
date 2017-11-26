// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-29
 */
@ThriftStruct
public final class ReviewPastInvitationDTO extends BaseThriftObject {

  private Long invitationId;

  private String templateName;

  @ThriftField(1)
  public Long getInvitationId() {
    return invitationId;
  }

  @ThriftField
  public void setInvitationId(Long invitationId) {
    this.invitationId = invitationId;
  }

  @ThriftField(2)
  public String getTemplateName() {
    return templateName;
  }

  @ThriftField
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }
}
