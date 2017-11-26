// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.common.facade.CommonToolFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("commonToolServer")
public class CommonToolServer {

  @Autowired
  private CommonToolFacade commonToolFacade;

  @Value("${thrift.port.commontool}")
  private Integer port;

  @Value("${thrift.zkpath.commontool}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(commonToolFacade, port, zkPath);
  }

}
