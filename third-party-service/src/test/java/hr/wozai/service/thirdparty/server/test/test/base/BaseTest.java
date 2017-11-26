// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.test.test.base;


import hr.wozai.service.thirdparty.server.ThirdPartyServerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ThirdPartyServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:8801")
@Transactional(value = "transactionManager")
//@TransactionConfiguration(defaultRollback = true)
public class BaseTest {

  @Before
  public void init() {}

  @Test
  public void test() {

  }
}
