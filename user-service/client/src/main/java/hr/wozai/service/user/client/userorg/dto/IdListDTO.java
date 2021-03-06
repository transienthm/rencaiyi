package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/2
 */
@ThriftStruct
public final class IdListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<Long> idList;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<Long> getIdList() {
    return idList;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setIdList(List<Long> idList) {
    this.idList = idList;
  }
}
