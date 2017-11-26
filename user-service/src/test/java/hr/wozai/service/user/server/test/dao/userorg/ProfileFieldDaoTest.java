// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.user.server.dao.userorg.ProfileFieldDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class ProfileFieldDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldDaoTest.class);

  @Autowired
  ProfileFieldDao profileFieldDao;

  // data
  long mockUserId = 199999999L;
  long mockOrgId = 299999999L;
  long mockProfileTemplateId = 399999999L;
  long mockContainerId = 499999999L;
  String displayName = "字段";
  String referenceName = "Field";
  int logicalIndex = 40;
  int physicalIndex = 41;
  int dataType = DataType.LONG_TEXT.getCode();
  int isTypeSpecEditable = 1;
  int isSystemRequired = 1;
  int isOnboardingStaffEditable = 1;
  int isActiveStaffEditable = 1;
  int isPublicVisible = 1;
  int isPublicVisibleEditable = 1;
  int isEnabled = 1;
  int isEnabledEditable = 0;
  int isMandatory = 1;
  int isMandatoryEditable = 0;
  ProfileField profileField;

  {
    profileField = new ProfileField();
    profileField.setOrgId(mockOrgId);
    profileField.setProfileTemplateId(mockProfileTemplateId);
    profileField.setContainerId(mockContainerId);
    profileField.setDisplayName(displayName);
    profileField.setReferenceName(referenceName);
    profileField.setLogicalIndex(logicalIndex);
    profileField.setPhysicalIndex(physicalIndex);
    profileField.setDataType(dataType);
    profileField.setIsTypeSpecEditable(isTypeSpecEditable);
    profileField.setIsSystemRequired(isSystemRequired);
    profileField.setIsOnboardingStaffEditable(isOnboardingStaffEditable);
    profileField.setIsActiveStaffEditable(isActiveStaffEditable);
    profileField.setIsPublicVisible(isPublicVisible);
    profileField.setIsPublicVisibleEditable(isPublicVisibleEditable);
    profileField.setIsEnabled(isEnabled);
    profileField.setIsEnabledEditable(isEnabledEditable);
    profileField.setIsMandatory(isMandatory);
    profileField.setIsMandatoryEditable(isMandatoryEditable);
    profileField.setCreatedUserId(mockUserId);
  }

  @Before
  public void setup() {}

  /**
   * Test:
   *  1) insertProfileField()
   *  2) batchInsertProfileField()
   *  3) findProfileFieldByOrgIdAndPrimaryKey()
   *  4) findProfileFieldByOrgIdAndPrimaryKeyForUpdate()
   *  5) listProfileFieldByProfileTemplateId()
   *  6) listProfileFieldByProfileTemplateIdForUpdate()
   *  7) findNextLogicalIndexByProfileTemplateIdForUpdate()
   *  8) batchUpdateLogicalIndexAndContainerIdByPrimaryKey()
   *  9) updateProfileFieldByPrimaryKeySelective()
   *  10) deleteProfileFieldByPrimaryKey()
   *
   *  11) listDataProfileFieldByProfileTemplateId()
   */
  @Test
  public void testAll() {

    // 1) 3) 4)
    long insertedProfileFieldId = profileFieldDao.insertProfileField(profileField);
    ProfileField insertedProfileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(mockOrgId, insertedProfileFieldId);
    Assert.assertEquals(profileField.getProfileFieldId(), insertedProfileField.getProfileFieldId());
    insertedProfileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKeyForUpdate(mockOrgId, insertedProfileFieldId);
    Assert.assertEquals(profileField.getProfileFieldId(), insertedProfileField.getProfileFieldId());

    // 10)
    profileFieldDao.deleteProfileFieldByPrimaryKey(mockOrgId, insertedProfileFieldId, mockUserId);
    ProfileField deletedProfileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(mockOrgId, insertedProfileFieldId);
    Assert.assertEquals(null, deletedProfileField);


    // 2) 5) 6) 7)
    List<ProfileField> profileFields = new ArrayList<>();
    int count = 5;
    profileField.setProfileTemplateId(mockProfileTemplateId + 1);
    for (int i = 0; i < count; i++) {
      profileFields.add(profileField);
    }
    profileFieldDao.batchInsertProfileField(profileFields);
    List<ProfileField> insertedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateId(mockOrgId, mockProfileTemplateId + 1);
    Assert.assertEquals(count, insertedProfileFields.size());
    insertedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(mockOrgId, mockProfileTemplateId + 1);
    Assert.assertEquals(count, insertedProfileFields.size());
    int nextLogicalIndex =
        profileFieldDao.findNextLogicalIndexByProfileTemplateIdForUpdate(mockOrgId, mockProfileTemplateId + 1);
    Assert.assertEquals(nextLogicalIndex, count);

    // 8)
    for (ProfileField profileField: insertedProfileFields) {
      profileField.setContainerId(mockContainerId + 1);
      profileField.setLogicalIndex(logicalIndex + 1);
      profileField.setLastModifiedUserId(mockUserId);
    }
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(insertedProfileFields);

    // 11)
    List<ProfileField> dataFields =
        profileFieldDao.listDataProfileFieldByProfileTemplateId(mockOrgId, mockProfileTemplateId);
    System.out.println("pf=" + profileFields);
    System.out.println("df=" + dataFields);
    Assert.assertNotEquals(dataFields.size(), profileFields.size());

  }

  @Test
  public void testListDataProfileFieldByContainerId() {

    // prepare
    int count = 10;
    long mockContainerId = 199999999L;
    profileField.setContainerId(mockContainerId);
    for (int i = 0; i < count; i++) {
      profileFieldDao.insertProfileField(profileField);
    }

    // verify
    List<ProfileField> insertedPFs = profileFieldDao.listDataProfileFieldByContainerId(mockOrgId, mockContainerId);
    Assert.assertEquals(count, insertedPFs.size());
    for (ProfileField profileField: insertedPFs) {
      Assert.assertEquals(mockContainerId, profileField.getContainerId().longValue());
    }

  }

  @Test
  public void testFindProfileFieldByReferenceName() {

    // prepare
    profileFieldDao.insertProfileField(profileField);

    // verify
    ProfileField insertedPF =
        profileFieldDao.findProfileFieldByReferenceName(mockOrgId, mockProfileTemplateId, referenceName);
    Assert.assertNotNull(insertedPF);

  }

  @Test
  public void testListDataProfileFieldByProfileTemplateIdForUpdate() {

    // prepare
    List<ProfileField> profileFields = new ArrayList<>();
    int count = 5;
    profileField.setProfileTemplateId(mockProfileTemplateId + 1);
    for (int i = 0; i < count; i++) {
      profileFields.add(profileField);
    }
    profileFieldDao.batchInsertProfileField(profileFields);

    // verify
    List<ProfileField> insertedProfileFields =
        profileFieldDao.listDataProfileFieldByProfileTemplateIdForUpdate(mockOrgId, mockProfileTemplateId + 1);
    Assert.assertEquals(count, insertedProfileFields.size());

  }

  @Test
  public void testBatchDeleteProfileFieldByPrimaryKey() {

    // prepare
    List<ProfileField> profileFields = new ArrayList<>();
    int count = 5;
    profileField.setProfileTemplateId(mockProfileTemplateId + 1);
    for (int i = 0; i < count; i++) {
      profileFields.add(profileField);
    }
    profileFieldDao.batchInsertProfileField(profileFields);
    List<ProfileField> insertedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateId(mockOrgId, mockProfileTemplateId + 1);
    Assert.assertEquals(count, insertedProfileFields.size());
    int toDeleteCount = 2;
    List<Long> toDelIds = new ArrayList<>();
    for (int i = 0; i < toDeleteCount; i++) {
      toDelIds.add(insertedProfileFields.get(i).getProfileFieldId());
    }

    // verify
    profileFieldDao.batchDeleteProfileFieldByPrimaryKey(mockOrgId, toDelIds, mockUserId);
    List<ProfileField> remainingPFs =
        profileFieldDao.listProfileFieldByProfileTemplateId(mockOrgId, mockProfileTemplateId + 1);
    Assert.assertEquals(count - toDeleteCount, remainingPFs.size());

  }

  @Test
  public void testUpdateProfileFieldByPrimaryKeySelective() {

    // prepare
    long insertedPFID = profileFieldDao.insertProfileField(profileField);

    // verify
    ProfileField insertedPF = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(mockOrgId, insertedPFID);
    String updateDisplayName = "updateDisplayName";
    insertedPF.setDisplayName(updateDisplayName);
    profileFieldDao.updateProfileFieldByPrimaryKeySelective(insertedPF);
    ProfileField updatedPF = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(mockOrgId, insertedPFID);
    Assert.assertEquals(updateDisplayName, updatedPF.getDisplayName());

  }


}
