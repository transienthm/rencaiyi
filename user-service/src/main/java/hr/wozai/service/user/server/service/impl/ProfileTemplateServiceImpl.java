// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.userorg.MetaUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.ProfileTemplateDao;
import hr.wozai.service.user.server.helper.ProfileTemplateHelper;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.service.ProfileTemplateService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-29
 */
@Service("profileTemplateService")
public class ProfileTemplateServiceImpl implements ProfileTemplateService {


  private static Logger LOGGER = LoggerFactory.getLogger(ProfileTemplateServiceImpl.class);

  @Autowired
  ProfileTemplateDao profileTemplateDao;

  /**
   * Only used for deleteProfileTemplate():
   *  put 1) check if template in use, and 2) delete template in one transaction
   */
  @Autowired
  MetaUserProfileDao metaUserProfileDao;

  @Override
  public ProfileTemplate findTheOnlyProfileTemplateOfOrg(long orgId) {
    ProfileTemplate profileTemplate = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(orgId);
    if (null == profileTemplate) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND);
    }
    return profileTemplate;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addProfileTemplate(ProfileTemplate profileTemplate) {
    if (!ProfileTemplateHelper.isValidAddRequest(profileTemplate)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    profileTemplateDao.insertProfileTemplate(profileTemplate);
    return profileTemplate.getProfileTemplateId();
  }

  @Override
  @LogAround
  public ProfileTemplate getProfileTemplate(long orgId, long profileTemplateId) {
    ProfileTemplate profileTemplate =
        profileTemplateDao.findProfileTemplateByPrimaryKey(orgId, profileTemplateId);
    if (null == profileTemplate) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND);
    }
    return profileTemplate;
  }

  @Override
  @LogAround
  public List<ProfileTemplate> listProfileTemplateId(long orgId) {
    List<ProfileTemplate> profileTemplates = profileTemplateDao.listProfileTemplateByOrgId(orgId);
    return profileTemplates;
  }

  @Override
  @LogAround
  public void updateProfileTemplateDisplayName(
      long orgId, long profileTemplateId, String displayName, long actorUserId) {
    if (StringUtils.isNullOrEmpty(displayName)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    profileTemplateDao.updateProfileTemplateDisplayName(orgId, profileTemplateId, displayName, actorUserId);
  }

  /**
   * Delete profileTemplate if no one uses
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteProfileTemplate(long orgId, long profileTemplateId, long actorUserId) {

    List<Long> occupiedUserIds = metaUserProfileDao.listUserIdByProfileTemplateId(orgId, profileTemplateId);
    if (!CollectionUtils.isEmpty(occupiedUserIds)) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_TEMPLATE_IN_USE);
    }

    profileTemplateDao.deleteProfileTemplateByOrgId(orgId, profileTemplateId, actorUserId);
  }


}
