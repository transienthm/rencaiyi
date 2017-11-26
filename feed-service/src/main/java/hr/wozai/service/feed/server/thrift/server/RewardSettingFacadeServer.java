/*package hr.wozai.service.feed.server.thrift.server;

import hr.wozai.service.feed.client.facade.RewardSettingFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

*//**
 * Created by wangbin on 2016/11/17.
 *//*
@Service("rewardSettingFacadeServer")
public class RewardSettingFacadeServer {

    @Autowired
    private RewardSettingFacade rewardSettingFacade;

    @Value("${thrift.port.rewardsetting}")
    private Integer port;

    @Value("${thrift.zkpath.rewardsetting}")
    private String zkPath;

    @PostConstruct
    private void init() {
        SqThriftServer.start(rewardSettingFacade, port, zkPath);
    }
}*/
