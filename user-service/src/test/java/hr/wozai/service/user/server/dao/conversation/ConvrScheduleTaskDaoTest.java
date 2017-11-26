package hr.wozai.service.user.server.dao.conversation;

import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/11/30.
 */
public class ConvrScheduleTaskDaoTest extends TestBase {

    long orgId = 999l;

    @Autowired
    private ConvrScheduleTaskDao convrScheduleTaskDao;

    @Test
    public void testAll() {
        List<ConvrSchedule> convrScheduleList = convrScheduleTaskDao.listConvrScheduleByOrgId(3l);
        Assert.assertEquals(6, convrScheduleList.size());
    }

}