// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
@ThriftStruct
public final class CommentListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<CommentDTO> commentDTOList;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<CommentDTO> getCommentDTOList() {
    return commentDTOList;
  }

  @ThriftField
  public void setCommentDTOList(List<CommentDTO> commentDTOList) {
    this.commentDTOList = commentDTOList;
  }

}
