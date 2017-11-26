package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@ThriftStruct
public final class UserEmploymentDTO extends BaseThriftObject {

  private Long userEmploymentId;

  private Long orgId;

  private Long userId;

  private Integer userStatus;

  private Integer onboardingStatus;

  private Integer contractType;

  private Integer employmentStatus;

  private Long internshipEnrollDate;

  private Long internshipResignDate;

  private Long parttimeEnrollDate;

  private Long parttimeResignDate;

  private Long fulltimeEnrollDate;

  private Long fulltimeResignDate;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  @ThriftField(1)
  public Long getUserEmploymentId() {
    return userEmploymentId;
  }

  @ThriftField
  public void setUserEmploymentId(Long userEmploymentId) {
    this.userEmploymentId = userEmploymentId;
  }

  @ThriftField(2)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(3)
  public Long getUserId() {
    return userId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField(4)
  public Integer getUserStatus() {
    return userStatus;
  }

  @ThriftField
  public void setUserStatus(Integer userStatus) {
    this.userStatus = userStatus;
  }

  @ThriftField(5)
  public Integer getOnboardingStatus() {
    return onboardingStatus;
  }

  @ThriftField
  public void setOnboardingStatus(Integer onboardingStatus) {
    this.onboardingStatus = onboardingStatus;
  }

  @ThriftField(6)
  public Integer getContractType() {
    return contractType;
  }

  @ThriftField
  public void setContractType(Integer contractType) {
    this.contractType = contractType;
  }

  @ThriftField(7)
  public Integer getEmploymentStatus() {
    return employmentStatus;
  }

  @ThriftField
  public void setEmploymentStatus(Integer employmentStatus) {
    this.employmentStatus = employmentStatus;
  }

  @ThriftField(8)
  public Long getInternshipEnrollDate() {
    return internshipEnrollDate;
  }

  @ThriftField
  public void setInternshipEnrollDate(Long internshipEnrollDate) {
    this.internshipEnrollDate = internshipEnrollDate;
  }

  @ThriftField(9)
  public Long getInternshipResignDate() {
    return internshipResignDate;
  }

  @ThriftField
  public void setInternshipResignDate(Long internshipResignDate) {
    this.internshipResignDate = internshipResignDate;
  }

  @ThriftField(10)
  public Long getParttimeEnrollDate() {
    return parttimeEnrollDate;
  }

  @ThriftField
  public void setParttimeEnrollDate(Long parttimeEnrollDate) {
    this.parttimeEnrollDate = parttimeEnrollDate;
  }

  @ThriftField(11)
  public Long getParttimeResignDate() {
    return parttimeResignDate;
  }

  @ThriftField
  public void setParttimeResignDate(Long parttimeResignDate) {
    this.parttimeResignDate = parttimeResignDate;
  }

  @ThriftField(12)
  public Long getFulltimeEnrollDate() {
    return fulltimeEnrollDate;
  }

  @ThriftField
  public void setFulltimeEnrollDate(Long fulltimeEnrollDate) {
    this.fulltimeEnrollDate = fulltimeEnrollDate;
  }

  @ThriftField(13)
  public Long getFulltimeResignDate() {
    return fulltimeResignDate;
  }

  @ThriftField
  public void setFulltimeResignDate(Long fulltimeResignDate) {
    this.fulltimeResignDate = fulltimeResignDate;
  }

  @ThriftField(14)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(15)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(16)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(17)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(18)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }
}
