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
public final class NewSurveyResponseDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long surveyResponseId;

  private Long orgId;

  private Long userId;

  private Long surveyActivityId;

  private Long surveyItemId;

  private Integer surveyItemType;

  private Integer response;

  private String responseDetail;

  private Integer isSubmit;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getSurveyResponseId() {
    return surveyResponseId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getUserId() {
    return userId;
  }

  @ThriftField(5)
  public Long getSurveyActivityId() {
    return surveyActivityId;
  }

  @ThriftField(6)
  public Long getSurveyItemId() {
    return surveyItemId;
  }

  @ThriftField(7)
  public Integer getSurveyItemType() {
    return surveyItemType;
  }

  @ThriftField(8)
  public Integer getResponse() {
    return response;
  }

  @ThriftField(9)
  public String getResponseDetail() {
    return responseDetail;
  }

  @ThriftField(10)
  public Integer getIsSubmit() {
    return isSubmit;
  }

  @ThriftField(11)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(12)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(13)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(14)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setSurveyResponseId(Long surveyResponseId) {
    this.surveyResponseId = surveyResponseId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
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
  public void setSurveyItemType(Integer surveyItemType) {
    this.surveyItemType = surveyItemType;
  }

  @ThriftField
  public void setResponse(Integer response) {
    this.response = response;
  }

  @ThriftField
  public void setResponseDetail(String responseDetail) {
    this.responseDetail = responseDetail;
  }

  @ThriftField
  public void setIsSubmit(Integer isSubmit) {
    this.isSubmit = isSubmit;
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
