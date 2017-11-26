package hr.wozai.service.user.client.survey.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/30
 */
@ThriftStruct
public final class SurveyActivityDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long surveyActivityId;

  private Long orgId;

  private Long createdTime;

  private List<SurveyResponseDTO> surveyResponseDTOs;

  private String averageScore;

  private String percentage;

  private int totalResponseNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getSurveyActivityId() {
    return surveyActivityId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(5)
  public List<SurveyResponseDTO> getSurveyResponseDTOs() {
    return surveyResponseDTOs;
  }

  @ThriftField(6)
  public String getAverageScore() {
    return averageScore;
  }

  @ThriftField(7)
  public String getPercentage() {
    return percentage;
  }

  @ThriftField(8)
  public int getTotalResponseNumber() {
    return totalResponseNumber;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setSurveyActivityId(Long surveyActivityId) {
    this.surveyActivityId = surveyActivityId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setSurveyResponseDTOs(List<SurveyResponseDTO> surveyResponseDTOs) {
    this.surveyResponseDTOs = surveyResponseDTOs;
  }

  @ThriftField
  public void setAverageScore(String averageScore) {
    this.averageScore = averageScore;
  }

  @ThriftField
  public void setPercentage(String percentage) {
    this.percentage = percentage;
  }

  @ThriftField
  public void setTotalResponseNumber(int totalResponseNumber) {
    this.totalResponseNumber = totalResponseNumber;
  }
}
