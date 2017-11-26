package hr.wozai.service.user.server.service;

import java.util.List;

import hr.wozai.service.user.server.model.userorg.ProfileTemplate;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-29
 */
public interface ProfileTemplateService {

  /**************** Methods after refraction(2016-08-08) below ****************/

  ProfileTemplate findTheOnlyProfileTemplateOfOrg(long orgId);

  /**************** Methods before refraction(2016-08-08) below ****************/

  /**
   * Add a new profileTemplate, including related preset fields
   *
   * @param profileTemplate
   * @return
   */
  long addProfileTemplate(ProfileTemplate profileTemplate);

  /**
   * Get a profileTemplate
   *
   * @param profileTemplateId
   * @param orgId
   * @return
   */
  ProfileTemplate getProfileTemplate(long orgId, long profileTemplateId);

  /**
   * List all profileTemplates of org
   *
   * @param orgId
   * @return
   */
  List<ProfileTemplate> listProfileTemplateId(long orgId);

  /**
   * Update displayName of profileTemplate
   *
   * @param orgId
   * @param profileTemplateId
   * @param displayName
   * @param actorUserId
   */
  void updateProfileTemplateDisplayName(long orgId, long profileTemplateId, String displayName, long actorUserId);

  /**
   * Display profileTemplate
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   */
  void deleteProfileTemplate(long orgId, long profileTemplateId, long actorUserId);

}
