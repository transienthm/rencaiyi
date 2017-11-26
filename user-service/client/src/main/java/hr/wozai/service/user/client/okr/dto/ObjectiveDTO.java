package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftStruct
public final class ObjectiveDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long objectiveId;

  private Long orgId;

  private Integer type;

  private Long ownerId;

  private String content;

  private Integer priority;

  private String process;

  private Long objectivePeriodId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private List<DirectorDTO> directorDTOList;

  private List<KeyResultDTO> keyResultDTOList;

  private CoreUserProfileDTO lastModifiedUserProfile;

  private String progress;

  private Integer isAutoCalc;

  private Integer progressMetricType;

  private String startingAmount;

  private String goalAmount;

  private String currentAmount;

  private String unit;

  private Long deadline;

  private Integer orderIndex;

  private Integer isPrivate;

  private Long parentObjectiveId;

  private String parentObjectiveName;

  private String comment;

  private String objectivePeriodOwnerName;

  private String objectivePeriodName;

  private Integer regularRemindType;

  private String objectivePeriodOwnerJobTitleName;

  private Integer hasSubordinate;

  private Integer hasParent;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getObjectiveId() {
    return objectiveId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Integer getType() {
    return type;
  }

  @ThriftField(5)
  public Long getOwnerId() {
    return ownerId;
  }

  @ThriftField(6)
  public String getContent() {
    return content;
  }

  @ThriftField(7)
  public Integer getPriority() {
    return priority;
  }

  @ThriftField(8)
  public String getProcess() {
    return process;
  }

  @ThriftField(9)
  public Long getObjectivePeriodId() {
    return objectivePeriodId;
  }

  @ThriftField(10)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(11)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(12)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(13)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField(14)
  public List<DirectorDTO> getDirectorDTOList() {
    return directorDTOList;
  }

  @ThriftField(15)
  public List<KeyResultDTO> getKeyResultDTOList() {
    return keyResultDTOList;
  }

  @ThriftField(16)
  public CoreUserProfileDTO getLastModifiedUserProfile() {
    return lastModifiedUserProfile;
  }

  @ThriftField(17)
  public String getProgress() {
    return progress;
  }

  @ThriftField(18)
  public Integer getIsAutoCalc() {
    return isAutoCalc;
  }

  @ThriftField(19)
  public Integer getProgressMetricType() {
    return progressMetricType;
  }

  @ThriftField(20)
  public String getStartingAmount() {
    return startingAmount;
  }

  @ThriftField(21)
  public String getGoalAmount() {
    return goalAmount;
  }

  @ThriftField(22)
  public String getCurrentAmount() {
    return currentAmount;
  }

  @ThriftField(23)
  public String getUnit() {
    return unit;
  }

  @ThriftField(24)
  public Long getDeadline() {
    return deadline;
  }

  @ThriftField(25)
  public Integer getOrderIndex() {
    return orderIndex;
  }

  @ThriftField(26)
  public Integer getIsPrivate() {
    return isPrivate;
  }

  @ThriftField(27)
  public Long getParentObjectiveId() {
    return parentObjectiveId;
  }

  @ThriftField(28)
  public String getComment() {
    return comment;
  }

  @ThriftField(29)
  public String getObjectivePeriodOwnerName() {
    return objectivePeriodOwnerName;
  }

  @ThriftField(30)
  public String getObjectivePeriodName() {
    return objectivePeriodName;
  }

  @ThriftField(31)
  public String getParentObjectiveName() {
    return parentObjectiveName;
  }

  @ThriftField(32)
  public Integer getRegularRemindType() {
    return regularRemindType;
  }

  @ThriftField(33)
  public String getObjectivePeriodOwnerJobTitleName() {
    return objectivePeriodOwnerJobTitleName;
  }

  @ThriftField(34)
  public Integer getHasSubordinate() {
    return hasSubordinate;
  }

  @ThriftField(35)
  public Integer getHasParent() {
    return hasParent;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setObjectiveId(Long objectiveId) {
    this.objectiveId = objectiveId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setType(Integer type) {
    this.type = type;
  }

  @ThriftField
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  @ThriftField
  public void setContent(String content) {
    this.content = content;
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
  public void setObjectivePeriodId(Long objectivePeriodId) {
    this.objectivePeriodId = objectivePeriodId;
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
  public void setKeyResultDTOList(List<KeyResultDTO> keyResultDTOList) {
    this.keyResultDTOList = keyResultDTOList;
  }

  @ThriftField
  public void setLastModifiedUserProfile(CoreUserProfileDTO lastModifiedUserProfile) {
    this.lastModifiedUserProfile = lastModifiedUserProfile;
  }

  @ThriftField
  public void setProgress(String progress) {
    this.progress = progress;
  }

  @ThriftField
  public void setIsAutoCalc(Integer isAutoCalc) {
    this.isAutoCalc = isAutoCalc;
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
  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  @ThriftField
  public void setOrderIndex(Integer orderIndex) {
    this.orderIndex = orderIndex;
  }

  @ThriftField
  public void setIsPrivate(Integer isPrivate) {
    this.isPrivate = isPrivate;
  }

  @ThriftField
  public void setParentObjectiveId(Long parentObjectiveId) {
    this.parentObjectiveId = parentObjectiveId;
  }

  @ThriftField
  public void setComment(String comment) {
    this.comment = comment;
  }

  @ThriftField
  public void setObjectivePeriodOwnerName(String objectivePeriodOwnerName) {
    this.objectivePeriodOwnerName = objectivePeriodOwnerName;
  }

  @ThriftField
  public void setObjectivePeriodName(String objectivePeriodName) {
    this.objectivePeriodName = objectivePeriodName;
  }

  @ThriftField
  public void setParentObjectiveName(String parentObjectiveName) {
    this.parentObjectiveName = parentObjectiveName;
  }

  @ThriftField
  public void setRegularRemindType(Integer regularRemindType) {
    this.regularRemindType = regularRemindType;
  }

  @ThriftField
  public void setObjectivePeriodOwnerJobTitleName(String objectivePeriodOwnerJobTitleName) {
    this.objectivePeriodOwnerJobTitleName = objectivePeriodOwnerJobTitleName;
  }

  @ThriftField
  public void setHasSubordinate(Integer hasSubordinate) {
    this.hasSubordinate = hasSubordinate;
  }

  @ThriftField
  public void setHasParent(Integer hasParent) {
    this.hasParent = hasParent;
  }
}
