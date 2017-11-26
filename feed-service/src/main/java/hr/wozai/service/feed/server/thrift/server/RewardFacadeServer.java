package hr.wozai.service.feed.server.thrift.server;

import hr.wozai.service.feed.client.facade.RewardFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by wangbin on 2016/11/21.
 */
@Service("rewardFacadeServer")
public class RewardFacadeServer {

    @Autowired
    private RewardFacade rewardFacade;

    @Value("${thrift.port.reward}")
    private Integer port;

    @Value("${thrift.zkpath.reward}")
    private String zkPath;

    @PostConstruct
    private void init() {
        SqThriftServer.start(rewardFacade, port, zkPath);
    }
}
