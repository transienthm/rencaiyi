package hr.wozai.service.user.server.test.dao.userorg;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.user.server.dao.userorg.BasicUserProfileDao;
import hr.wozai.service.user.server.model.userorg.BasicUserProfile;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * BasicUserProfileDao Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 20, 2016</pre>
 */
public class BasicUserProfileDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldDaoTest.class);

  @Autowired
  private BasicUserProfileDao basicUserProfileDao;

  long orgId = 299999999L;
  long userId = 399999999L;
  long profileTemplateId = 69999999L;
  long createdUserId = 79999999L;

  String resume = "resume";
  BasicUserProfile basicUserProfile = null;

  {
    basicUserProfile = new BasicUserProfile();
    basicUserProfile.setOrgId(orgId);
    basicUserProfile.setUserId(userId);
    basicUserProfile.setResume(resume);
    basicUserProfile.setCreatedUserId(createdUserId);
  }

  @Before
  public void setup() throws Exception {
  }

  @After
  public void teardown() throws Exception {
  }

  /**
   * Method: insertBasicUserProfile(BasicUserProfile basicUserProfile)
   */
  @Test
  public void testInsertBasicUserProfile() throws Exception {
    basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
    BasicUserProfile insertedBasicUserProfile =
        basicUserProfileDao.findBasicUserProfileByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(resume, insertedBasicUserProfile.getResume());
  }

  /**
   * Method: listBasicUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds)
   */
  @Test
  public void testListBasicUserProfileByOrgIdAndUserId() throws Exception {
    List<Long> userIds = new ArrayList<>();
    int count = 10;
    for (long i = 1; i <= count; i++) {
      basicUserProfile.setUserId(i);
      basicUserProfile.setCreatedUserId(userId);
      basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
      userIds.add(i);
    }
    List<BasicUserProfile> insertedProfiles = basicUserProfileDao.listBasicUserProfileByOrgIdAndUserId(orgId, userIds);
    Assert.assertEquals(count, insertedProfiles.size());
  }

  @Test
  public void testListBasicUserProfileByCreatedUserId() {

    List<Long> userIds = new ArrayList<>();
    int count = 10;
    for (long i = 1; i <= count; i++) {
      basicUserProfile.setUserId(i);
      basicUserProfile.setCreatedUserId(createdUserId);
      basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
      userIds.add(i);
    }
    List<BasicUserProfile> insertedProfiles = basicUserProfileDao
        .listBasicUserProfileByCreatedUserId(orgId, createdUserId);
    Assert.assertEquals(count, insertedProfiles.size());

    for (int i = 0; i < insertedProfiles.size(); i++) {
      Assert.assertEquals(createdUserId, insertedProfiles.get(i).getCreatedUserId().longValue());
    }

  }

  @Test
  public void testListBasicUserProfileByOrgIdOrderByCreatedTimeDesc() {

    List<Long> userIds = new ArrayList<>();
    int count = 10;
    int pageNumber = 1;
    int pageSize = 20;
    for (long i = 1; i <= count; i++) {
      basicUserProfile.setUserId(i);
      basicUserProfile.setCreatedUserId(userId);
      basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
      userIds.add(i);
    }
    List<BasicUserProfile> insertedProfiles = basicUserProfileDao
        .listBasicUserProfileByOrgIdOrderByCreatedTimeDesc(orgId, pageNumber, pageSize);
    Assert.assertEquals(count, insertedProfiles.size());

    for (int i = 0; i < insertedProfiles.size() - 1; i++) {
      Assert.assertTrue(insertedProfiles.get(i).getCreatedTime().longValue()
                        >= insertedProfiles.get(i + 1).getCreatedUserId().longValue());
    }
  }

  @Test
  public void testCountBasicUserProfileByOrgId() {

    List<Long> userIds = new ArrayList<>();
    int count = 10;
    for (long i = 1; i <= count; i++) {
      basicUserProfile.setUserId(i);
      basicUserProfile.setCreatedUserId(userId);
      basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
      userIds.add(i);
    }
    int insertedCount = basicUserProfileDao.countBasicUserProfileByOrgId(orgId);
    Assert.assertEquals(count, insertedCount);

  }

  /**
   * Method: updateBasicUserProfileByOrgIdAndUserId(BasicUserProfile basicUserProfile)
   */
  @Test
  public void testUpdateBasicUserProfileByOrgIdAndUserId() throws Exception {
    basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
    BasicUserProfile insertedProfile = basicUserProfileDao.findBasicUserProfileByOrgIdAndUserId(orgId, userId);
    String newResume = resume + resume;
    insertedProfile.setResume(newResume);
    insertedProfile.setLastModifiedUserId(userId);
    basicUserProfileDao.updateBasicUserProfileByOrgIdAndUserId(insertedProfile);
    BasicUserProfile updatedProfile = basicUserProfileDao.findBasicUserProfileByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(newResume, updatedProfile.getResume());
  }

  @Test
  public void testDeleteBasicUserProfileByOrgIdAndUserId() {

    // prepare
    basicUserProfileDao.insertBasicUserProfile(basicUserProfile);
    BasicUserProfile insertedProfile = basicUserProfileDao.findBasicUserProfileByOrgIdAndUserId(orgId, userId);
    basicUserProfileDao.deleteBasicUserProfileByOrgIdAndUserId(orgId, insertedProfile.getUserId(), createdUserId);

    // verify
    BasicUserProfile deleteCUP = basicUserProfileDao
        .findBasicUserProfileByOrgIdAndUserId(orgId, insertedProfile.getUserId());
    Assert.assertNull(deleteCUP);

  }

} 
