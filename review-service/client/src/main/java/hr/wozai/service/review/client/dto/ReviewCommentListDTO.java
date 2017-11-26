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
 * @Created: 2016-03-09
 */
@ThriftStruct
public final class ReviewCommentListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<ReviewCommentDTO> reviewCommentDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<ReviewCommentDTO> getReviewCommentDTOs() {
    return reviewCommentDTOs;
  }

  @ThriftField
  public void setReviewCommentDTOs(List<ReviewCommentDTO> reviewCommentDTOs) {
    this.reviewCommentDTOs = reviewCommentDTOs;
  }

}
