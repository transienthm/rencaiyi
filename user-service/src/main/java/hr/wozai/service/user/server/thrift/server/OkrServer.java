// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("okrServer")
public class OkrServer {

  @Autowired
  private OkrFacade okrFacade;

  @Value("${thrift.port.okr}")
  private Integer port;

  @Value("${thrift.zkpath.okr}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(okrFacade, port, zkPath);
  }

}
