package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.user.server.dao.okr.OkrCommentDao;
import hr.wozai.service.user.server.model.okr.OkrComment;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/9/8
 */
public class OkrCommentDaoTest extends TestBase {

  @Autowired
  OkrCommentDao okrCommentDao;

  long orgId = 199L;
  long objectiveId = 199L;
  long userId = 199L;
  String content = "content";
  String keyResultContent = "";
  OkrComment okrComment;

  @Before
  public void setUp() throws Exception {
    okrComment = new OkrComment();
    okrComment.setOrgId(orgId);
    okrComment.setObjectiveId(objectiveId);
    okrComment.setKeyResultId(0L);
    okrComment.setKeyResultContent(keyResultContent);
    okrComment.setUserId(userId);
    okrComment.setContent(content);
    okrComment.setCreatedUserId(userId);
  }

  @Test
  public void testAll() throws Exception {
    long okrCommentId = okrCommentDao.insertOkrComment(okrComment);

    okrComment.setKeyResultId(100L);
    okrCommentDao.insertOkrComment(okrComment);

    OkrComment inDb = okrCommentDao.findOkrComment(okrCommentId, orgId);
    Assert.assertEquals(content, inDb.getContent());

    String update = "update";
    okrComment.setContent(update);
    okrComment.setLastModifiedUserId(userId);
    okrCommentDao.updateOkrComment(okrComment);

    List<OkrComment> okrCommentList = okrCommentDao.listOkrCommentsByObjectiveId(orgId, objectiveId, 0L, 1, 5);
    Assert.assertEquals(1, okrCommentDao.listOkrCommentsByObjectiveId(orgId, objectiveId, 100L, 1, 5).size());
    Assert.assertEquals(2, okrCommentList.size());
    inDb = okrCommentList.get(1);
    Assert.assertEquals(orgId, inDb.getOrgId().longValue());
    Assert.assertEquals(objectiveId, inDb.getObjectiveId().longValue());
    Assert.assertEquals(userId, inDb.getUserId().longValue());

    Assert.assertEquals(2, okrCommentDao.countOkrCommentByObjectiveId(orgId, objectiveId, 0L).intValue());

    okrComment.setIsDeleted(1);
    okrComment.setLastModifiedUserId(userId);
    okrCommentDao.updateOkrComment(okrComment);

    okrCommentList = okrCommentDao.listOkrCommentsByObjectiveId(orgId, objectiveId, 0L, 1, 5);
    Assert.assertEquals(1, okrCommentList.size());
  }
}