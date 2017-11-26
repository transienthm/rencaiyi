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
public final class FeedDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long feedId;

  private Long orgId;

  private Long userId;

  private Long teamId;

  private String content;

  private List<String> atUsers;

  private List<String> images;

  private Long likeNumber;

  private Long commentNumber;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private Integer isDeleted;

  private RewardDTO rewardDTO;


  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getFeedId() {
    return feedId;
  }

  @ThriftField
  public void setFeedId(Long feedId) {
    this.feedId = feedId;
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
  public Long getUserId() {
    return userId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField(5)
  public Long getTeamId() {
    return teamId;
  }

  @ThriftField
  public void setTeamId(Long teamId) {
    this.teamId= teamId;
  }

  @ThriftField(6)
  public String getContent() {
    return content;
  }

  @ThriftField
  public void setContent(String content) {
    this.content = content;
  }

  @ThriftField(7)
  public List<String> getAtUsers() {
    return atUsers;
  }

  @ThriftField
  public void setAtUsers(List<String> atUsers) {
    this.atUsers = atUsers;
  }

  @ThriftField(8)
  public List<String> getImages() {
    return images;
  }

  @ThriftField
  public void setImages(List<String> images) {
    this.images = images;
  }

  @ThriftField(9)
  public Long getLikeNumber() {
    return likeNumber;
  }

  @ThriftField
  public void setLikeNumber(Long likeNumber) {
    this.likeNumber = likeNumber;
  }

  @ThriftField(10)
  public Long getCommentNumber() {
    return commentNumber;
  }

  @ThriftField
  public void setCommentNumber(Long commentNumber) {
    this.commentNumber = commentNumber;
  }

  @ThriftField(11)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(12)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(13)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(14)
  public String getExtend() {
    return extend;
  }

  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }

  @ThriftField(15)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted)
  {
    this.isDeleted = isDeleted;
  }

  @ThriftField(16)
  public RewardDTO getRewardDTO() {
    return rewardDTO;
  }

  @ThriftField
  public void setRewardDTO(RewardDTO rewardDTO) {
    this.rewardDTO = rewardDTO;
  }
}
