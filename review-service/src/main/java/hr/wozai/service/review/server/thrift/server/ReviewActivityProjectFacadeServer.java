// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.server;

import hr.wozai.service.review.client.facade.ReviewActivityProjectFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@Service("reviewActivityProjectFacadeServer")
public class ReviewActivityProjectFacadeServer {

  @Autowired
  private ReviewActivityProjectFacade reviewActivityProjectFacade;

  @Value("${thrift.port.reviewActivityProject}")
  private Integer port;

  @Value("${thrift.zkpath.reviewActivityProject}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(reviewActivityProjectFacade, port, zkPath);
  }

}
