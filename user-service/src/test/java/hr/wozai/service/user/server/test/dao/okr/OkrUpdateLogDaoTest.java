package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.user.server.dao.okr.OkrUpdateLogDao;
import hr.wozai.service.user.server.model.okr.OkrUpdateLog;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/9/8
 */
public class OkrUpdateLogDaoTest extends TestBase{
  @Autowired
  OkrUpdateLogDao okrUpdateLogDao;

  long orgId = 199L;
  long okrCommentId = 199L;
  String title = "title";
  String attribute = "attribute";
  String before = "before";
  String after = "after";
  long userId = 199L;
  OkrUpdateLog okrUpdateLog;

  @Before
  public void setUp() throws Exception {
    okrUpdateLog = new OkrUpdateLog();
    okrUpdateLog.setOrgId(orgId);
    okrUpdateLog.setOkrCommentId(okrCommentId);
    okrUpdateLog.setAttribute(attribute);
    okrUpdateLog.setBeforeValue(before);
    okrUpdateLog.setAfterValue(after);
    okrUpdateLog.setCreatedUserId(userId);
  }

  @Test
  public void testBatchInsertOkrUpdateLog() throws Exception {
    long result = okrUpdateLogDao.batchInsertOkrUpdateLog(Arrays.asList(okrUpdateLog));
    Assert.assertEquals(1L, result);

    result = okrUpdateLogDao.batchInsertOkrUpdateLog(new ArrayList<>());
    Assert.assertEquals(0, result);

    List<OkrUpdateLog> okrUpdateLogList = okrUpdateLogDao.listOkrUpdateLogsByOkrCommentId(orgId, okrCommentId);
    Assert.assertEquals(1, okrUpdateLogList.size());
    OkrUpdateLog inDb = okrUpdateLogList.get(0);

    Assert.assertEquals(attribute, inDb.getAttribute());
    Assert.assertEquals(before, inDb.getBeforeValue());
    Assert.assertEquals(after, inDb.getAfterValue());
  }
}