package hr.wozai.service.nlp.client.labelcloud.dto;

import java.util.List;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

@ThriftStruct
public final class LabelCloudListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;
  private List<LabelCloudDTO> labelCloudDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return this.serviceStatusDTO;
  }

  @ThriftField(2)
  public List<LabelCloudDTO> getLabelCloudDTOs() {
    return this.labelCloudDTOs;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setLabelCloudDTOs(List<LabelCloudDTO> labelCloudDTOs) {
    this.labelCloudDTOs = labelCloudDTOs;
  }
}
