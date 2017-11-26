// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.enums;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public enum ReviewItemType {

  QUESTION("question", 0),

  PROJECT("project", 1);

  private String name;
  private int code;

  ReviewItemType(String name, int code) {
    this.name = name;
    this.code = code;
  }

  public static String getName(int code) {
    for(ReviewItemType reviewItemType: ReviewItemType.values()) {
      if(reviewItemType.getCode() == code) {
        return reviewItemType.name;
      }
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

}
