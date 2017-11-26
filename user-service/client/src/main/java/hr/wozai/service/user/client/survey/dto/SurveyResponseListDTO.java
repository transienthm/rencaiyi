package hr.wozai.service.user.client.survey.dto;

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
public final class SurveyResponseListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private SurveyActivityDTO surveyActivityDTO;

  private List<SurveyResponseDTO> surveyResponseDTOs;

  private int averageScore;
}
