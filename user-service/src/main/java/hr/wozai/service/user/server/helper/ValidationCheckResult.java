// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-26
 */
public class ValidationCheckResult {

  public boolean noError;

  public String errorMessage;

  public ValidationCheckResult() {
    noError = true;
    errorMessage = "";
  }

  public boolean isNoError() {
    return noError;
  }

  public void setNoError(boolean noError) {
    this.noError = noError;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
