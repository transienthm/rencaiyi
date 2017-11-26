package hr.wozai.service.nlp.server.thrift.server.labelcloud;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import hr.wozai.service.nlp.client.labelcloud.facade.LabelCloudFacade;

@Service("labelCloudFacadeServer")
public class LabelCloudFacadeServer {

  @Autowired
  private LabelCloudFacade labelCloudFacade;

  @Value("${thrift.port.labelCloud}")
  private Integer port;

  @Value("${thrift.zkpath.labelCloud}")
  private String zkPath;

  @PostConstruct
  private void init() {
    SqThriftServer.start(this.labelCloudFacade, this.port, this.zkPath);
  }
}