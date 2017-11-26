// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
@SpringBootApplication
@ImportResource("classpath:application-context.xml")
@ComponentScan(value = {
    "hr.wozai.service.feed",
    "hr.wozai.service.thirdparty.client.utils",
    "hr.wozai.service.servicecommons.utils.logging"})
@EnableTransactionManagement
public class FeedServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FeedServerApplication.class);
  }

}
