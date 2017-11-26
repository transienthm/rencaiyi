package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/12
 */
@ThriftStruct
public final class MessageListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<MessageDTO> messageDTOs;

  private int unReadNumber;

  private Integer totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<MessageDTO> getMessageDTOs() {
    return messageDTOs;
  }

  @ThriftField(3)
  public int getUnReadNumber() {
    return unReadNumber;
  }

  @ThriftField(4)
  public Integer getTotalNumber() {
    return totalNumber;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setMessageDTOs(List<MessageDTO> messageDTOs) {
    this.messageDTOs = messageDTOs;
  }

  @ThriftField
  public void setUnReadNumber(int unReadNumber) {
    this.unReadNumber = unReadNumber;
  }

  @ThriftField
  public void setTotalNumber(Integer totalNumber) {
    this.totalNumber = totalNumber;
  }
}

