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
 * @created 16/12/6
 */
@ThriftStruct
public final class SurveyItemHistoryListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<SurveyItemHistoryDTO> surveyItemHistoryDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<SurveyItemHistoryDTO> getSurveyItemHistoryDTOs() {
    return surveyItemHistoryDTOs;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setSurveyItemHistoryDTOs(List<SurveyItemHistoryDTO> surveyItemHistoryDTOs) {
    this.surveyItemHistoryDTOs = surveyItemHistoryDTOs;
  }
}
