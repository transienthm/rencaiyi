package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/18
 */
@ThriftStruct
public final class OkrLogCommentListDTO extends BaseThriftObject {
  private ServiceStatusDTO serviceStatusDTO;

  private List<OkrLogCommentDTO> okrLogCommentDTOList;

  private long totalRecordNum;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<OkrLogCommentDTO> getOkrLogCommentDTOList() {
    return okrLogCommentDTOList;
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
  public void setOkrLogCommentDTOList(List<OkrLogCommentDTO> okrLogCommentDTOList) {
    this.okrLogCommentDTOList = okrLogCommentDTOList;
  }

  @ThriftField
  public void setTotalRecordNum(long totalRecordNum) {
    this.totalRecordNum = totalRecordNum;
  }
}
