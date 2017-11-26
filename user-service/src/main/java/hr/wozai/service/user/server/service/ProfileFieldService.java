// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.userorg.AddressRegion;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-09
 */
public interface ProfileFieldService {

  /**
   * Add data-type profileField
   *
   * @param profileField
   * @return
   */
  long addCustomDataProfileField(ProfileField profileField);

  /**
   * Add container-type profileField
   *
   * @param profileField
   * @return
   */
  long addCustomContainerProfileField(ProfileField profileField);

  /**
   * Add all preset fields for a profileTemplate
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   */
  void addAllPresetFieldForProfileTemplate(long orgId, long profileTemplateId, long actorUserId);

  /**
   * Get specific profileField
   *
   * @param orgId
   * @param profileFieldId
   * @return
   */
  ProfileField getProfileField(long orgId, long profileFieldId);

  /**
   * Get specific profileField
   *
   * @param orgId
   * @param profileTemplateId
   * @param referenceName
   * @return
   */
  ProfileField getProfileField(long orgId, long profileTemplateId, String referenceName);

  /**
   * List all profileFields of a profileTemplate
   *
   * @param orgId
   * @param profileTemplateId
   * @return
   */
  List<ProfileField> listAllProfileFieldOfTemplate(long orgId, long profileTemplateId);

  /**
   * List data profileFields of a profileTemplate
   *
   * @param orgId
   * @param profileTemplateId
   * @return
   */
  List<ProfileField> listDataProfileFieldOfTemplate(long orgId, long profileTemplateId);

  /**
   * List data profileFields of a container
   *
   * @param orgId
   * @param containerFieldId
   * @return
   */
  List<ProfileField> listDataProfileFieldOfContainer(long orgId, long containerFieldId);

  /**
   * Update data-type field
   *
   * @param profileField
   */
  void updateDataProfileField(ProfileField profileField);

  /**
   * Update container-type field
   *
   * @param profileField
   */
  void updateContainerProfileField(ProfileField profileField);

  /**
   * Move data profile field in field-list
   *
   * @param profileField: requiring profileFieldId and logicalIndex
   */
  void moveDataProfileFiled(ProfileField profileField);

  /**
   * Move container profileField and contained data fields in field-list
   *
   * @param profileField: requiring profileFieldId and logicalIndex
   */
  void moveContainerProfileField(ProfileField profileField);

  /**
   * Delete data-type profileField
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   */
  void deleteDataProfileField(long orgId, long profileFieldId, long actorUserId);

  /**
   * Delete container-type profileField
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   */
  void deleteContainerProfileField(long orgId, long profileFieldId, long actorUserId);

  /**
   * Delete all profileFields of a template
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   */
  void deleteAllProfileFieldOfTemplate(long orgId, long profileTemplateId, long actorUserId);

  List<PickOption> listPickOptionsByOrgIdAndPickOptionIds(long orgId, List<Long> pickOptionIds);

  List<PickOption> listPickOptionByOrgIdAndProfileFieldIdForUpdate(long orgId, long profileFieldId);

  List<AddressRegion> listAddressRegion(long parentId);


  /******** Methods after refraction ********/

  List<ProfileField> listAllProfileFieldOfOrg(long orgId);

  List<ProfileField> listAllProfileFieldOfOrgForUpdate(long orgId);

  void updateUserProfileConfig(UserProfileConfig userProfileConfig);

}
