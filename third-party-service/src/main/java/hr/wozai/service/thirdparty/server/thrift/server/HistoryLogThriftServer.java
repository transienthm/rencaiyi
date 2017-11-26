package hr.wozai.service.thirdparty.server.thrift.server;

import hr.wozai.service.thirdparty.client.facade.HistoryLogFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by wangbin on 16/4/27.
 */
@Service("historyLogThriftServer")
public class HistoryLogThriftServer {

    @Autowired
    private HistoryLogFacade historyLogFacade;

    @Value("${thrift.port.historylog}")
    private Integer port;

    @Value("${thrift.zkpath.historylog}")
    private String zkPath;

    @PostConstruct
    private void init() {
        SqThriftServer.start(historyLogFacade, port, zkPath);
    }
}
