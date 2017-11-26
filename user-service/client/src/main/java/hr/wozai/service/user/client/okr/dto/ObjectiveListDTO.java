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
public final class ObjectiveListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<ObjectiveDTO> objectiveDTOList;

  private String totalProgress;

  private long beginTimestamp;

  private long endTimestamp;

  private long totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<ObjectiveDTO> getObjectiveDTOList() {
    return objectiveDTOList;
  }

  @ThriftField(3)
  public String getTotalProgress() {
    return totalProgress;
  }

  @ThriftField(4)
  public long getBeginTimestamp() {
    return beginTimestamp;
  }

  @ThriftField(5)
  public long getEndTimestamp() {
    return endTimestamp;
  }

  @ThriftField(6)
  public long getTotalNumber() {
    return totalNumber;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setObjectiveDTOList(List<ObjectiveDTO> objectiveDTOList) {
    this.objectiveDTOList = objectiveDTOList;
  }

  @ThriftField
  public void setTotalProgress(String totalProgress) {
    this.totalProgress = totalProgress;
  }

  @ThriftField
  public void setBeginTimestamp(long beginTimestamp) {
    this.beginTimestamp = beginTimestamp;
  }

  @ThriftField
  public void setEndTimestamp(long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  @ThriftField
  public void setTotalNumber(long totalNumber) {
    this.totalNumber = totalNumber;
  }
}
