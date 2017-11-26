// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import hr.wozai.service.user.client.userorg.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("userOrgServer")
public class UserOrgServer {

  @Autowired
  private UserFacade userFacade;

  @Value("${thrift.port.user}")
  private Integer port;

  @Value("${thrift.zkpath.user}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(userFacade, port, zkPath);
  }

}
