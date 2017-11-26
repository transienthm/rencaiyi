// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-29
 */
@ThriftStruct
public final class ConvrRecordDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long convrRecordId;

  private Long orgId;

  private Long convrScheduleId;

  private Long convrDate;

  private Long sourceUserId;

  private Long targetUserId;

  private String topicProgress;

  private String topicPlan;

  private String topicObstacle;

  private String topicHelp;

  private String topicCareer;

  private String topicElse;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getConvrRecordId() {
    return convrRecordId;
  }

  @ThriftField
  public void setConvrRecordId(Long convrRecordId) {
    this.convrRecordId = convrRecordId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(4)
  public Long getConvrScheduleId() {
    return convrScheduleId;
  }

  @ThriftField
  public void setConvrScheduleId(Long convrScheduleId) {
    this.convrScheduleId = convrScheduleId;
  }

  @ThriftField(5)
  public Long getConvrDate() {
    return convrDate;
  }

  @ThriftField
  public void setConvrDate(Long convrDate) {
    this.convrDate = convrDate;
  }

  @ThriftField(6)
  public String getTopicProgress() {
    return topicProgress;
  }

  @ThriftField
  public void setTopicProgress(String topicProgress) {
    this.topicProgress = topicProgress;
  }

  @ThriftField(7)
  public String getTopicPlan() {
    return topicPlan;
  }

  @ThriftField
  public void setTopicPlan(String topicPlan) {
    this.topicPlan = topicPlan;
  }

  @ThriftField(8)
  public String getTopicObstacle() {
    return topicObstacle;
  }

  @ThriftField
  public void setTopicObstacle(String topicObstacle) {
    this.topicObstacle = topicObstacle;
  }

  @ThriftField(9)
  public String getTopicHelp() {
    return topicHelp;
  }

  @ThriftField
  public void setTopicHelp(String topicHelp) {
    this.topicHelp = topicHelp;
  }

  @ThriftField(10)
  public String getTopicCareer() {
    return topicCareer;
  }

  @ThriftField
  public void setTopicCareer(String topicCareer) {
    this.topicCareer = topicCareer;
  }

  @ThriftField(11)
  public String getTopicElse() {
    return topicElse;
  }

  @ThriftField
  public void setTopicElse(String topicElse) {
    this.topicElse = topicElse;
  }

  @ThriftField(12)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(13)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(14)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(15)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(16)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(17)
  public Long getSourceUserId() {
    return sourceUserId;
  }

  @ThriftField
  public void setSourceUserId(Long sourceUserId) {
    this.sourceUserId = sourceUserId;
  }

  @ThriftField(18)
  public Long getTargetUserId() {
    return targetUserId;
  }

  @ThriftField
  public void setTargetUserId(Long targetUserId) {
    this.targetUserId = targetUserId;
  }
}
