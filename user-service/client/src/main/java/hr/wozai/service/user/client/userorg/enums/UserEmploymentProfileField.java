// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-05
 */
public enum UserEmploymentProfileField {

  /******** fields in CoreUserProfile ********/

  CONTRACT_TYPE(1, "contractType", "contract_type", "合同类型", 2),
  ENROLL_DATE(2, "enrollDate", "enroll_date", "入职日期", 4),
  RESIGN_DATE(3, "resignDate", "resign_date", "合同到期日", 4),

  ;
 
  private Integer fieldCode;
  private String referenceName;
  private String dbColumnName;
  private String csvColumnName;
  private Integer dataType;

  private UserEmploymentProfileField(
      Integer fieldCode, String referenceName, String dbColumnName, String csvColumnName, Integer dataType) {
    this.fieldCode = fieldCode;
    this.referenceName = referenceName;
    this.dbColumnName = dbColumnName;
    this.csvColumnName = csvColumnName;
    this.dataType = dataType;
  }

  public static UserEmploymentProfileField getEnumByCode(Integer fieldCode) {

    if (null == fieldCode) {
      return null;
    }
    for (UserEmploymentProfileField userEmploymentProfileField : UserEmploymentProfileField.values()) {
      if (IntegerUtils.equals(userEmploymentProfileField.fieldCode, fieldCode)) {
        return userEmploymentProfileField;
      }
    }

    return null;
  }

  public static UserEmploymentProfileField getEnumByReferenceName(String referenceName) {

    if (null == referenceName) {
      return null;
    }
    for (UserEmploymentProfileField userEmploymentProfileField : UserEmploymentProfileField.values()) {
      if (userEmploymentProfileField.getReferenceName().equals(referenceName)) {
        return userEmploymentProfileField;
      }
    }

    return null;
  }

  public static UserEmploymentProfileField getEnumByDbColumnName(String dbColumnName) {

    if (null == dbColumnName) {
      return null;
    }
    for (UserEmploymentProfileField userEmploymentProfileField : UserEmploymentProfileField.values()) {
      if (userEmploymentProfileField.getDbColumnName().equals(dbColumnName)) {
        return userEmploymentProfileField;
      }
    }

    return null;
  }

  public Integer getFieldCode() {
    return fieldCode;
  }

  public void setFieldCode(Integer fieldCode) {
    this.fieldCode = fieldCode;
  }

  public String getReferenceName() {
    return referenceName;
  }

  public void setReferenceName(String referenceName) {
    this.referenceName = referenceName;
  }

  public String getDbColumnName() {
    return dbColumnName;
  }

  public void setDbColumnName(String dbColumnName) {
    this.dbColumnName = dbColumnName;
  }

  public String getCsvColumnName() {
    return csvColumnName;
  }

  public void setCsvColumnName(String csvColumnName) {
    this.csvColumnName = csvColumnName;
  }

  public Integer getDataType() {
    return dataType;
  }

  public void setDataType(Integer dataType) {
    this.dataType = dataType;
  }
}
