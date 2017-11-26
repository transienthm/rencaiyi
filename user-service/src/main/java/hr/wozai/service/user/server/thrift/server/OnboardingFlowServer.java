// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.user.client.onboarding.facade.OnboardingFlowFacade;
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
@Service("onboardingFlowServer")
public class OnboardingFlowServer {

  @Autowired
  private OnboardingFlowFacade onboardingFlowFacade;

  @Value("${thrift.port.onboardingFlow}")
  private Integer port;

  @Value("${thrift.zkpath.onboardingFlow}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(onboardingFlowFacade, port, zkPath);
  }

}
