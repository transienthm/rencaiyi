package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.ReportLineDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.ReportLine;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/3
 */
public class ReportLineDaoTest extends TestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportLineDaoTest.class);

  @Autowired
  ReportLineDao reportLineDao;

  private long userId = 199L;
  private long reportUserId = 99L;
  private long orgId = 199L;

  @Test
  public void testBatchInsertReportLines() throws Exception {
    ReportLine reportLine = new ReportLine();
    reportLine.setOrgId(orgId);
    reportLine.setUserId(userId);
    reportLine.setReportUserId(reportUserId);
    reportLine.setCreatedUserId(userId);
    reportLine.setLastModifiedUserId(userId);

    long result = reportLineDao.batchInsertReportLines(Arrays.asList(reportLine));
    Assert.assertEquals(1, result);

    ReportLine indb = reportLineDao.getReportLineByUserId(orgId, userId);
    Assert.assertEquals(reportUserId, indb.getReportUserId().longValue());

    List<ReportLine> reportLines = reportLineDao.listReportLinesByUserIds(orgId, Arrays.asList(userId));
    Assert.assertEquals(1, reportLines.size());

    List<Long> userIds = reportLineDao.listReporteesByUserId(orgId, reportUserId);
    Assert.assertEquals(1, userIds.size());
    Assert.assertEquals(userId, userIds.get(0).longValue());

    reportLineDao.batchUpdateReportLines(orgId, Arrays.asList(userId), 8L, userId);
    userIds = reportLineDao.listReporteesByUserId(orgId, reportUserId);
    Assert.assertEquals(0, userIds.size());

    reportLineDao.batchDeleteReportLines(orgId, Arrays.asList(userId), -1L);
    Assert.assertNull(reportLineDao.getReportLineByUserId(orgId, userId));

  }
}