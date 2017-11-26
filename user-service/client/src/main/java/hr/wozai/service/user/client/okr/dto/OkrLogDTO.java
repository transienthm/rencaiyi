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
public final class OkrLogDTO extends BaseThriftObject {
  private ServiceStatusDTO serviceStatusDTO;

  private Long okrLogId;

  private Long orgId;

  private Long objectiveId;

  private Integer type;

  private String content;

  private List<String> atUsers;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getOkrLogId() {
    return okrLogId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getObjectiveId() {
    return objectiveId;
  }

  @ThriftField(5)
  public String getContent() {
    return content;
  }

  @ThriftField(6)
  public List<String> getAtUsers() {
    return atUsers;
  }

  @ThriftField(7)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(8)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(9)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(10)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField(11)
  public Integer getType() {
    return type;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOkrLogId(Long okrLogId) {
    this.okrLogId = okrLogId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setObjectiveId(Long objectiveId) {
    this.objectiveId = objectiveId;
  }

  @ThriftField
  public void setContent(String content) {
    this.content = content;
  }

  @ThriftField
  public void setAtUsers(List<String> atUsers) {
    this.atUsers = atUsers;
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

  @ThriftField
  public void setType(Integer type) {
    this.type = type;
  }
}
