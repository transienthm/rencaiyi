package hr.wozai.service.thirdparty.server.thrift.server;

import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/12
 */
@Service
public class MessageCenterServer {
  @Autowired
  private MessageCenterFacade messageCenterFacade;

  @Value("${thrift.port.message}")
  private Integer port;

  @Value("${thrift.zkpath.message}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(messageCenterFacade, port, zkPath);
  }
}
