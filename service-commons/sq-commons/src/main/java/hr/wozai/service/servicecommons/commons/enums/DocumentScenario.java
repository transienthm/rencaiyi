package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 0.0.5
 * @Created: 2016-02-19
 */
public enum DocumentScenario {

  ONBOARDING_DOCUMENT(1, "ONBOARDING_DOCUMENT"),
  USER_PROFILE_FIELD(2, "USER_PROFILE_FIELD"),
  MANUAL_OPERATION_CSV(3, "MANUAL_OPERATION_CSV"),
  ONBOARDING_TEMPLATE_IMAGE(4, "ONBOARDING_TEMPLATE_IMAGE"),

  ;

  private int code;
  private String msg;

  private DocumentScenario(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static DocumentScenario getEnumByCode(int code) {
    for (DocumentScenario documentScenario : DocumentScenario.values()) {
      if (documentScenario.code == code) {
        return documentScenario;
      }
    }
    return null;
  }

  public static DocumentScenario getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (DocumentScenario documentScenario : DocumentScenario.values()) {
      if (documentScenario.getMsg().equals(desc)) {
        return documentScenario;
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
