package hr.wozai.service.review.server.thrift.server;

import hr.wozai.service.review.client.facade.ReviewActivityDetailFacade;
import hr.wozai.service.servicecommons.thrift.server.SqThriftServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-20
 */
@Service("reviewActivityDetailFacadeServer")
public class ReviewActivityDetailFacadeServer {

    @Autowired
    private ReviewActivityDetailFacade reviewActivityDetailFacade;

    @Value("${thrift.port.reviewActivityDetail}")
    private Integer port;

    @Value("${thrift.zkpath.reviewActivityDetail}")
    private String zkPath;

    @PostConstruct
    private void init() {
        SqThriftServer.start(reviewActivityDetailFacade, port, zkPath);
    }

}
