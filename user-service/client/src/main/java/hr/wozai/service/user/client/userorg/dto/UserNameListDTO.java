package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/21
 */
@ThriftStruct
public final class UserNameListDTO {
  private ServiceStatusDTO serviceStatusDTO;

  private List<Long> idList;

  private long totalRecordNum;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<Long> getIdList() {
    return idList;
  }

  @ThriftField
  public void setIdList(
          List<Long> idList) {
    this.idList = idList;
  }

  @ThriftField(3)
  public long getTotalRecordNum() {
    return totalRecordNum;
  }

  @ThriftField
  public void setTotalRecordNum(long totalRecordNum) {
    this.totalRecordNum = totalRecordNum;
  }
}
