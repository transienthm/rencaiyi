package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/18
 */
@ThriftStruct
public final class OkrUpdateLogDTO extends BaseThriftObject {
  private ServiceStatusDTO serviceStatusDTO;

  private Long okrUpdateLogId;

  private Long orgId;

  private Long okrCommentId;

  private String attribute;

  private String beforeValue;

  private String afterValue;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getOkrUpdateLogId() {
    return okrUpdateLogId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getOkrCommentId() {
    return okrCommentId;
  }

  @ThriftField(6)
  public String getAttribute() {
    return attribute;
  }

  @ThriftField(7)
  public String getBeforeValue() {
    return beforeValue;
  }

  @ThriftField(8)
  public String getAfterValue() {
    return afterValue;
  }

  @ThriftField(9)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(10)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(11)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(12)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOkrUpdateLogId(Long okrUpdateLogId) {
    this.okrUpdateLogId = okrUpdateLogId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setOkrCommentId(Long okrCommentId) {
    this.okrCommentId = okrCommentId;
  }

  @ThriftField
  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  @ThriftField
  public void setBeforeValue(String beforeValue) {
    this.beforeValue = beforeValue;
  }

  @ThriftField
  public void setAfterValue(String afterValue) {
    this.afterValue = afterValue;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }
}
