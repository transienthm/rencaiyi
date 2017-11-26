// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.server;

import hr.wozai.service.review.client.facade.ReviewTemplateFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@Service("reviewTemplateFacadeServer")
public class ReviewTemplateFacadeServer {

  @Autowired
  private ReviewTemplateFacade reviewTemplateFacade;

  @Value("${thrift.port.reviewTemplate}")
  private Integer port;

  @Value("${thrift.zkpath.reviewTemplate}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(reviewTemplateFacade, port, zkPath);
  }

}
