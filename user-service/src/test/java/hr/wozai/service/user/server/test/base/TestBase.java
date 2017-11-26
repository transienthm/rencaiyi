// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.base;



import hr.wozai.service.user.server.UserServerApplication;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UserServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:8811")
@Transactional(value = "transactionManager")
public class TestBase {

  Logger LOGGER = LoggerFactory.getLogger(TestBase.class);

  @PostConstruct
  public void init() {}

}