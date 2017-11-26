package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.PickOptionDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.PickOption;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public class PickOptionDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldDaoTest.class);

  @Autowired
  PickOptionDao pickOptionDao;

  long userId = 10;
  long orgId = 20;
  long profileFieldId = 30;
  PickOption pickOption = null;

  {
    pickOption = new PickOption();
    pickOption.setOrgId(orgId);
    pickOption.setProfileFieldId(profileFieldId);
    pickOption.setIsDefault(0);
    pickOption.setIsDeprecated(0);
    pickOption.setCreatedUserId(userId);
  }

  @Test
  public void testBatchInsertPickOption() throws Exception {

    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      PickOption currOption = new PickOption();
      BeanUtils.copyProperties(pickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      pickOptions.add(currOption);
    }
    pickOptionDao.batchInsertPickOption(pickOptions);

  }

  @Test
  public void testListPickOptionByProfileFieldId() throws Exception {

    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      PickOption currOption = new PickOption();
      BeanUtils.copyProperties(pickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      pickOptions.add(currOption);
    }
    pickOptionDao.batchInsertPickOption(pickOptions);

    List<PickOption> addedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    Assert.assertEquals(optionCount, addedPickOptions.size());
    addedPickOptions = pickOptionDao.listPickOptionByProfileFieldIdForUpdate(orgId, profileFieldId);
    Assert.assertEquals(optionCount, addedPickOptions.size());
  }


  @Test
  public void testListPickOptionByOrgIdAndPickOptionIds() {

    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      PickOption currOption = new PickOption();
      BeanUtils.copyProperties(pickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      pickOptions.add(currOption);
    }
    pickOptionDao.batchInsertPickOption(pickOptions);
    List<PickOption> insertedPOs = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    ArrayList<Long> pickOptionIds = new ArrayList<>();
    for (PickOption pickOption: insertedPOs) {
      pickOptionIds.add(pickOption.getPickOptionId());
    }

    // verify
    List<PickOption> insertedPOsByIds = pickOptionDao.listPickOptionByOrgIdAndPickOptionIds(orgId, pickOptionIds);
    Assert.assertEquals(optionCount, insertedPOsByIds.size());

  }

  /**
   * Method: batchUpdatePickOption(List<PickOption> pickOptions)
   */
  @Test
  public void testBatchUpdatePickOption() throws Exception {
    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      PickOption currOption = new PickOption();
      BeanUtils.copyProperties(pickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      pickOptions.add(currOption);
    }
    pickOptionDao.batchInsertPickOption(pickOptions);

    List<PickOption> addedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    for (PickOption pickOption: addedPickOptions) {
      pickOption.setOptionValue("1000");
      pickOption.setOptionIndex(1000);
      pickOption.setIsDefault(1);
      pickOption.setLastModifiedUserId(userId);
    }
    pickOptionDao.batchUpdatePickOption(addedPickOptions);

    List<PickOption> updatedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    PickOption firstPickOption = updatedPickOptions.get(0);
    Assert.assertEquals("1000", firstPickOption.getOptionValue());
    Assert.assertEquals(1000, firstPickOption.getOptionIndex().intValue());
    Assert.assertEquals(1, firstPickOption.getIsDefault().intValue());
  }

  /**
   * Method: batchDeprecatePickOptionByPrimaryKey(long orgId, List<Long> pickOptionIds)
   */
  @Test
  public void testBatchDeprecatePickOptionByPrimaryKey() throws Exception {
    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      PickOption currOption = new PickOption();
      BeanUtils.copyProperties(pickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      pickOptions.add(currOption);
    }
    pickOptionDao.batchInsertPickOption(pickOptions);

    List<PickOption> addedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    List<Long> ids = new ArrayList<>();
    for (PickOption pickOption: addedPickOptions) {
      ids.add(pickOption.getPickOptionId());
    }
    pickOptionDao.batchDeprecatePickOptionByPrimaryKey(orgId, ids, userId);
    List<PickOption> updatedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    int deprecatedCount = 0;
    for (PickOption pickOption: updatedPickOptions) {
      if (pickOption.getIsDeprecated() == 1) {
        deprecatedCount ++;
      }
    }
    Assert.assertEquals(deprecatedCount, updatedPickOptions.size());

  }

  /**
   * Method: batchDeletePickOptionByProfileFieldId(long orgId, long profileFieldId)
   */
  @Test
  public void testBatchDeletePickOptionByProfileFieldId() throws Exception {
    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      PickOption currOption = new PickOption();
      BeanUtils.copyProperties(pickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      pickOptions.add(currOption);
    }
    pickOptionDao.batchInsertPickOption(pickOptions);

    pickOptionDao.batchDeletePickOptionByProfileFieldId(orgId, profileFieldId, userId);
    List<PickOption> deletedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, profileFieldId);
    Assert.assertEquals(0, deletedPickOptions.size());
  }

}