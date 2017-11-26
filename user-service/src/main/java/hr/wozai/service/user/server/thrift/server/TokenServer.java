// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.userorg.facade.TokenFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("tokenServer")
public class TokenServer {

  @Autowired
  private TokenFacade tokenFacade;

  @Value("${thrift.port.token}")
  private Integer port;

  @Value("${thrift.zkpath.token}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(tokenFacade, port, zkPath);
  }

}
