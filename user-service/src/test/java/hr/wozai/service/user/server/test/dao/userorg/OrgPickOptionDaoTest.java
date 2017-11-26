package hr.wozai.service.user.server.test.dao.userorg;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.user.server.dao.userorg.OrgPickOptionDao;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public class OrgPickOptionDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldDaoTest.class);

  @Autowired
  OrgPickOptionDao orgPickOptionDao;

  long mockUserId = 199999999L;
  long mockOrgId = 299999999L;
  int configType = 1;
  OrgPickOption orgPickOption = null;

  {
    orgPickOption = new OrgPickOption();
    orgPickOption.setOrgId(mockOrgId);
    orgPickOption.setConfigType(configType);
    orgPickOption.setIsDefault(0);
    orgPickOption.setIsDeprecated(0);
    orgPickOption.setCreatedUserId(mockUserId);
  }

  @Test
  public void testBatchInsertOrgPickOption() throws Exception {

    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      OrgPickOption currOption = new OrgPickOption();
      BeanUtils.copyProperties(orgPickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      orgPickOptions.add(currOption);
    }
    orgPickOptionDao.batchInsertOrgPickOption(orgPickOptions);

  }

  @Test
  public void testListOrgPickOptionByConfigType() throws Exception {

    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      OrgPickOption currOption = new OrgPickOption();
      BeanUtils.copyProperties(orgPickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      orgPickOptions.add(currOption);
    }
    orgPickOptionDao.batchInsertOrgPickOption(orgPickOptions);

    List<OrgPickOption> addedOrgPickOptions = orgPickOptionDao.listOrgPickOptionByConfigType(mockOrgId, configType);
    Assert.assertEquals(optionCount, addedOrgPickOptions.size());
    addedOrgPickOptions = orgPickOptionDao.listOrgPickOptionByConfigTypeForUpdate(mockOrgId, configType);
    Assert.assertEquals(optionCount, addedOrgPickOptions.size());
  }


  @Test
  public void testListOrgPickOptionByOrgIdAndOrgPickOptionIds() {

    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      OrgPickOption currOption = new OrgPickOption();
      BeanUtils.copyProperties(orgPickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      orgPickOptions.add(currOption);
    }
    orgPickOptionDao.batchInsertOrgPickOption(orgPickOptions);
    List<OrgPickOption> insertedPOs = orgPickOptionDao.listOrgPickOptionByConfigType(mockOrgId, configType);
    ArrayList<Long> orgPickOptionIds = new ArrayList<>();
    for (OrgPickOption orgPickOption: insertedPOs) {
      orgPickOptionIds.add(orgPickOption.getOrgPickOptionId());
    }

    // verify
    List<OrgPickOption> insertedPOsByIds = orgPickOptionDao.listOrgPickOptionByOrgIdAndOrgPickOptionIds(mockOrgId, orgPickOptionIds);
    Assert.assertEquals(optionCount, insertedPOsByIds.size());

  }

  /**
   * Method: batchUpdateOrgPickOption(List<OrgPickOption> orgPickOptions)
   */
  @Test
  public void testBatchUpdateOrgPickOption() throws Exception {
    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      OrgPickOption currOption = new OrgPickOption();
      BeanUtils.copyProperties(orgPickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      orgPickOptions.add(currOption);
    }
    orgPickOptionDao.batchInsertOrgPickOption(orgPickOptions);

    List<OrgPickOption> addedOrgPickOptions = orgPickOptionDao.listOrgPickOptionByConfigType(mockOrgId, configType);
    for (OrgPickOption orgPickOption: addedOrgPickOptions) {
      orgPickOption.setOptionValue("1000");
      orgPickOption.setOptionIndex(1000);
      orgPickOption.setIsDefault(1);
      orgPickOption.setLastModifiedUserId(mockUserId);
    }
    orgPickOptionDao.batchUpdateOrgPickOption(addedOrgPickOptions);

    List<OrgPickOption> updatedOrgPickOptions = orgPickOptionDao.listOrgPickOptionByConfigType(mockOrgId, configType);
    OrgPickOption firstOrgPickOption = updatedOrgPickOptions.get(0);
    Assert.assertEquals("1000", firstOrgPickOption.getOptionValue());
    Assert.assertEquals(1000, firstOrgPickOption.getOptionIndex().intValue());
    Assert.assertEquals(1, firstOrgPickOption.getIsDefault().intValue());
  }

  /**
   * Method: batchDeprecateOrgPickOptionByPrimaryKey(long orgId, List<Long> orgPickOptionIds)
   */
  @Test
  public void testBatchDeprecateOrgPickOptionByPrimaryKey() throws Exception {
    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    int optionCount = 10;
    for (int i = 0; i < optionCount; i++) {
      OrgPickOption currOption = new OrgPickOption();
      BeanUtils.copyProperties(orgPickOption, currOption);
      currOption.setOptionValue(i + "");
      currOption.setOptionIndex(i);
      orgPickOptions.add(currOption);
    }
    orgPickOptionDao.batchInsertOrgPickOption(orgPickOptions);

    List<OrgPickOption> addedOrgPickOptions = orgPickOptionDao.listOrgPickOptionByConfigType(mockOrgId, configType);
    List<Long> ids = new ArrayList<>();
    for (OrgPickOption orgPickOption: addedOrgPickOptions) {
      ids.add(orgPickOption.getOrgPickOptionId());
    }
    orgPickOptionDao.batchDeprecateOrgPickOptionByPrimaryKey(mockOrgId, ids, mockUserId);
    List<OrgPickOption> updatedOrgPickOptions = orgPickOptionDao.listOrgPickOptionByConfigType(mockOrgId, configType);
    int deprecatedCount = 0;
    for (OrgPickOption orgPickOption: updatedOrgPickOptions) {
      if (orgPickOption.getIsDeprecated() == 1) {
        deprecatedCount ++;
      }
    }
    Assert.assertEquals(deprecatedCount, updatedOrgPickOptions.size());

  }

}