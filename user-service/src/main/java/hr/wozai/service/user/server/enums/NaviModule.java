// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.enums;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/10/9
 */
public enum NaviModule {

  OBJECTIVE(1, "目标模块引导");

  private Integer code;
  private String desc;

  NaviModule(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public Integer getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
