// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.test.base;

import hr.wozai.service.feed.server.FeedServerApplication;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FeedServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:8120")
@Transactional(value = "transactionManager")
//@TransactionConfiguration(defaultRollback = false)
public class TestBase {

  @Before
  public void init() {
  }

}
