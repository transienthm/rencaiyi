// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-05
 */
public enum SystemProfileField {

  /******** fields in CoreUserProfile ********/

  FULL_NAME(1, "fullName", "full_name", "姓名", 5),
  EMAIL_ADDRESS(2, "emailAddress", "email_address", "企业邮箱", 5),
  MOBILE_PHONE(3, "mobilePhone", "mobile_phone", "手机号", 5),
  GENDER(4, "gender", "gender", "性别", 10),
  PERSONAL_EMAIL(5, "personalEmail", "personal_email", "个人邮箱", 5),
  EMPLOYEE_ID(6, "employeeId", "employee_id", "工号", 5),
  JOB_TITLE(7, "jobTitle", "job_title", "职位", 10),
  JOB_LEVEL(8, "jobLevel", "job_level", "职级", 10),
  NICK_NAME(9, "nickName", "nick_name", "昵称", 5),
  AVATAR_URL(10, "avatarUrl", "avatar_url",  "头像", 6),
  DATE_OF_BIRTH(11, "dateOfBirth", "date_of_birth", "出生日期", 4),
  SIGNATURE_LINE(12, "signatureLine", "signature_line", "个性签名", 5),

  /******** fields in BasicUserProfile ********/

  SELF_INTRO(101, "selfIntro", "self_intro", "个人介绍", 6),
  RESUME(102, "resume", "resume", "简历", 7),
  CITIZEN_ID(103, "citizenId", "citizen_id", "身份证号", 5),
  DEGREE_LEVEL(104, "degreeLevel", "degree_level", "学历", 10),
  COLLEGE_NAME(105, "collegeName", "college_name", "毕业院校", 5),
  MARITAL_STATUS(106, "maritalStatus", "marital_status", "婚姻状况", 10),
//  PERSONAL_EMAIL(107, "personalEmail", "personal_email", 5),
  LIVING_ADDRESS(108, "livingAddress", "living_address", "现住址", 8),
  WEIXIN_ACCOUNT(109, "weixinAccount", "weixin_account", "微信", 5),
  WEIBO_ACCOUNT(110, "weiboAccount", "weibo_account", "微博", 5),
  QQ_ACCOUNT(111, "qqAccount", "qq_account", "QQ", 5),
  LINKEDIN_ACCOUNT(112, "linkedinAccount", "linkedin_account", "LinkedIn", 5),
  PERSONAL_WEBSITE(113, "personalWebsite", "personal_website", "个人网址", 5),
  PAYROLL_ACCOUNT(114, "payrollAccount", "payroll_account", "工资卡号", 5),
  PAYROLL_BANK(115, "payrollBank", "payroll_bank", "开户银行", 5),
  GONJIJIN_ACCOUNT(116, "gongjijinAccount", "gongjijin_account", "公积金账号", 5),
  SHEBAO_ACCOUNT(117, "shebaoAccount", "shebao_account", "社保账号", 5),
  RESIDENCE_TYPE(118, "residenceType", "residence_type", "户籍类型", 10),
  RESIDENCE_ADDRESS(119, "residenceAddress", "residence_address", "户籍地址", 8),
  OFFICIAL_PHOTO(120, "officialPhoto", "official_photo", "一寸免冠照", 13),
  CITIZEN_ID_COPY(121, "citizenIdCopy", "citizen_id_copy", "身份证正反面照", 14),
  ;

  private Integer fieldCode;
  private String referenceName;
  private String dbColumnName;
  private String csvColumnName;
  private Integer dataType;

  private SystemProfileField(
      Integer fieldCode, String referenceName, String dbColumnName, String csvColumnName, Integer dataType) {
    this.fieldCode = fieldCode;
    this.referenceName = referenceName;
    this.dbColumnName = dbColumnName;
    this.csvColumnName = csvColumnName;
    this.dataType = dataType;
  }

  public static SystemProfileField getEnumByCode(Integer fieldCode) {

    if (null == fieldCode) {
      return null;
    }
    for (SystemProfileField systemProfileField : SystemProfileField.values()) {
      if (IntegerUtils.equals(systemProfileField.fieldCode, fieldCode)) {
        return systemProfileField;
      }
    }

    return null;
  }

  public static SystemProfileField getEnumByReferenceName(String referenceName) {

    if (null == referenceName) {
      return null;
    }
    for (SystemProfileField systemProfileField : SystemProfileField.values()) {
      if (systemProfileField.getReferenceName().equals(referenceName)) {
        return systemProfileField;
      }
    }

    return null;
  }

  public static SystemProfileField getEnumByDbColumnName(String dbColumnName) {

    if (null == dbColumnName) {
      return null;
    }
    for (SystemProfileField systemProfileField : SystemProfileField.values()) {
      if (systemProfileField.getDbColumnName().equals(dbColumnName)) {
        return systemProfileField;
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
