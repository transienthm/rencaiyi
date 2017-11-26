package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.user.server.dao.okr.DirectorDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.okr.Director;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/7
 */
public class DirectorDaoTest extends TestBase {
  @Autowired
  DirectorDao directorDao;

  private long orgId = 1L;
  private long userId = 1L;
  private long objectId = 1L;
  private Director director;

  @Before
  public void setUp() throws Exception {
    director = new Director();
    director.setOrgId(orgId);
    director.setUserId(userId);
    director.setType(1);
    director.setObjectId(objectId);
    director.setCreatedUserId(userId);
  }

  @Test
  public void testBatchInsertDirector() throws Exception {
    directorDao.batchInsertDirector(Arrays.asList(director));

    List<Director> directorList = directorDao.listDirectorByTypeAndObjectId(orgId, 1, objectId);
    Assert.assertEquals(directorList.size(), 1);

    directorList = directorDao.listDirectorsByObjectIds(orgId, 1, Arrays.asList(objectId));
    Assert.assertEquals(directorList.size(), 1);

    Assert.assertEquals(1, directorDao.listObjectiveAndKeyResultDirectorsByObjectiveId(orgId, objectId).size());

    directorDao.batchDeleteDirectorByTypeAndObjectId(orgId, 1, Arrays.asList(objectId), userId);
    directorList = directorDao.listDirectorByTypeAndObjectId(orgId, 1, objectId);
    Assert.assertEquals(0, directorList.size());
  }
}