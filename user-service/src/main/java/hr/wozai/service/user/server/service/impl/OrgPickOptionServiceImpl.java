// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.FastJSONUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.enums.ConfigType;
import hr.wozai.service.user.server.dao.userorg.OrgPickOptionDao;
import hr.wozai.service.user.server.helper.OrgPickOptionHelper;
import hr.wozai.service.user.server.helper.PickOptionHelper;
import hr.wozai.service.user.server.helper.ProfileFieldHelper;
import hr.wozai.service.user.server.model.userorg.EncryptedPickOption;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.service.OrgPickOptionService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-07-26
 */
@Service("orgPickOptionService")
public class OrgPickOptionServiceImpl implements OrgPickOptionService {

  private static Logger LOGGER = LoggerFactory.getLogger(OrgPickOptionServiceImpl.class);

  private List<OrgPickOption> presetJobTitles = null;
  private List<OrgPickOption> presetJobLevels = null;

  @Autowired
  OrgPickOptionDao orgPickOptionDao;

  @PostConstruct
  public void init() {
    BufferedReader br = null;
    try {
      InputStream resource = getClass().getResourceAsStream("/preset/preset-org-pick-options.json");
      br = new BufferedReader(new InputStreamReader(resource));
      String line = null;
      StringBuilder stringBuilder = new StringBuilder();
      while (null != (line = br.readLine())) {
        stringBuilder.append(line);
      }
      JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
      presetJobTitles = FastJSONUtils.convertJSONArrayToObjectList(
          jsonObject.getJSONArray(ConfigType.JOB_TITLE.getDesc()), OrgPickOption.class);
      presetJobLevels = FastJSONUtils.convertJSONArrayToObjectList(
          jsonObject.getJSONArray(ConfigType.JOB_LEVEL.getDesc()), OrgPickOption.class);
      LOGGER.info("init(): presetJobTitles={}, presetJobLevels={}", presetJobTitles, presetJobLevels);
    } catch (Exception e) {
      LOGGER.error("init()-error: fail to parse preset config file");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_JSON);
    }

  }

  /**
   * Steps:
   *  1) make sure no existed config of this org
   *  2) insert presetJobTitles
   *  3) insert presetJobLevels
   *
   * @param orgId
   */
  @Override
  @LogAround
  @Transactional(
      value = "transactionManager",
      rollbackFor = Exception.class)
  public void initJobTitleAndJobLevelOfOrg(long orgId) {

    // 1)
    List<OrgPickOption> existedJobTitles =
        orgPickOptionDao.listOrgPickOptionByConfigType(orgId, ConfigType.JOB_TITLE.getCode());
    List<OrgPickOption> existedJobLevels =
        orgPickOptionDao.listOrgPickOptionByConfigType(orgId, ConfigType.JOB_LEVEL.getCode());
    if (!CollectionUtils.isEmpty(existedJobTitles)
        || !CollectionUtils.isEmpty(existedJobLevels)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // 2)
    List<OrgPickOption> clonedPresetJobTitles = cloneListOfOrgPickOptions(presetJobTitles);
    for (OrgPickOption jobTitle: clonedPresetJobTitles) {
      jobTitle.setOrgId(orgId);
      jobTitle.setConfigType(ConfigType.JOB_TITLE.getCode());
      jobTitle.setIsDeprecated(0);
      jobTitle.setCreatedUserId(-1L);
    }
    orgPickOptionDao.batchInsertOrgPickOption(clonedPresetJobTitles);

    // 3)
    List<OrgPickOption> clonedPresetJobLevels = cloneListOfOrgPickOptions(presetJobLevels);
    for (OrgPickOption jobLevel: clonedPresetJobLevels) {
      jobLevel.setOrgId(orgId);
      jobLevel.setConfigType(ConfigType.JOB_LEVEL.getCode());
      jobLevel.setIsDeprecated(0);
      jobLevel.setCreatedUserId(-1L);
    }
    orgPickOptionDao.batchInsertOrgPickOption(clonedPresetJobLevels);

  }

  @Override
  @LogAround
  public List<OrgPickOption> listPickOptionOfConfigType(long orgId, int configType) {
    return orgPickOptionDao.listOrgPickOptionByConfigType(orgId, configType);
  }

  /**
   * Steps:
   *  1) add
   *  2) deprecate
   *  3) update
   *
   * @param orgPickOptions
   */
  @Override
  @LogAround
  @Transactional(
      value = "transactionManager",
      rollbackFor = Exception.class)
  public void batchUpdateOrgPickOptions(long orgId, List<OrgPickOption> orgPickOptions, long actorUserId) {

    if (CollectionUtils.isEmpty(orgPickOptions)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    for (OrgPickOption orgPickOption: orgPickOptions) {
      orgPickOption.setOrgId(orgId);
      if (null == orgPickOption.getOrgPickOptionId()) {
        orgPickOption.setCreatedUserId(actorUserId);
      } else {
        orgPickOption.setLastModifiedUserId(actorUserId);
      }
    }

    if (!OrgPickOptionHelper.isValidSequenceOfPickOption(orgPickOptions)) {
      throw new ServiceStatusException(ServiceStatus.UO_INVALID_PICK_OPTION);
    }

    int configType = orgPickOptions.get(0).getConfigType();
    List<OrgPickOption> currOrgPickOptions =
        orgPickOptionDao.listOrgPickOptionByConfigTypeForUpdate(orgId, configType);
    for (OrgPickOption currOrgPickOption: currOrgPickOptions) {
      currOrgPickOption.setLastModifiedUserId(actorUserId);
    }

    // add orgPickOptions
    List<OrgPickOption> orgPickOptionsToAdd =
        OrgPickOptionHelper.listOrgPickOptionToAddUponUpdate(orgPickOptions);
    if (!CollectionUtils.isEmpty(orgPickOptionsToAdd)) {
      orgPickOptionDao.batchInsertOrgPickOption(orgPickOptionsToAdd);
    }

    // deprecate orgPickOptions
    List<Long> orgPickOptionsIdsToDeprecate =
        OrgPickOptionHelper.listOrgPickOptionIdsToDeprecatedUponUpdate(currOrgPickOptions, orgPickOptions);
    if (!CollectionUtils.isEmpty(orgPickOptionsIdsToDeprecate)) {
      orgPickOptionDao.batchDeprecateOrgPickOptionByPrimaryKey(
          orgId, orgPickOptionsIdsToDeprecate, actorUserId);
    }

    // update pickOptions
    List<OrgPickOption> orgPickOptionsToUpdate =
        OrgPickOptionHelper.listOrgPickOptionToUpdateUponUpdate(currOrgPickOptions, orgPickOptions);
    if (!CollectionUtils.isEmpty(orgPickOptionsToUpdate)) {
      orgPickOptionDao.batchUpdateOrgPickOption(orgPickOptions);
    }
  }

  private List<OrgPickOption> cloneListOfOrgPickOptions(List<OrgPickOption> existedOptions) {
    List<OrgPickOption> clonedOptions = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(existedOptions)) {
      clonedOptions = new ArrayList<>();
      for (int i = 0; i < existedOptions.size(); i++) {
        OrgPickOption clonedOption = new OrgPickOption();
        BeanUtils.copyProperties(existedOptions.get(i), clonedOption);
        clonedOptions.add(clonedOption);
      }
    }
    return clonedOptions;
  }
}
