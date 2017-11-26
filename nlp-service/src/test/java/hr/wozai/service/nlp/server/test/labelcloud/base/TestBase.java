package hr.wozai.service.nlp.server.test.labelcloud.base;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.boot.test.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import hr.wozai.service.nlp.server.NLPServerApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NLPServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:8500")
@Transactional(value = "transactionManager")
public class TestBase {

  @Before
  public void init() {
  }

  @Test
  public void test() {
  }

}