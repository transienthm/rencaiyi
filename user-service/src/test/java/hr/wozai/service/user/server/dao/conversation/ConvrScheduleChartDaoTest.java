package hr.wozai.service.user.server.dao.conversation;

import hr.wozai.service.user.server.model.conversation.ConvrScheduleChart;
import hr.wozai.service.user.server.model.conversation.ConvrSourceUserInfo;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/12/6.
 */
public class ConvrScheduleChartDaoTest extends TestBase {

    @Autowired
    private ConvrScheduleChartDao convrScheduleChartDao;

    @Test
    public void testAll() {
/*        List<ConvrScheduleChart> result = convrScheduleChartDao.listConvrScheduleChartByOrgId(30, 3l);
        for (ConvrScheduleChart convrScheduleChart : result) {
            System.out.println(convrScheduleChart);
        }*/

        List<ConvrSourceUserInfo> convrSourceUserInfos = convrScheduleChartDao.getConvrSourceUserInfoByOrgId(3l, 1, 20);
        for (ConvrSourceUserInfo convrSourceUserInfo : convrSourceUserInfos) {
            System.out.println(convrSourceUserInfo);
        }
    }

}