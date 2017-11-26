// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.userorg.dto.AddressRegionDTO;
import hr.wozai.service.user.client.userorg.dto.AddressRegionListDTO;
import hr.wozai.service.user.client.userorg.dto.OrgPickOptionDTO;
import hr.wozai.service.user.client.userorg.dto.OrgPickOptionListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileTemplateDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileTemplateListDTO;
import hr.wozai.service.user.client.userorg.facade.ProfileTemplateFacade;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.userorg.AddressRegion;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.OrgPickOptionService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.ProfileTemplateService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-21
 */
@Service("profileTemplateFacade")
public class ProfileTemplateFacadeImpl implements ProfileTemplateFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileTemplateFacadeImpl.class);

  private static final String PRESET_PROFILE_TEMPLATE_DISPLAY_NAME = "默认员工档案模板";

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  ProfileTemplateService profileTemplateService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  OrgPickOptionService orgPickOptionService;

  /**************** Methods after refraction(2016-08-08) below ****************/

  @Override
  @LogAround
  public LongDTO addContainerProfileField(
      long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      ProfileField profileField = new ProfileField();
      BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileField);
      profileField.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      profileField.setOrgId(orgId);
      profileField.setCreatedUserId(actorUserId);
      long profileFieldId = profileFieldService.addCustomContainerProfileField(profileField);
      result.setData(profileFieldId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addContainerProfileField(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public LongDTO addDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      ProfileField profileField = new ProfileField();
      BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileField);
      profileField.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      profileField.setOrgId(orgId);
      profileField.setCreatedUserId(actorUserId);
      long profileFieldId = profileFieldService.addCustomDataProfileField(profileField);
      result.setData(profileFieldId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addDataProfileField(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProfileFieldDTO getProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId) {

    ProfileFieldDTO result = new ProfileFieldDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileField profileField = profileFieldService.getProfileField(orgId, profileFieldId);
      BeanHelper.copyPropertiesHandlingJSON(profileField, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getProfileField(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO moveContainerProfileField(
      long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      ProfileField profileField = new ProfileField();
      BeanUtils.copyProperties(profileFieldDTO, profileField);
      profileField.setOrgId(orgId);
      profileField.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      profileField.setLastModifiedUserId(actorUserId);
      profileFieldService.moveContainerProfileField(profileField);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("moveContainerProfileField(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO moveDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      ProfileField profileField = new ProfileField();
      BeanUtils.copyProperties(profileFieldDTO, profileField);
      profileField.setOrgId(orgId);
      profileField.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      profileField.setLastModifiedUserId(actorUserId);
      profileFieldService.moveDataProfileFiled(profileField);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("moveContainerProfileField(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateContainerProfileField(
      long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      ProfileField profileField = new ProfileField();
      BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileField);
      profileField.setOrgId(orgId);
      profileField.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      profileField.setLastModifiedUserId(actorUserId);
      profileFieldService.updateContainerProfileField(profileField);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateContainerProfileField(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateDataProfileField(
      long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      ProfileField profileField = new ProfileField();
      BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileField);
      profileField.setOrgId(orgId);
      profileField.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      profileField.setLastModifiedUserId(actorUserId);
      profileFieldService.updateDataProfileField(profileField);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateDataProfileField(): error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) wipe out user data in UserProfile
   *  2) delete container field and containing data fields
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public VoidDTO deleteContainerProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1)
      List<ProfileField> containingDataFields =
          profileFieldService.listDataProfileFieldOfContainer(orgId, profileFieldId);
      if (!CollectionUtils.isEmpty(containingDataFields)) {
        for (ProfileField profileField: containingDataFields) {
          userProfileService.wipeUserProfileDataOfField(orgId, profileField.getProfileFieldId(), actorUserId);
        }
      }

      // 2)
      profileFieldService.deleteContainerProfileField(orgId, profileFieldId, actorUserId);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("deleteContainerProfileField(): error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) wipe out user data in UserProfile
   *  2) delete data field
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public VoidDTO deleteDataProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1)
      userProfileService.wipeUserProfileDataOfField(orgId, profileFieldId, actorUserId);
      // 2)
      profileFieldService.deleteDataProfileField(orgId, profileFieldId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("deleteDataProfileField(): error", e);
    }

    return result;
  }


  /**************** Methods before refraction(2016-08-08) beolow ****************/

  @Override
  @LogAround
  public LongDTO addPresetProfileTemplate(long orgId, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // add profileTemplate
      ProfileTemplate profileTemplate = new ProfileTemplate();
      profileTemplate.setOrgId(orgId);
      profileTemplate.setDisplayName(PRESET_PROFILE_TEMPLATE_DISPLAY_NAME);
      profileTemplate.setIsPreset(1);
      profileTemplate.setCreatedUserId(actorUserId);
      long profileTemplateId = profileTemplateService.addProfileTemplate(profileTemplate);
      // add all preset profileFields
      profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, actorUserId);
      result.setData(profileTemplateId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addPresetProfileTemplate(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public LongDTO addCustomProfileTemplate(long orgId, String displayName, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // add profileTemplate
      ProfileTemplate profileTemplate = new ProfileTemplate();
      profileTemplate.setOrgId(orgId);
      profileTemplate.setDisplayName(displayName);
      profileTemplate.setIsPreset(0);
      profileTemplate.setCreatedUserId(actorUserId);
      long profileTemplateId = profileTemplateService.addProfileTemplate(profileTemplate);
      // add all preset profileFields
      profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, actorUserId);
      result.setData(profileTemplateId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addCustomProfileTemplate(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProfileTemplateDTO getProfileTemplate(
      long orgId, long profileTemplateId, long actorUserId, long adminUserId) {

    ProfileTemplateDTO result = new ProfileTemplateDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.getProfileTemplate(orgId, profileTemplateId);
      if (null != profileTemplate) {
        BeanHelper.copyPropertiesHandlingJSON(profileTemplate, result);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getProfileTemplate(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProfileTemplateListDTO listProfileTemplate(long orgId, long actorUserId, long adminUserId) {

    ProfileTemplateListDTO result = new ProfileTemplateListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProfileTemplate> profileTemplates = profileTemplateService.listProfileTemplateId(orgId);
      List<ProfileTemplateDTO> profileTemplateDTOs = null;
      if (!CollectionUtils.isEmpty(profileTemplates)) {
        profileTemplateDTOs = new ArrayList<>();
        for (ProfileTemplate profileTemplate: profileTemplates) {
          ProfileTemplateDTO profileTemplateDTO = new ProfileTemplateDTO();
          BeanUtils.copyProperties(profileTemplate, profileTemplateDTO);
          profileTemplateDTOs.add(profileTemplateDTO);
        }
      } else {
        profileTemplateDTOs = Collections.EMPTY_LIST;
      }
      result.setProfileTemplateDTOs(profileTemplateDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listProfileTemplate(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateProfileTemplateDisplayName(
      long orgId, long profileTemplateId, String displayName, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      profileTemplateService.updateProfileTemplateDisplayName(orgId, profileTemplateId, displayName, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateProfileTemplateDisplayName(): error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) delete template if no one is using
   *  2) delete all fields and relevant pickOptions
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public VoidDTO deleteProfileTemplate(long orgId, long profileTemplateId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1)
      profileTemplateService.deleteProfileTemplate(orgId, profileTemplateId, actorUserId);
      // 2)
      profileFieldService.deleteAllProfileFieldOfTemplate(orgId, profileTemplateId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("deleteProfileTemplate(): error", e);
    }

    return result;
  }

  // Note: comment out according to product design on 2016-08-08
//  @Override
//  @LogAround
//  public LongDTO addContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId,
//                                          long adminUserId) {
//    LongDTO result = new LongDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      // add profileTemplate
//      ProfileField profileField = new ProfileField();
//      BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileField);
//      profileField.setOrgId(orgId);
//      profileField.setCreatedUserId(actorUserId);
//      long profileFieldId = profileFieldService.addCustomContainerProfileField(profileField);
//      result.setData(profileFieldId);
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("addContainerProfileField(): error", e);
//    }
//
//    return result;
//  }

//  @Override
//  @LogAround
//  public LongDTO addDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {
//
//    LongDTO result = new LongDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      // add profileTemplate
//      ProfileField profileField = new ProfileField();
//      BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileField);
//      profileField.setOrgId(orgId);
//      profileField.setCreatedUserId(actorUserId);
//      long profileFieldId = profileFieldService.addCustomDataProfileField(profileField);
//      result.setData(profileFieldId);
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("addDataProfileField(): error", e);
//    }
//
//    return result;
//  }

//  @Override
//  @LogAround
//  public ProfileFieldDTO getProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId) {
//
//    ProfileFieldDTO result = new ProfileFieldDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      ProfileField profileField = profileFieldService.getProfileField(orgId, profileFieldId);
//      BeanHelper.copyPropertiesHandlingJSON(profileField, result);
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("getProfileField(): error", e);
//    }
//
//    return result;
//  }

  @Override
  @LogAround
  public ProfileFieldDTO getProfileFieldByReferenceName(
      long orgId, long profileTemplateId, String referenceName, long actorUserId, long adminUserId) {
    ProfileFieldDTO result = new ProfileFieldDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileField profileField = profileFieldService.getProfileField(orgId, profileTemplateId, referenceName);
      BeanHelper.copyPropertiesHandlingJSON(profileField, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getProfileField(): error", e);
    }

    return result;  }

  @Override
  @LogAround
  public ProfileFieldListDTO listProfileField(long orgId, long profileTemplateId, long actorUserId, long adminUserId) {

    ProfileFieldListDTO result = new ProfileFieldListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
      List<ProfileFieldDTO> profileFieldDTOs = null;
      if (!CollectionUtils.isEmpty(profileFields)) {
        profileFieldDTOs = new ArrayList<>();
        for (ProfileField profileField: profileFields) {
          ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
          BeanHelper.copyPropertiesHandlingJSON(profileField, profileFieldDTO);
          profileFieldDTOs.add(profileFieldDTO);
        }
      } else {
        profileFieldDTOs = Collections.EMPTY_LIST;
      }
      result.setProfileFieldDTOs(profileFieldDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listAllProfileFieldOfTemplate(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public AddressRegionListDTO listAddressRegion(long orgId, long parentId, long actorUserId, long adminUserId) {

    AddressRegionListDTO result = new AddressRegionListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<AddressRegion> addressRegions = profileFieldService.listAddressRegion(parentId);
      List<AddressRegionDTO> addressRegionDTOs = null;
      if (!CollectionUtils.isEmpty(addressRegions)) {
        addressRegionDTOs = new ArrayList<>();
        for (int i = 0; i < addressRegions.size(); i++) {
          AddressRegionDTO addressRegionDTO = new AddressRegionDTO();
          BeanUtils.copyProperties(addressRegions.get(i), addressRegionDTO);
          addressRegionDTOs.add(addressRegionDTO);
        }
      } else {
        addressRegionDTOs = Collections.EMPTY_LIST;
      }
      result.setAddressRegionDTOs(addressRegionDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listAddressRegion(): error", e);
    }

    return result;

  }

  @Override
  @LogAround
  public OrgPickOptionListDTO listOrgPickOptionOfConfigType(long orgId, int configType) {

    OrgPickOptionListDTO result = new OrgPickOptionListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<OrgPickOption> orgPickOptions = orgPickOptionService.listPickOptionOfConfigType(orgId, configType);
      List<OrgPickOptionDTO> orgPickOptionDTOs = new ArrayList<>();
      for (OrgPickOption orgPickOption: orgPickOptions) {
        OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
        BeanUtils.copyProperties(orgPickOption, orgPickOptionDTO);
        orgPickOptionDTOs.add(orgPickOptionDTO);
      }
      result.setOrgPickOptionDTOs(orgPickOptionDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listPickOptionOfConfigType(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchUpdateOrgPickOptions(long orgId, OrgPickOptionListDTO orgPickOptionListDTO, long actorUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<OrgPickOption> orgPickOptions = new ArrayList<>();
      if (null != orgPickOptionListDTO
          && !CollectionUtils.isEmpty(orgPickOptionListDTO.getOrgPickOptionDTOs())) {
        for (OrgPickOptionDTO orgPickOptionDTO: orgPickOptionListDTO.getOrgPickOptionDTOs()) {
          OrgPickOption orgPickOption = new OrgPickOption();
          BeanUtils.copyProperties(orgPickOptionDTO, orgPickOption);
          orgPickOptions.add(orgPickOption);
        }
      }
      orgPickOptionService.batchUpdateOrgPickOptions(orgId, orgPickOptions, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("batchUpdateOrgPickOptions(): error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProfileFieldListDTO listAllProfileFieldOfOrg(long orgId, long actorUserId, long adminUserId) {

    ProfileFieldListDTO result = new ProfileFieldListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfOrg(orgId);
      List<ProfileFieldDTO> profileFieldDTOs = null;
      if (!CollectionUtils.isEmpty(profileFields)) {
        profileFieldDTOs = new ArrayList<>();
        for (ProfileField profileField: profileFields) {
          ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
          BeanHelper.copyPropertiesHandlingJSON(profileField, profileFieldDTO);
          profileFieldDTOs.add(profileFieldDTO);
        }
      } else {
        profileFieldDTOs = Collections.EMPTY_LIST;
      }
      result.setProfileFieldDTOs(profileFieldDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listAllProfileFieldOfOrg(): error", e);
    }

    return result;

  }

  @Override
  @LogAround
  public VoidDTO updateUserProfileConfig(
      long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserProfileConfig userProfileConfig = new UserProfileConfig();
      if (null != profileFieldDTO) {
        BeanUtils.copyProperties(profileFieldDTO, userProfileConfig);
        userProfileConfig.setOrgId(orgId);
        userProfileConfig.setLastModifiedUserId(actorUserId);
      }
      profileFieldService.updateUserProfileConfig(userProfileConfig);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateUserProfileConfig(): error", e);
    }

    return result;
  }

}
