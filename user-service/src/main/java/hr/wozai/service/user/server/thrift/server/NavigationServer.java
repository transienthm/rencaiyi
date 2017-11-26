// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import hr.wozai.service.user.client.userorg.facade.NavigationFacade;
import hr.wozai.service.user.client.userorg.facade.OrgFacade;
import hr.wozai.service.user.server.model.navigation.Navigation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-01
 */
@Service("navigationServer")
public class NavigationServer {

  @Autowired
  private NavigationFacade navigationFacade;

  @Value("${thrift.port.navigation}")
  private Integer port;

  @Value("${thrift.zkpath.navigation}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(navigationFacade, port, zkPath);
  }

}
