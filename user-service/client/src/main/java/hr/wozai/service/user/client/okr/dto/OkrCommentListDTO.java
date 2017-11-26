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
public final class OkrCommentListDTO extends BaseThriftObject {
  private ServiceStatusDTO serviceStatusDTO;

  private List<OkrCommentDTO> okrCommentDTOList;

  private long totalRecordNum;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<OkrCommentDTO> getOkrCommentDTOList() {
    return okrCommentDTOList;
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
  public void setOkrCommentDTOList(List<OkrCommentDTO> okrCommentDTOList) {
    this.okrCommentDTOList = okrCommentDTOList;
  }

  @ThriftField
  public void setTotalRecordNum(long totalRecordNum) {
    this.totalRecordNum = totalRecordNum;
  }
}
