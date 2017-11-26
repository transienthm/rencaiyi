package hr.wozai.service.user.client.survey.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
@ThriftStruct
public final class NewSurveyItemDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  public Long surveyItemId;

  public Long orgId;

  public Integer surveyItemType;

  public String question;

  public String description;

  public String lowLabel;

  public String highLabel;

  public Long startTime;

  public Long endTime;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  private Integer status;

  private boolean deletable;

  private List<NewSurveyResponseDTO> surveyResponseDTOs;
  private int totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getSurveyItemId() {
    return surveyItemId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Integer getSurveyItemType() {
    return surveyItemType;
  }

  @ThriftField(5)
  public String getQuestion() {
    return question;
  }

  @ThriftField(6)
  public String getDescription() {
    return description;
  }

  @ThriftField(7)
  public String getLowLabel() {
    return lowLabel;
  }

  @ThriftField(8)
  public String getHighLabel() {
    return highLabel;
  }

  @ThriftField(9)
  public Long getStartTime() {
    return startTime;
  }

  @ThriftField(10)
  public Long getEndTime() {
    return endTime;
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

  @ThriftField(15)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField(16)
  public Integer getStatus() {
    return status;
  }

  @ThriftField(17)
  public boolean isDeletable() {
    return deletable;
  }

  @ThriftField(18)
  public List<NewSurveyResponseDTO> getSurveyResponseDTOs() {
    return surveyResponseDTOs;
  }

  @ThriftField(19)
  public int getTotalNumber() {
    return totalNumber;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setSurveyItemId(Long surveyItemId) {
    this.surveyItemId = surveyItemId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setSurveyItemType(Integer surveyItemType) {
    this.surveyItemType = surveyItemType;
  }

  @ThriftField
  public void setQuestion(String question) {
    this.question = question;
  }

  @ThriftField
  public void setDescription(String description) {
    this.description = description;
  }

  @ThriftField
  public void setLowLabel(String lowLabel) {
    this.lowLabel = lowLabel;
  }

  @ThriftField
  public void setHighLabel(String highLabel) {
    this.highLabel = highLabel;
  }

  @ThriftField
  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @ThriftField
  public void setEndTime(Long endTime) {
    this.endTime = endTime;
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

  @ThriftField
  public void setStatus(Integer status) {
    this.status = status;
  }

  @ThriftField
  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }

  @ThriftField
  public void setSurveyResponseDTOs(List<NewSurveyResponseDTO> surveyResponseDTOs) {
    this.surveyResponseDTOs = surveyResponseDTOs;
  }

  @ThriftField
  public void setTotalNumber(int totalNumber) {
    this.totalNumber = totalNumber;
  }
}
