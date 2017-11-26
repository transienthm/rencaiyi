// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.client.onboarding.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-17
 */
@ThriftStruct
public final class SuperAdminDTO extends BaseThriftObject {

  /**
   * TODO: delete after @LPJ setup for existed orgs
   */
  private Long orgId;

  private String emailAddress;

  private String passwordPlainText;

  private String usageSecret;

  @ThriftField(1)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(2)
  public String getEmailAddress() {
    return emailAddress;
  }

  @ThriftField
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @ThriftField(3)
  public String getPasswordPlainText() {
    return passwordPlainText;
  }

  @ThriftField
  public void setPasswordPlainText(String passwordPlainText) {
    this.passwordPlainText = passwordPlainText;
  }

  @ThriftField(4)
  public String getUsageSecret() {
    return usageSecret;
  }

  @ThriftField
  public void setUsageSecret(String usageSecret) {
    this.usageSecret = usageSecret;
  }
}
