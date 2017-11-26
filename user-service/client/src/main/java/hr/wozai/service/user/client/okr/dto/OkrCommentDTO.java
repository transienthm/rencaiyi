package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/18
 */
@ThriftStruct
public final class OkrCommentDTO extends BaseThriftObject {
  private ServiceStatusDTO serviceStatusDTO;

  private Long okrCommentId;

  private Long orgId;

  private Long objectiveId;

  private Long userId;

  private String content;

  private List<OkrUpdateLogDTO> okrUpdateLogDTOList;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private CoreUserProfileDTO actorUserProfile;

  private boolean isEditable;

  private boolean isDeletable;

  private String keyResultContent;

  private Long keyResultId;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getOkrCommentId() {
    return okrCommentId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getObjectiveId() {
    return objectiveId;
  }

  @ThriftField(5)
  public Long getUserId() {
    return userId;
  }

  @ThriftField(6)
  public String getContent() {
    return content;
  }

  @ThriftField(7)
  public List<OkrUpdateLogDTO> getOkrUpdateLogDTOList() {
    return okrUpdateLogDTOList;
  }

  @ThriftField(8)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(9)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(10)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(11)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField(12)
  public CoreUserProfileDTO getActorUserProfile() {
    return actorUserProfile;
  }

  @ThriftField(13)
  public boolean isDeletable() {
    return isDeletable;
  }

  @ThriftField(14)
  public String getKeyResultContent() {
    return keyResultContent;
  }

  @ThriftField(15)
  public boolean isEditable() {
    return isEditable;
  }

  @ThriftField(16)
  public Long getKeyResultId() {
    return keyResultId;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOkrCommentId(Long okrCommentId) {
    this.okrCommentId = okrCommentId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setObjectiveId(Long objectiveId) {
    this.objectiveId = objectiveId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField
  public void setContent(String content) {
    this.content = content;
  }

  @ThriftField
  public void setOkrUpdateLogDTOList(List<OkrUpdateLogDTO> okrUpdateLogDTOList) {
    this.okrUpdateLogDTOList = okrUpdateLogDTOList;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField
  public void setActorUserProfile(CoreUserProfileDTO actorUserProfile) {
    this.actorUserProfile = actorUserProfile;
  }

  @ThriftField
  public void setDeletable(boolean deletable) {
    isDeletable = deletable;
  }

  @ThriftField
  public void setKeyResultContent(String keyResultContent) {
    this.keyResultContent = keyResultContent;
  }

  @ThriftField
  public void setEditable(boolean editable) {
    isEditable = editable;
  }

  @ThriftField
  public void setKeyResultId(Long keyResultId) {
    this.keyResultId = keyResultId;
  }
}
