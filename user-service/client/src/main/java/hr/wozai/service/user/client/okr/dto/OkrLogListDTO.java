package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/18
 */
@ThriftStruct
public final class OkrLogListDTO extends BaseThriftObject {
  private ServiceStatusDTO serviceStatusDTO;

  private List<OkrLogDTO> okrLogDTOList;

  private long totalRecordNum;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<OkrLogDTO> getOkrLogDTOList() {
    return okrLogDTOList;
  }

  @ThriftField(3)
  public long getTotalRecordNum() {
    return totalRecordNum;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOkrLogDTOList(List<OkrLogDTO> okrLogDTOList) {
    this.okrLogDTOList = okrLogDTOList;
  }

  @ThriftField
  public void setTotalRecordNum(long totalRecordNum) {
    this.totalRecordNum = totalRecordNum;
  }
}
