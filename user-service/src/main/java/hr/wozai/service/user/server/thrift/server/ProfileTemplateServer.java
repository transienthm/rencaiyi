// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.userorg.facade.ProfileTemplateFacade;
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
@Service("profileTemplateServer")
public class ProfileTemplateServer {

  @Autowired
  private ProfileTemplateFacade profileTemplateFacade;

  @Value("${thrift.port.profileTemplate}")
  private Integer port;

  @Value("${thrift.zkpath.profileTemplate}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(profileTemplateFacade, port, zkPath);
  }

}
