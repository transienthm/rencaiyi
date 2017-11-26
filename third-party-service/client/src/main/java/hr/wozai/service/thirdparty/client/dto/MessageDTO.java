package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/11
 */
@ThriftStruct
public final class MessageDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long messageId;

  private Long orgId;

  private List<Long> senders;

  private Integer templateId;

  // PERSONAL:0  SYSTEM:1
  private Integer type;

  private Long objectId;

  private Long receiverId;

  private String objectContent;

  //unread:0 read:1
  private Integer isRead;

  private Long createdTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getMessageId() {
    return messageId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public List<Long> getSenders() {
    return senders;
  }

  @ThriftField(5)
  public Integer getTemplateId() {
    return templateId;
  }

  @ThriftField(6)
  public Integer getType() {
    return type;
  }

  @ThriftField(7)
  public Long getObjectId() {
    return objectId;
  }

  @ThriftField(8)
  public Long getReceiverId() {
    return receiverId;
  }

  @ThriftField(9)
  public Integer getIsRead() {
    return isRead;
  }

  @ThriftField(10)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(11)
  public String getObjectContent() {
    return objectContent;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setSenders(List<Long> senders) {
    this.senders = senders;
  }

  @ThriftField
  public void setTemplateId(Integer templateId) {
    this.templateId = templateId;
  }

  @ThriftField
  public void setType(Integer type) {
    this.type = type;
  }

  @ThriftField
  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  @ThriftField
  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  @ThriftField
  public void setIsRead(Integer isRead) {
    this.isRead = isRead;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setObjectContent(String objectContent) {
    this.objectContent = objectContent;
  }
}
