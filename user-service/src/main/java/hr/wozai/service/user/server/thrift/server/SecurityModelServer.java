// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.userorg.facade.SecurityModelFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("securityModelServer")
public class SecurityModelServer {

  @Autowired
  private SecurityModelFacade securityModelFacade;

  @Value("${thrift.port.sm}")
  private Integer port;

  @Value("${thrift.zkpath.sm}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(securityModelFacade, port, zkPath);
  }

}
