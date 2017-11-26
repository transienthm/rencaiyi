// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.server;

import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.user.client.survey.facade.SurveyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("surveyServer")
public class SurveyServer {

  @Autowired
  private SurveyFacade surveyFacade;

  @Value("${thrift.port.survey}")
  private Integer port;

  @Value("${thrift.zkpath.survey}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(surveyFacade, port, zkPath);
  }

}
