package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftStruct
public final class KeyResultDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long keyResultId;

  private Long orgId;

  private String content;

  private Long objectiveId;

  private Integer priority;

  private String process;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private List<DirectorDTO> directorDTOList;

  private String progress;

  private Integer progressMetricType;

  private String startingAmount;

  private String goalAmount;

  private String currentAmount;

  private String unit;

  private Long deadline;

  private String comment;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getKeyResultId() {
    return keyResultId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public String getContent() {
    return content;
  }

  @ThriftField(5)
  public Long getObjectiveId() {
    return objectiveId;
  }

  @ThriftField(6)
  public Integer getPriority() {
    return priority;
  }

  @ThriftField(7)
  public String getProcess() {
    return process;
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
  public List<DirectorDTO> getDirectorDTOList() {
    return directorDTOList;
  }

  @ThriftField(13)
  public String getProgress() {
    return progress;
  }

  @ThriftField(14)
  public Integer getProgressMetricType() {
    return progressMetricType;
  }

  @ThriftField(15)
  public String getStartingAmount() {
    return startingAmount;
  }

  @ThriftField(16)
  public String getGoalAmount() {
    return goalAmount;
  }

  @ThriftField(17)
  public String getCurrentAmount() {
    return currentAmount;
  }

  @ThriftField(18)
  public String getUnit() {
    return unit;
  }

  @ThriftField(19)
  public String getComment() {
    return comment;
  }

  @ThriftField(20)
  public Long getDeadline() {
    return deadline;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setKeyResultId(Long keyResultId) {
    this.keyResultId = keyResultId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setContent(String content) {
    this.content = content;
  }

  @ThriftField
  public void setObjectiveId(Long objectiveId) {
    this.objectiveId = objectiveId;
  }

  @ThriftField
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  @ThriftField
  public void setProcess(String process) {
    this.process = process;
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
  public void setDirectorDTOList(List<DirectorDTO> directorDTOList) {
    this.directorDTOList = directorDTOList;
  }

  @ThriftField
  public void setProgress(String progress) {
    this.progress = progress;
  }

  @ThriftField
  public void setProgressMetricType(Integer progressMetricType) {
    this.progressMetricType = progressMetricType;
  }

  @ThriftField
  public void setStartingAmount(String startingAmount) {
    this.startingAmount = startingAmount;
  }

  @ThriftField
  public void setGoalAmount(String goalAmount) {
    this.goalAmount = goalAmount;
  }

  @ThriftField
  public void setCurrentAmount(String currentAmount) {
    this.currentAmount = currentAmount;
  }

  @ThriftField
  public void setUnit(String unit) {
    this.unit = unit;
  }

  @ThriftField
  public void setComment(String comment) {
    this.comment = comment;
  }

  @ThriftField
  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }
}
