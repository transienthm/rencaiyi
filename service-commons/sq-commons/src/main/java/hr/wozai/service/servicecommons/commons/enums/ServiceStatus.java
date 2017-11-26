// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

import hr.wozai.service.servicecommons.commons.consts.MetadataConsts;

/**
 * @Author: lepujiu
 * @Version: 0.0.3
 * @Created: 2016-1-25
 */
public enum ServiceStatus {

  /**
   * Format: XXXyyZZ
   *  XXX: similar to http code
   *  yy: service code
   *  ZZ: specific error code
   *
   * yy:
   *  00: common usage
   *  01: AuthService
   *  02: UserOrg
   *  03: UserProfile
   *  04: Onboarding
   *  05: Review
   *  06: OKR
   *  07: Feed
   *  08: Third-Party
   *  09: DocumentService
   */

/***************************** Commons ******************************/

  /**
   * 200-00-ZZ
   */
  COMMON_OK(2000000, "请求成功"),

  /**
   * 200-01-ZZ
   */
  COMMON_CREATED(2010000, "创建成功"),

  /**
   * 400-00-ZZ
   */
  COMMON_BAD_REQUEST(4000000, "系统开小差了，请稍后再试"),
  COMMON_INVALID_PARAM(4000001, "系统开小差了，请稍后再试"),
  COMMON_INVALID_JSON(4000002, "系统开小差了，请稍后再试"),
  COMMON_ILLEGAL_CHARACTER(4000003, "保存失败，请检查是否输入了非法字符(如emoji 符号)"),
  COMMON_STRING_VALIDATE_FAIL(4000004,"文本内容过长"),
  COMMON_BATCH_EMAIL_PARAMETER_LENGTH_ERROR(4000005, "批量邮件参数组参数长度有误"),
  COMMON_BATCH_EMAIL_ADDRESS_ERROR(4000006, "批量邮件地址错误"),

  /**
   * 403-00-ZZ
   */
  COMMON_PERMISSION_DENIED(4030000, "您没有权限执行此操作"),

  /**
   * 404-00-ZZ
   */
  COMMON_NOT_FOUND(4040000, "未找到该数据"),

  /**
   * 500-00-ZZ
   */
  COMMON_INTERNAL_SERVER_ERROR(5000000, "系统开小差了，请稍后再试"),

  /***************************** AuthService ******************************/
  /**
   * 400-01-ZZ
   */
  AS_INVALID_LOGIN_CREDENTIALS(4000100, "登录名或密码错误"),
  AS_INVALID_LOGIN_CREDENTIALS_NEED_CAPTCHA(4000101, "请填写验证码"),
  AS_NO_AUTH_BEFORE_ENROLL_DATE(4000102, "您尚未正式入职，暂时无法操作，请耐心等待"),
  AS_EMAIL_EXIST(4000103, "邮箱已被注册"),
  AS_INVALID_TOKEN(4000104, "请重新登录"),
  AS_INVALID_CAPTCHA_VERIFICATION(4000105, "验证码错误"),
  AS_LINK_INVALID_OR_EXPIRED(4000106, "链接无效或已过期"),


  /***************************** UserOrg ******************************/
  /**
   * 400-02-ZZ
   */
  UO_TEAM_EXIST(4000202, "该团队名称已存在"),
  UO_TEAM_MEMBER_EXIST(4000201, "该成员已经在这个团队中了"),
  UO_ORG_MEMBER_EXIST(4000202, "已经存在该用户，请检查"),
  UO_UUID_EXPIRE(4000203, "链接已过期"),
  UO_INVALID_PICK_OPTION(4000204, "选项配置有误"),
  UO_INVALID_USER_PROFILE_CONFIG(4000205, "预设档案配置有误"),

  /**
   * 403-02-ZZ
   */
  UO_TEAM_UPDATE_FAIL(4030200, "无法移动该团队"),
  UO_TEAM_DELETE_FAIL(4030201, "无法删除该团队"),
  UO_REPORTLINE_UPDATE_FAIL(4030203, "汇报对象修改失败，不能将汇报对象设置为本人或下属"),
  UO_REPORTLINE_DELETE_FAIL(4030204, "汇报关系删除失败"),

  /**
   * 404-02-ZZ
   */
  UO_ORG_NOT_FOUND(4040200, "未找到该公司"),
  UO_TEAM_MEMBER_NOT_FOUND(4040201, "未找到该组织关系"),
  UO_USER_NOT_FOUND(4040202, "未找到该成员"),
  UO_TEAM_NOT_FOUND(4040203, "未找到该团队"),
  UO_PARENT_TEAM_NOT_FOUND(4040205, "未找到父级团队"),
  UO_ORG_MEMBER_NOT_FOUND(4040206, "该用户不属于该公司"),
  UO_REPORTLINE_NOT_FOUND(4040207, "未找到该汇报对象"),
  UO_REMINDSETTING_NOT_FOUND(4040208, "未找到该提醒设置"),
  UO_ROLE_NOT_FOUND(4040209, "未找到该角色"),
  UO_UUID_NOT_FOUND(4040210, "未找到UUID"),
  UO_NAVI_NOT_FOUND(4040211, "未找到引导步骤"),
  UO_PROJECT_TEAM_NOT_FOUND(4040212, "未找到项目组"),


  /***************************** UserProfile ******************************/

  /**
   * 400-03-ZZ
   */
  UP_INVALID_CONTAINER_FOR_DATA_FIELD(4000300, "未找到匹配的容器字段"),
  UP_INVALID_PICK_OPTIONS(4000301, "下拉选项列表设置有误"),
  UP_INVALID_PROFILE_FIELDS(4000302, "字段列表设置有误"),
  UP_INVALID_FIELD_MOVE(4000303, "字段排序操作有误"),
  UP_CANNOT_DELETE_SYSTEM_REQURIED_FIELD(4000304, "系统字段不可删除"),
  UP_INVALID_FIELD_VALUE(4000305, "成员档案数据格式有误"),
  UP_INVALID_FIELD_VALUE_UPDATE(4000306, "成员档案数据更新有误"),
  UP_CSV_EMPTY(4000307, "导入的数据文件不能为空"),
  UP_INVALID_CSV_DATA(4000308, "导入数据字段有误"),
  UP_INVALID_CSV_FORMAT(4000309, "导入数据格式有误"),
  UP_ENROLL_LATER_THAN_RESIGN(4000309, "离职日期不能早于入职日期"),
  UP_USER_NOT_FOUND_OR_CANNOT_INIT_PWD(4000310, "您的帐号不存在或不可激活。请联系管理员或HR确认"),
  UP_USER_ALREADY_ACTIVATED(4000311, "您的帐号已经激活，请直接登录"),
  UP_SUPER_ADMIN_CANNOT_RESIGN(4000313, "超级管理员无法离职"),

  /**
   * 403-03-ZZ
   */
  UP_PROFILE_TEMPLATE_IN_USE(4030300, "该档案模板当前有成员使用，无法删除"),
  UP_DATA_FIELD_NUMBER_UPPERBOUND(4030301, "数据字段的个数已经达到上限，无法添加"),
  UP_CONTAINER_FIELD_NUMBER_UPPERBOUND(4030302, "容器字段的个数已经达到上限，无法添加"),
  UP_CANNOT_RESIGN_USER_WITH_REPORTEE(4030303, "该成员仍有人向其汇报，无法离职。请调整汇报关系后办理"),
  UP_CANNOT_DELETE_ACTIVE_USER(4030304, "该成员处于在职状态，无法彻底删除。请先将其离职"),
  UP_FIELD_IS_MANDATORY(4030305, "必填字段不可为空"),
  UP_CONTAINER_DUP_NAME(4030306, "分组名已被使用"),
  UP_DATA_FIELD_DUP_NAME(4030307, "该字段名在当前分组内已被使用"),

  /**
   * 404-03-ZZ
   */
  UP_PROFILE_TEMPLATE_NOT_FOUND(4040300, "未找到该档案模板"),
  UP_PROFILE_FIELD_NOT_FOUND(4040301, "未找到该字段"),
  UP_PROFILE_FIELDS_EMPTY(4040302, "未找到任何可用字段"),
  UP_USER_NOT_FOUND(4040303, "未找到该用户"),
  UP_JOB_TRANSFER_NOT_FOUNT(4040304, "未找到该调岗记录"),
  UP_STATUS_UPDATE_NOT_FOUND(4040305, "未找到该变更记录"),


  /***************************** Onboarding ******************************/

  /**
   * 400-04-ZZ
   */
  OB_INVALID_ONBOARDING_DOCUMENT_SEQUENCE(4000400, "入职文档列表有误"),

  /**
   * 403-04-ZZ
   */
  OB_ONBOARDING_TEMPLATE_IN_USE(4030400, "该入职模板当前有员工使用, 无法删除"),

  /**
   * 404-04-ZZ
   */
  OB_ONBOARDING_TEMPLATE_NOT_FOUND(4040400, "入职模板未找到"),

  /***************************** Review ******************************/

  /**
   * 400-05-ZZ
   */
  REVIEW_COMMENT_EXIST(4000500, "反馈已存在"),
  REVIEW_TEMPLATE_EXIST(4000501, "反馈活动已存在"),
  REVIEW_ACTIVITY_EXIST(4000502, "反馈活动已存在"),
  REVIEW_QUESTION_EXIST(4000503, "反馈活动问题项已存在"),
  REVIEW_PROJECT_EXIST(4000504, "反馈项目已存在"),
  REVIEW_INVITATION_EXIST(4000505, "反馈邀请已存在"),
  REVIEW_REMAINING_REVIEW_TODO(4000506, "该用户仍有反馈活动未完成"),

  /**
   * 405-05-ZZ
   */
  REVIEW_COMMENT_NOT_FOUND(4040500, "未找到该反馈"),
  REVIEW_TEMPLATE_NOT_FOUND(4040501, "未找到该反馈活动"),
  REVIEW_ACTIVITY_NOT_FOUND(4040502, "未找到该自评"),
  REVIEW_QUESTION_NOT_FOUND(4040503, "未找到该反馈问题项"),
  REVIEW_PROJECT_NOT_FOUND(4040504, "未找到反馈项目"),
  REVIEW_INVITATION_NOT_FOUND(4040505, "未找到该反馈邀请"),

  /***************************** Okr ******************************/

  /**
   * 400-06-ZZ
   */
  OKR_OBJECTIVE_PERIOD_EXIST(4000600, "目标周期已存在"),
  OKR_SET_PARENT_OBJECTIVE_ERROR(4000601, "上级目标设置失败，不能将上级目标设置为本目标或其下级"),
  /**
   * 404-06-ZZ
   */
  OKR_OBJECTIVE_PERIOD_NOT_FOUND(4040600, "未找到目标周期"),
  OKR_OBJECTIVE_NOT_FOUND(4040601, "未找到工作目标"),
  OKR_KEYRESULT_NOT_FOUND(4040602, "未找到关键结果"),
  OKR_DIRECTOR_NOT_FOUND(4040603, "未找到负责人"),
  OKR_LOG_NOT_FOUND(4040604, "未找到目标动态"),
  OKR_LOG_COMMENT_NOT_FOUND(4040605, "未找到回复"),

  /***************************** Feed and Reward******************************/

  /**
   * 400-07-ZZ
   */
  FD_CONTENT_TOO_LONG(4000701, "抱歉, 您的字数超限"),

  /**
   * 404-07-ZZ
   */
  FD_FEED_NOT_FOUND(4040700, "动态不存在或已删除"),
  FD_COMMENT_NOT_FOUND(40407001, "回复不存在或已删除"),
  FD_THUMBUP_NOT_FOUND(404007002, "点赞不存在或已删除"),
  FD_REWARD_NOT_FOUND(40407003, "赞赏不存在或已删除"),
  FD_REWARD_SETTING_NOT_FOUND(40407004, "赞赏设置不存在或已删除"),
  FD_REWARD_MEDAL_NOT_FOUND(40407005, "勋章不存在或已删除"),

  /***************************** Document ******************************/

  /**
   * 404-08-ZZ
   */
  DOC_NOT_FOUND(4040800, "未找到该文档"),

  /***************************** Third-Party ******************************/

  /**
   * 400-09-ZZ
   */
  TP_INVALID_CAPTCHA_VERIFICATION(4000800, "验证码无效"),
  TP_EXPIRED_VERIFICATION(4000801, "验证码已过期"),
  TP_EXISTING_VERIFICATION(4000802, "验证码已被使用"),

  /**
   * 404-09-ZZ
   */
  TP_MOBILE_NOT_FOUND(4040800, "未找到该手机号"),
  TP_HISTORY_LOG_NOT_FOUND(4040801, "未找到历史日志"),
  TP_SMS_VERIFICATION_NOT_FOUND(4040902, "手机验证码不正确"),
  TP_EMAIL_VERIFICATION_NOT_FOUND(4040903, "邮件验证码不正确"),


  /***************************** Survey ******************************/
  /**
   * 400-12-ZZ
   */
  SUR_TIME_PERIOD_TOO_SHORT(4001200, "生效时间范围太短,调研项无法发送,请修改")
  ;






  /********************************* Deprecated ************************************/

//
//  BAD_REQUEST(400000, "非法请求"),
//  INVALID_PARAMS(400001, "输入参数格式有误"),
//  INVALID_LOGIN_CREDENTIALS(400002, "邮箱地址或密码有误"),
//  INVALID_DATA_VALUE(400003, "字段值格式有误"),
//  INVALID_MOBILE(400004, "电话格式有误"),
//  DUPLICATE_VALUE(400005, "该名字已存在"),
//  INVALID_LOGIN_CREDENTIALS_NEED_CAPTCHA(400006, "邮箱地址或密码有误且需要验证码"),
//  NO_AUTH_BEFORE_ENROLL_DATE(400007, "未到入职时间"),
//
//  /** 400XXX: error of org settings **/
//  DATA_FIELD_NUMBER_UPPERBOUND(400100, "数据字段数量已经达到上限"),
//  CONTAINER_FIELD_NUMBER_UPPERBOUND(400101, "分组字段数量已经达到上限"),
//  CHECKLIST_OPTION_REQUIRED(400103, "至少需要一个列表选项"),
//  CHECKLIST_OPTION_DEFAULT_UPPERBOUND(400104, "请选择唯一的默认值"),
//
//  PROFILE_TEMPLAET_IN_USE(400120, "该档案模板当前有用户使用, 无法删除"),
//  ONBOARDING_TEMPLATE_IN_USE(400121, "该入职模板当前有用户使用, 无法删除"),
//
//  INVALID_JSON_FORMAT(400200, "非法JSON格式"),
//
//  UNAUTHORRIZED(401000, "Authentication Required"),
//  INVALID_CREDENTIAL(401001, "Invalid User Credential"),
//  INVALID_TOKEN(401002, "Invalid Token"),
//  INVALID_VERIFICATION(401300, "验证码无效"),
//  INVALID_CAPTCHA_VERIFICATION(401301, "Invalid Captcha Verification"),
//  INVALID_EMAIL_VERIFICATION(401302, "Invalid Email Verification"),
//  INVALID_SMS_VERIFICATION(401303, "手机号或验证码错误"),
//  EXPIRED_VERIFICATION(401304, "验证码已过期"),
//  EXISTING_VERIFICATION(401305, "验证码已被使用"),
//
//  INVALID_UUID(401401, "UUID不存在"),
//  EXPIRED_UUID(401402, "UUID已过期"),
//
//  NOT_FOUND(404000, "未找到该数据"),
//
//  EXISTING_EMAIL(405001, "该邮箱地址已被注册"),
//  EXISTING_MOBILE(405002, "该手机号码已被注册"),
//  EXISTING_OBJECT(405003, "Existing Object"),
//
//  /** 406XXX: error code for batch import staff from csv file **/
//  CSV_EMPTY(406000, "空表单"),
//
//  TEAM_DELETE_FAIL(407000, "该团队有子团队或子成员,无法删除"),
//  UPDATE_FAIL(407001, "非法移动"),
//  DELETE_FAIL(407002, "非法删除"),
//  CANNOT_READ(407003, "无权查看"),
//
//  /** 408XXX: error code for user-sys **/
//  REMAINING_REVIEW_TODO(408001, "该用户仍有评价活动未完成"),
//  CANNOT_DELETE_ACTIVE_USER(408002, "请先将该用户离职"),
//  CANNOT_RESIGN_USER_WITH_REPORTEE(408003, "请先移除所有下属"),
//
//  INTERNAL_SERVER_ERROR(500000, "服务端错误");

  // 403XX
//  FORBIDDEN(40300, "Authorization Required"),
//  PERMISSION_DENIED(40301, "Permission Denied"),
//  UNREADY_TO_SIGN(40302, "Unready to Sign"),

  private int code;
  private String msg;

  private ServiceStatus(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static ServiceStatus getEnumByCode(int code) {

    for (ServiceStatus serviceStatus : ServiceStatus.values()) {
      if (serviceStatus.code == code) {
        return serviceStatus;
      }
    }

    return null;
  }

  public static ServiceStatus getEnumByMsg(String msg) {

    if (null == msg) {
      return null;
    }
    for (ServiceStatus serviceStatus : ServiceStatus.values()) {
      if (serviceStatus.getMsg().equals(msg)) {
        return serviceStatus;
      }
    }

    return null;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}