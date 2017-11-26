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
public final class SurveyItemListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<SurveyItemDTO> surveyItemDTOs;

  private long totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<SurveyItemDTO> getSurveyItemDTOs() {
    return surveyItemDTOs;
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
  public void setSurveyItemDTOs(List<SurveyItemDTO> surveyItemDTOs) {
    this.surveyItemDTOs = surveyItemDTOs;
  }

  @ThriftField
  public void setTotalNumber(long totalNumber) {
    this.totalNumber = totalNumber;
  }
}
