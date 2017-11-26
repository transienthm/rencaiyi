package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-12
 */
@ThriftStruct
public final class ReviewReportDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Integer selfNotBegin;

  private Integer selfInProgress;

  private Integer selfFinished;

  private Integer peerNotBegin;

  private Integer peerInProgress;

  private Integer peerFinished;

  private Integer managerNotBegin;

  private Integer managerInProgress;

  private Integer managerFinished;

  private Map<Integer, Long> managerScore;

  private ReviewTemplateDTO reviewTemplateDTO;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Integer getSelfNotBegin() {
    return selfNotBegin;
  }

  @ThriftField
  public void setSelfNotBegin(Integer selfNotBegin) {
    this.selfNotBegin = selfNotBegin;
  }

  @ThriftField(3)
  public Integer getSelfInProgress() {
    return selfInProgress;
  }

  @ThriftField
  public void setSelfInProgress(Integer selfInProgress) {
    this.selfInProgress = selfInProgress;
  }

  @ThriftField(4)
  public Integer getSelfFinished() {
    return selfFinished;
  }

  @ThriftField
  public void setSelfFinished(Integer selfFinished) {
    this.selfFinished = selfFinished;
  }

  @ThriftField(5)
  public Integer getPeerNotBegin() {
    return peerNotBegin;
  }

  @ThriftField
  public void setPeerNotBegin(Integer peerNotBegin) {
    this.peerNotBegin = peerNotBegin;
  }

  @ThriftField(6)
  public Integer getPeerInProgress() {
    return peerInProgress;
  }

  @ThriftField
  public void setPeerInProgress(Integer peerInProgress) {
    this.peerInProgress = peerInProgress;
  }

  @ThriftField(7)
  public Integer getPeerFinished() {
    return peerFinished;
  }

  @ThriftField
  public void setPeerFinished(Integer peerFinished) {
    this.peerFinished = peerFinished;
  }

  @ThriftField(8)
  public Integer getManagerNotBegin() {
    return managerNotBegin;
  }

  @ThriftField
  public void setManagerNotBegin(Integer managerNotBegin) {
    this.managerNotBegin = managerNotBegin;
  }

  @ThriftField(9)
  public Integer getManagerInProgress() {
    return managerInProgress;
  }

  @ThriftField
  public void setManagerInProgress(Integer managerInProgress) {
    this.managerInProgress = managerInProgress;
  }

  @ThriftField(10)
  public Integer getManagerFinished() {
    return managerFinished;
  }

  @ThriftField
  public void setManagerFinished(Integer managerFinished) {
    this.managerFinished = managerFinished;
  }

  @ThriftField(11)
  public Map<Integer, Long> getManagerScore() {
    return managerScore;
  }

  @ThriftField
  public void setManagerScore(Map<Integer, Long> managerScore) {
    this.managerScore = managerScore;
  }

  @ThriftField(12)
  public ReviewTemplateDTO getReviewTemplateDTO() {
    return reviewTemplateDTO;
  }

  @ThriftField
  public void setReviewTemplateDTO(ReviewTemplateDTO reviewTemplateDTO) {
    this.reviewTemplateDTO = reviewTemplateDTO;
  }
}
