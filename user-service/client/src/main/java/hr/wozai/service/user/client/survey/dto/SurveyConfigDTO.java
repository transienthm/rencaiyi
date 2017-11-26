package hr.wozai.service.user.client.survey.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
@ThriftStruct
public final class SurveyConfigDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long surveyConfigId;

  private Long orgId;

  private Integer frequency;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getSurveyConfigId() {
    return surveyConfigId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Integer getFrequency() {
    return frequency;
  }

  @ThriftField(5)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(6)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(7)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(8)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField(9)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setSurveyConfigId(Long surveyConfigId) {
    this.surveyConfigId = surveyConfigId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
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
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }
}