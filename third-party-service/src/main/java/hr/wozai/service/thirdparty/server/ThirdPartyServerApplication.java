// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Author:  Zhe Chen
 * Date:    7/27/15
 * The entrance of Rest-API-Webapp
 */
@SpringBootApplication
@ImportResource("classpath:spring/application-context.xml")
@EnableTransactionManagement
public class ThirdPartyServerApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(ThirdPartyServerApplication.class);
    }
}