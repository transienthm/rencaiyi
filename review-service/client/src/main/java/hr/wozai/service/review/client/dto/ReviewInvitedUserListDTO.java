// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@ThriftStruct
public final class ReviewInvitedUserListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private ReviewInvitedUserDTO managerUserDTO;

  private List<ReviewInvitedUserDTO> reviewInvitedUserDTOs;

  private Integer isAddable;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public ReviewInvitedUserDTO getManagerUserDTO() {
    return managerUserDTO;
  }

  @ThriftField
  public void setManagerUserDTO(ReviewInvitedUserDTO managerUserDTO) {
    this.managerUserDTO = managerUserDTO;
  }

  @ThriftField(3)
  public List<ReviewInvitedUserDTO> getReviewInvitedUserDTOs() {
    return reviewInvitedUserDTOs;
  }

  @ThriftField
  public void setReviewInvitedUserDTOs(List<ReviewInvitedUserDTO> reviewInvitedUserDTOs) {
    this.reviewInvitedUserDTOs = reviewInvitedUserDTOs;
  }

  @ThriftField(4)
  public Integer getIsAddable() {
    return isAddable;
  }

  @ThriftField
  public void setIsAddable(Integer isAddable) {
    this.isAddable = isAddable;
  }
}
