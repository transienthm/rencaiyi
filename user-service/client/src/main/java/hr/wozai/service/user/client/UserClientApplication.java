// Copyright (C) 2015 Shanqian
// All rights reserved

package hr.wozai.service.user.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:application-context.xml")
@ComponentScan(value = "hr.wozai.service.user.client")
public class UserClientApplication {

  public static void main(String[] args) {

    SpringApplication.run(UserClientApplication.class);
  }

}