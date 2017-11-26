// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.document.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@ThriftStruct
public final class DocumentDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long documentId;

  private Long orgId;

  private Integer scenario;

  private String documentKey;

  private String documentName;

  private String documentType;

  private String md5Hash;

  private String description;

  private Long documentSize;

  private Integer storageStatus;

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
  public Long getDocumentId() {
    return documentId;
  }

  @ThriftField
  public void setDocumentId(Long documentId) {
    this.documentId = documentId;
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
  public String getDocumentKey() {
    return documentKey;
  }

  @ThriftField
  public void setDocumentKey(String documentKey) {
    this.documentKey = documentKey;
  }

  @ThriftField(5)
  public String getDocumentName() {
    return documentName;
  }

  @ThriftField
  public void setDocumentName(String documentName) {
    this.documentName = documentName;
  }

  @ThriftField(6)
  public String getDocumentType() {
    return documentType;
  }

  @ThriftField
  public void setDocumentType(String documentType) {
    this.documentType = documentType;
  }

  @ThriftField(7)
  public String getMd5Hash() {
    return md5Hash;
  }

  @ThriftField
  public void setMd5Hash(String md5Hash) {
    this.md5Hash = md5Hash;
  }

  @ThriftField(8)
  public Long getDocumentSize() {
    return documentSize;
  }

  @ThriftField
  public void setDocumentSize(Long documentSize) {
    this.documentSize = documentSize;
  }

  @ThriftField(9)
  public Integer getStorageStatus() {
    return storageStatus;
  }

  @ThriftField
  public void setStorageStatus(Integer storageStatus) {
    this.storageStatus = storageStatus;
  }

  @ThriftField(10)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
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
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(15)
  public String getDescription() {
    return description;
  }

  @ThriftField
  public void setDescription(String description) {
    this.description = description;
  }

  @ThriftField(16)
  public Integer getScenario() {
    return scenario;
  }

  @ThriftField
  public void setScenario(Integer scenario) {
    this.scenario = scenario;
  }
}
