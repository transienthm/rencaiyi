package hr.wozai.service.user.client.common.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/16
 */
@ThriftStruct
public final class RemindSettingListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<RemindSettingDTO> remindSettingDTOList;
@ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }
@ThriftField(2)
  public List<RemindSettingDTO> getRemindSettingDTOList() {
    return remindSettingDTOList;
  }
@ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }
@ThriftField
  public void setRemindSettingDTOList(List<RemindSettingDTO> remindSettingDTOList) {
    this.remindSettingDTOList = remindSettingDTOList;
  }
}
