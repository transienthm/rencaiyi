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
public final class OkrRemindSettingListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<OkrRemindSettingDTO> okrRemindSettingDTOList;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<OkrRemindSettingDTO> getOkrRemindSettingDTOList() {
    return okrRemindSettingDTOList;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOkrRemindSettingDTOList(List<OkrRemindSettingDTO> okrRemindSettingDTOList) {
    this.okrRemindSettingDTOList = okrRemindSettingDTOList;
  }
}
