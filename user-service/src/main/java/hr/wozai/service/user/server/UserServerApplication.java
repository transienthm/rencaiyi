// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ImportResource("classpath:application-context.xml")
@ComponentScan(value = {
    "hr.wozai.service.user.server",
    "hr.wozai.service.thirdparty.client.utils",
    "hr.wozai.service.thirdparty.client.aspect",
    "hr.wozai.service.servicecommons.utils.logging"})
@EnableTransactionManagement
public class UserServerApplication {

  public static void main(String[] args) {

    SpringApplication.run(UserServerApplication.class);
  }

}