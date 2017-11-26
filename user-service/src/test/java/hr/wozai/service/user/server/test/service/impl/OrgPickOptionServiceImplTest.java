// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.enums.ConfigType;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.service.OrgPickOptionService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
public class OrgPickOptionServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OrgPickOptionServiceImplTest.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  private OrgPickOptionService orgPickOptionService;

  // data
  String orgFullName = "北京闪签科技有限公司";
  String orgShortName = "闪签";
  String orgAvatarUrl = "http://some-url.com";

  long mockOrgId = 19999999L;
  long mockUserId = 29999999L;

  @Before
  public void setup() {
  }

  /**
   * Case #1: normal
   *
   */
  @Test
  public void testInitJobTitleAndJobLevelOfOrgCase1() {

    // prepare
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);

    // verify
    List<OrgPickOption> insertedJobTitles =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    List<OrgPickOption> insertedJobLevels =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_LEVEL.getCode());
    Assert.assertTrue(!CollectionUtils.isEmpty(insertedJobTitles));
    Assert.assertTrue(!CollectionUtils.isEmpty(insertedJobLevels));

  }

  /**
   * Case #2: abnormal, add twice for an org
   *
   */
  @Test
  public void testInitJobTitleAndJobLevelOfOrgCase2() {

    // prepare
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);

    // verify
    List<OrgPickOption> insertedJobTitles =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    List<OrgPickOption> insertedJobLevels =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_LEVEL.getCode());
    Assert.assertTrue(!CollectionUtils.isEmpty(insertedJobTitles));
    Assert.assertTrue(!CollectionUtils.isEmpty(insertedJobLevels));

    thrown.expect(ServiceStatusException.class);
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);

  }

  /**
   * Case #1: normal
   *
   */
  @Test
  public void testBatchUpdateOrgPickOptionsCase1() {

    /**
     * prepare
     */
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);

    List<OrgPickOption> insertedJobTitles =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    int initialOptionCount = insertedJobTitles.size();

    LOGGER.info("initialOptionCount={}", initialOptionCount);

    /**
     * verify
     */

    // 1) add
    OrgPickOption newOption = new OrgPickOption();
    BeanUtils.copyProperties(insertedJobTitles.get(initialOptionCount - 1), newOption);
    newOption.setOrgPickOptionId(null);
    newOption.setOptionIndex(initialOptionCount);
    String newOptionValue = "BrandNewJobTitle";
    newOption.setOptionValue(newOptionValue);
    insertedJobTitles.add(newOption);
    orgPickOptionService.batchUpdateOrgPickOptions(mockOrgId, insertedJobTitles, mockUserId);

    List<OrgPickOption> addedJobTitles =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    Assert.assertEquals(initialOptionCount + 1, addedJobTitles.size());
    Assert.assertEquals(newOptionValue, addedJobTitles.get(initialOptionCount).getOptionValue());

    // 2) deprecate
    addedJobTitles.remove(addedJobTitles.size() - 1);
    orgPickOptionService.batchUpdateOrgPickOptions(mockOrgId, addedJobTitles, mockUserId);
    List<OrgPickOption> deprecatedJobTitles =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    Assert.assertEquals(initialOptionCount + 1, deprecatedJobTitles.size());
    Assert.assertEquals(1, deprecatedJobTitles.get(deprecatedJobTitles.size() - 1).getIsDeprecated().intValue());

    // 3) update
    String updateOptionValue = "UpdatedJobTitle";
    deprecatedJobTitles.get(deprecatedJobTitles.size() - 2).setOptionValue(updateOptionValue);
    orgPickOptionService.batchUpdateOrgPickOptions(mockOrgId, deprecatedJobTitles, mockUserId);
    List<OrgPickOption> updatedJobTitles =
        orgPickOptionService.listPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    Assert.assertEquals(updateOptionValue, updatedJobTitles.get(updatedJobTitles.size() - 2).getOptionValue());

  }

  /**
   * Case 2: abnormal, empty list
   */
  @Test
  public void testBatchUpdateOrgPickOptionsCase2() {
    thrown.expect(ServiceStatusException.class);
    orgPickOptionService.batchUpdateOrgPickOptions(mockOrgId, Collections.EMPTY_LIST, mockUserId);
  }

  /**
   * Case 3: abnormal, UO_INVALID_PICK_OPTION
   */
  @Test
  public void testBatchUpdateOrgPickOptionsCase3() {
    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    orgPickOptions.add(new OrgPickOption());
    thrown.expect(ServiceStatusException.class);
    orgPickOptionService.batchUpdateOrgPickOptions(mockOrgId, orgPickOptions, mockUserId);
  }

  }
