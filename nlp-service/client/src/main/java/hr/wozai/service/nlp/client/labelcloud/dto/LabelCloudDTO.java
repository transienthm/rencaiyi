package hr.wozai.service.nlp.client.labelcloud.dto;

import java.util.List;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

@ThriftStruct
public final class LabelCloudDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long cloudId;
  private Long orgId;
  private Long surveyActivityId;
  private Long surveyItemId;
  private Long cloudVersion;
  private List<LabelDTO> labelClouds;
  private Long createdTime;
  private Long lastModifiedTime;
  private Integer isDeleted;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return this.serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getCloudId() {
    return this.cloudId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return this.orgId;
  }

  @ThriftField(4)
  public Long getSurveyActivityId() {
    return this.surveyActivityId;
  }

  @ThriftField(5)
  public Long getSurveyItemId() {
    return this.surveyItemId;
  }

  @ThriftField(6)
  public Long getCloudVersion() {
    return this.cloudVersion;
  }

  @ThriftField(7)
  public List<LabelDTO> getLabelClouds() {
    return this.labelClouds;
  }

  @ThriftField(8)
  public Long getCreatedTime() {
    return this.createdTime;
  }

  @ThriftField(9)
  public Long getLastModifiedTime() {
    return this.lastModifiedTime;
  }

  @ThriftField(10)
  public Integer getIsDeleted() {
    return this.isDeleted;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setCloudId(Long cloudId) {
    this.cloudId = cloudId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setSurveyActivityId(Long surveyActivityId) {
    this.surveyActivityId = surveyActivityId;
  }

  @ThriftField
  public void setSurveyItemId(Long surveyItemId) {
    this.surveyItemId = surveyItemId;
  }

  @ThriftField
  public void setCloudVersion(Long cloudVersion) {
    this.cloudVersion = cloudVersion;
  }

  @ThriftField
  public void setLabelClouds(List<LabelDTO> labelClouds) {
    this.labelClouds = labelClouds;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
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
