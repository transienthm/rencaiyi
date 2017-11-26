// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

import java.util.List;

import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringListDTO;
import hr.wozai.service.user.client.userorg.dto.AddressRegionListDTO;
import hr.wozai.service.user.client.userorg.dto.OrgPickOptionDTO;
import hr.wozai.service.user.client.userorg.dto.OrgPickOptionListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileTemplateDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileTemplateListDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;


/**
 * The facade that:
 *  1) for Admin user: config profileTemplate and onboardingTemplate
 *  2) for HR user: create an onboardingActivity
 *  3) for Staff user: go thru onboardingActivity
 *
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@ThriftService
public interface ProfileTemplateFacade {

   /**
   *  Add the preset profileTemplate
   *
   * @param orgId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO addPresetProfileTemplate(long orgId, long actorUserId, long adminUserId);

  /**
   * Add custom profileTemplate with preset fields
   *
   * @param orgId
   * @param displayName
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO addCustomProfileTemplate(long orgId, String displayName, long actorUserId, long adminUserId);

  /**
   * Get profileTemplate
   *
   * @param profileTemplateId
   * @return
   */
  @ThriftMethod
  ProfileTemplateDTO getProfileTemplate(long orgId, long profileTemplateId, long actorUserId, long adminUserId);

  /**
   * List profileTemplate of org
   *
   * @param orgId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ProfileTemplateListDTO listProfileTemplate(long orgId, long actorUserId, long adminUserId);

  /**
   * Update the display name of profileTemplate
   *
   * @param orgId
   * @param displayName
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateProfileTemplateDisplayName(
      long orgId, long profileTemplateId, String displayName, long actorUserId, long adminUserId);

  /**
   * Delete a profileTemplates
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO deleteProfileTemplate(long orgId, long profileTemplateId, long actorUserId, long adminUserId);

  /**
   * Add container-type profileField
   *
   * @param orgId
   * @param profileFieldDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO addContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

  /**
   * Add data-type profileField
   *
   * @param orgId
   * @param profileFieldDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO addDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

   /**
   * Get specific profileField
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ProfileFieldDTO getProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId);

  /**
   * Get specific profileField
   *
   * @param orgId
   * @param profileTemplateId
   * @param referenceName
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ProfileFieldDTO getProfileFieldByReferenceName(
      long orgId, long profileTemplateId, String referenceName, long actorUserId, long adminUserId);

  /**
   * List all profileFields of a profileTemplate
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ProfileFieldListDTO listProfileField(long orgId, long profileTemplateId, long actorUserId, long adminUserId);

  /**
   * Update container profileField
   *
   * @param orgId
   * @param profileFieldDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

  /**
   * Update data profileField
   *
   * @param orgId
   * @param profileFieldDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

  /**
   * Move container profileField and contained data profileFields
   *
   * @param orgId
   * @param profileFieldDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO moveContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

  /**
   * Move data profileField
   *
   * @param orgId
   * @param profileFieldDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO moveDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

  /**
   * Delete container profileField
   *
   * @param profileFieldId
   * @param orgId
   */
  @ThriftMethod
  VoidDTO deleteContainerProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId);

  /**
   * Delete data profileField
   *
   * @param profileFieldId
   * @param orgId
   */
  @ThriftMethod
  VoidDTO deleteDataProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId);

  @ThriftMethod
  AddressRegionListDTO listAddressRegion(long orgId, long parentId, long actorUserId, long adminUserId);

  /******** Methods after refraction ********/

  @ThriftMethod
  OrgPickOptionListDTO listOrgPickOptionOfConfigType(long orgId, int configType);

  @ThriftMethod
  VoidDTO batchUpdateOrgPickOptions(long orgId, OrgPickOptionListDTO orgPickOptionListDTO, long actorUserId);

  @ThriftMethod
  ProfileFieldListDTO listAllProfileFieldOfOrg(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateUserProfileConfig(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId);

}
