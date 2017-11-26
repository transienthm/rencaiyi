package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.OrgMemberDao;
import hr.wozai.service.user.server.model.userorg.OrgMember;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/30
 */
public class OrgMemberDaoTest extends TestBase {
  @Autowired
  private OrgMemberDao orgMemberDao;

  private long orgId = 199L;
  private long userId = 199L;
  private OrgMember orgMember;

  @Before
  public void setUp() throws Exception {
    orgMember = new OrgMember();
    orgMember.setOrgId(orgId);
    orgMember.setUserId(userId);
    orgMember.setCreatedUserId(userId);
  }

  @Test
  public void testInsertOrgMember() throws Exception {
    long orgMemberId = orgMemberDao.insertOrgMember(orgMember);

    List<Long> userIds = orgMemberDao.listUserIdListByOrgId(orgId);
    Assert.assertEquals(1, userIds.size());

    long oId = orgMemberDao.findOrgIdByUserId(userId);
    Assert.assertEquals(orgId, oId);

    OrgMember inDb = orgMemberDao.findByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(orgId, inDb.getOrgId().longValue());

    orgMemberDao.deleteOrgMemberByUserId(userId);

    inDb = orgMemberDao.findByUserIdAndOrgId(orgId, userId);
    Assert.assertNull(inDb);
  }
}