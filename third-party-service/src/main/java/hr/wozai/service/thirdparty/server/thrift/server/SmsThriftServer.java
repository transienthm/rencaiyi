// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.thirdparty.server.thrift.server;

import hr.wozai.service.thirdparty.client.facade.SmsFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * The thrift server for sms service
 *
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-29
 */
@Service
public class SmsThriftServer {

  @Autowired
  private SmsFacade smsFacade;

  @Value("${thrift.port.sms}")
  private Integer port;

  @Value("${thrift.zkpath.sms}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(smsFacade, port, zkPath);
  }

}
