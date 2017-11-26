// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.test.test.cases;

import hr.wozai.service.thirdparty.client.facade.SmsFacade;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-08
 */
public class SmsFacadeTest extends BaseTest {

  private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SmsFacadeTest.class);

  @Autowired
  SmsFacade smsFacade;

  @Before
  public void init() {
  }

  @Test
  public void sendSmsMessageTestCase() {

    String mobilePhone = "11111111111";

    LOGGER.info("mobilePhone=" + mobilePhone);
    LOGGER.info("sendSmsMessage() response=" + smsFacade.sendSmsMessage(mobilePhone));
  }

  @Test
  public void verifySmsMessageTestCase() {

    String mobilePhone = "11111111111";
    String verificationCode = "523082";
    String fakeVerificationCode = "363719";
    String expiredVerificationCode = "363718";

    LOGGER.info("mobilePhone=" + mobilePhone);
    LOGGER.info("verifySmsMessage() response=" + smsFacade.verifySmsMessage(mobilePhone, verificationCode));
    LOGGER.info("verifySmsMessage() response=" + smsFacade.verifySmsMessage(mobilePhone, fakeVerificationCode));
    LOGGER.info("verifySmsMessage() response=" + smsFacade.verifySmsMessage(mobilePhone, expiredVerificationCode));

  }


}
