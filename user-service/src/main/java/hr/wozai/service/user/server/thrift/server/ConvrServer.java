// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import hr.wozai.service.user.client.conversation.facade.ConvrFacade;
import hr.wozai.service.user.client.document.facade.DocumentFacade;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-01
 */
@Service("convrServer")
public class ConvrServer {

  @Autowired
  private ConvrFacade convrFacade;

  @Value("${thrift.port.convr}")
  private Integer port;

  @Value("${thrift.zkpath.convr}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(convrFacade, port, zkPath);
  }

}
