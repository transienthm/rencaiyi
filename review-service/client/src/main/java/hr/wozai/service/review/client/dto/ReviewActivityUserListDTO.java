package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-12
 */
@ThriftStruct
public final class ReviewActivityUserListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<ReviewActivityUserDTO> activityUserDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<ReviewActivityUserDTO> getActivityUserDTOs() {
    return activityUserDTOs;
  }

  @ThriftField
  public void setActivityUserDTOs(List<ReviewActivityUserDTO> activityUserDTOs) {
    this.activityUserDTOs = activityUserDTOs;
  }

}
