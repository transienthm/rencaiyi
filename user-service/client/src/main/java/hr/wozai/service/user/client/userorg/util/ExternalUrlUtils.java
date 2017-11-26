// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.util;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-13
 */
public class ExternalUrlUtils {

  private static final String HTTP_ENDPOINT_PREFIX_OF_AUTH = "u?uuid=";
  private static final String HTTP_ENDPOINT_SURFIX_OF_INIT_PASSWORD = "#init-password";

  private static final String HTTP_ENDPOINT_PREFIX_OF_ONBOARDING_FLOW = "onboarding-flows/staff?uuid=";
  private static final String HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW = "#/onboarding/staff/welcome";

  private static final String HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW_REVIEW = "#/team/staffProfile/";

  private static final String HTTP_ENDPOINT_SURFIX_OF_LOGIN = "u";

  public static String generateInvitationUrlOfOnboardingFlowForStaff(String host, String uuid) {
    return host + HTTP_ENDPOINT_PREFIX_OF_ONBOARDING_FLOW + uuid + HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW;
  }

}
