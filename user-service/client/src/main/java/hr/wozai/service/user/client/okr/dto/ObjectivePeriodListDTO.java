package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftStruct
public final class ObjectivePeriodListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<ObjectivePeriodDTO> periodDTOList;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<ObjectivePeriodDTO> getPeriodDTOList() {
    return periodDTOList;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setPeriodDTOList(List<ObjectivePeriodDTO> periodDTOList) {
    this.periodDTOList = periodDTOList;
  }
}
