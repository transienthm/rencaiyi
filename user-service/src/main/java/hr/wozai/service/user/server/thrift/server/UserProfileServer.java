// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("userProfileServer")
public class UserProfileServer {

  @Autowired
  private UserProfileFacade userProfileFacade;

  @Value("${thrift.port.userprofile}")
  private Integer port;

  @Value("${thrift.zkpath.userprofile}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(userProfileFacade, port, zkPath);
  }

}
