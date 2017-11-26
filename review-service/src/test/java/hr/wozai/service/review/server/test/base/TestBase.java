// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.base;

import hr.wozai.service.review.server.ReviewServerApplication;
import org.junit.Before;
import org.junit.Test;
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
@SpringApplicationConfiguration(classes = ReviewServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:8220")
@Transactional(value = "transactionManager")
public class TestBase {

  @Before
  public void init() {
  }

  @Test
  public void test() {
  }

}
