// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.enums;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public enum ReviewTemplateStatus {
  DRAFT("draft", 1),
  IN_PROGRESS("in progress", 2),
  FINISH("finish", 3),
  CANCELED("canceled", 4);

  private String name;
  private int code;

  ReviewTemplateStatus(String name, int code) {
    this.name = name;
    this.code = code;
  }

  public static String getName(int code) {
    for(ReviewTemplateStatus reviewTemplateStatus: ReviewTemplateStatus.values()) {
      if(reviewTemplateStatus.getCode() == code) {
        return reviewTemplateStatus.name;
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
