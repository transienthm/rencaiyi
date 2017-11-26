// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.userorg.facade.OrgFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-01
 */
@Service("orgServer")
public class OrgServer {

  @Autowired
  private OrgFacade orgFacade;

  @Value("${thrift.port.org}")
  private Integer port;

  @Value("${thrift.zkpath.org}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(orgFacade, port, zkPath);
  }

}
