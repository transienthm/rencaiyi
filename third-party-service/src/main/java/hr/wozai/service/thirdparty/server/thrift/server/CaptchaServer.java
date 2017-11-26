// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.thrift.server;

import hr.wozai.service.thirdparty.client.facade.CaptchaFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-05
 */
@Service
public class CaptchaServer {

  @Autowired
  private CaptchaFacade captchaFacade;

  @Value("${thrift.port.captcha}")
  private Integer port;

  @Value("${thrift.zkpath.captcha}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(captchaFacade, port, zkPath);
  }
}
