// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.constant;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
public enum FeedThumbupEnum {

  UNLIKE(0),
  LIKE(1)
  ;

  private int code;

  private FeedThumbupEnum(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

}
