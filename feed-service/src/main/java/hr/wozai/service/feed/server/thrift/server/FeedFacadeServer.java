// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.thrift.server;

import hr.wozai.service.feed.client.facade.FeedFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
@Service("feedFacadeServer")
public class FeedFacadeServer {

  @Autowired
  private FeedFacade feedFacade;

  @Value("${thrift.port.feed}")
  private Integer port;

  @Value("${thrift.zkpath.feed}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(feedFacade, port, zkPath);
  }

}
