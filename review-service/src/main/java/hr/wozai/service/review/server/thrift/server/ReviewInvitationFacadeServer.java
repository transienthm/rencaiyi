// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.server;

import hr.wozai.service.review.client.facade.ReviewInvitationFacade;
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
@Service("reviewInvitationFacadeServer")
public class ReviewInvitationFacadeServer {

  @Autowired
  private ReviewInvitationFacade reviewInvitationFacade;

  @Value("${thrift.port.reviewInvitation}")
  private Integer port;

  @Value("${thrift.zkpath.reviewInvitation}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(reviewInvitationFacade, port, zkPath);
  }

}
