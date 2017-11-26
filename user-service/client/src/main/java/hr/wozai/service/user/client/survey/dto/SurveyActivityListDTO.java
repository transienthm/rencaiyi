package hr.wozai.service.user.client.survey.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;
import lombok.Data;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/12/6
 */
@ThriftStruct
public final class SurveyActivityListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<SurveyActivityDTO> surveyActivityDTOs;

  private long totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<SurveyActivityDTO> getSurveyActivityDTOs() {
    return surveyActivityDTOs;
  }

  @ThriftField(3)
  public long getTotalNumber() {
    return totalNumber;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setSurveyActivityDTOs(List<SurveyActivityDTO> surveyActivityDTOs) {
    this.surveyActivityDTOs = surveyActivityDTOs;
  }

  @ThriftField
  public void setTotalNumber(long totalNumber) {
    this.totalNumber = totalNumber;
  }
}
