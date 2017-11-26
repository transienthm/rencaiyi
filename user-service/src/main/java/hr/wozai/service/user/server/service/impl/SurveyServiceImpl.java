package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.survey.SurveyActivityDao;
import hr.wozai.service.user.server.dao.survey.SurveyConfigDao;
import hr.wozai.service.user.server.dao.survey.SurveyItemDao;
import hr.wozai.service.user.server.dao.survey.SurveyResponseDao;
import hr.wozai.service.user.server.enums.SurveyFrequency;
import hr.wozai.service.user.server.helper.SurveyHelper;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.model.survey.SurveyConfig;
import hr.wozai.service.user.server.model.survey.SurveyItem;
import hr.wozai.service.user.server.model.survey.SurveyResponse;
import hr.wozai.service.user.server.service.SurveyService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
@Service("surveyServiceImpl")
public class SurveyServiceImpl implements SurveyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyServiceImpl.class);

  @Autowired
  private SurveyItemDao surveyItemDao;

  @Autowired
  private SurveyActivityDao surveyActivityDao;

  @Autowired
  private SurveyResponseDao surveyResponseDao;

  @Autowired
  private SurveyConfigDao surveyConfigDao;

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long insertSurveyItem(SurveyItem surveyItem) {
    SurveyConfig surveyConfig = this.getSurveyConfig(surveyItem.getOrgId());
    SurveyHelper.checkSurveyItemAddParamsWithSurveyConfig(surveyItem, surveyConfig);

    return surveyItemDao.insertSurveyItem(surveyItem);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int deleteSurveyItemByPrimaryKey(long orgId, long surveyItemId, long actorUserId) {
    return surveyItemDao.deleteSurveyItemByPrimaryKey(orgId, surveyItemId, actorUserId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int updateSurveyItem(SurveyItem surveyItem) {
    SurveyConfig surveyConfig = this.getSurveyConfig(surveyItem.getOrgId());
    SurveyHelper.checkSurveyItemUpdateParamsWithSurveyConfig(surveyItem, surveyConfig);

    return surveyItemDao.updateSurveyItem(surveyItem);
  }

  @Override
  @LogAround
  public List<SurveyItem> listAvailableSurveyItemsByOrgIdAndTimestamp(
          long orgId, long timestamp, int pageNumber, int pageSize) {
    return surveyItemDao.listAvailableSurveyItemsByOrgIdAndTimestamp(orgId, timestamp, pageNumber, pageSize);
  }

  @Override
  @LogAround
  public int countSurveyItemsByOrgId(long orgId) {
    return surveyItemDao.countSurveyItemsByOrgId(orgId);
  }

  @Override
  @LogAround
  public List<SurveyItem> listSamePeriodSurveyItemsByOrgId(long orgId, long startTime, long endTime) {
    /*SurveyConfig surveyConfig = this.getSurveyConfig(orgId);
    SurveyHelper.checkStartAndEndTime(startTime, endTime, SurveyFrequency.getEnumByCode(surveyConfig.getFrequency()));*/
    return surveyItemDao.listSamePeriodSurveyItemsByOrgId(orgId, startTime, endTime);
  }

  @Override
  @LogAround
  public SurveyItem findSurveyItemByPrimaryKey(long orgId, long surveyItemId) {
    return surveyItemDao.findSurveyItemByPrimaryKey(orgId, surveyItemId);
  }

  @Override
  @LogAround
  public List<SurveyItem> listSurveyItemsByOrgIdAndItemIds(long orgId, List<Long> surveyItemIds) {
    if (CollectionUtils.isEmpty(surveyItemIds)) {
      return Collections.EMPTY_LIST;
    }
    return surveyItemDao.listSurveyItemsByOrgIdAndItemIds(orgId, surveyItemIds);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long insertSurveyConfig(SurveyConfig surveyConfig) {
    SurveyHelper.checkSurveyConfigAddParams(surveyConfig);

    return surveyConfigDao.insertSurveyConfig(surveyConfig);
  }

  @Override
  @LogAround
  public SurveyConfig getSurveyConfig(long orgId) {
    SurveyConfig surveyConfig = surveyConfigDao.findSurveyConfigByOrgId(orgId);
    if (surveyConfig == null) {
      surveyConfig = new SurveyConfig();
      surveyConfig.setOrgId(orgId);
      surveyConfig.setFrequency(SurveyFrequency.ONE_WEEK.getCode());
    }
    return surveyConfig;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long updateSurveyConfig(SurveyConfig surveyConfig) {
    SurveyHelper.checkSurveyConfigUpdateParams(surveyConfig);

    return surveyConfigDao.updateSurveyConfig(surveyConfig);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean initSurveyActivity(long orgId, SurveyActivity surveyActivity, List<Long> userIds) {
    // 判断本周起内是不是已经发过survey
    SurveyActivity latestActivity = this.getLatestSurveyActivity(orgId);
    SurveyConfig surveyConfig = this.getSurveyConfig(orgId);
    if (!SurveyHelper.checkActivityIsNotInPeriod(latestActivity, surveyConfig)) {
      return false;
    }

    List<SurveyItem> surveyItems = surveyItemDao.listAvailableSurveyItemsByOrgIdAndTimestamp(
            surveyActivity.getOrgId(), surveyActivity.getCreatedTime(), 1, Integer.MAX_VALUE);
    // 如果没有调研项, 返回
    if (CollectionUtils.isEmpty(surveyItems) || CollectionUtils.isEmpty(userIds)) {
      return false;
    }

    SurveyHelper.checkSurveyActivityAddParams(surveyActivity);
    long activityId = surveyActivityDao.insertSurveyActivity(surveyActivity);

    // 为每个用户创建多条response
    List<SurveyResponse> surveyResponses = new ArrayList<>();
    for (Long userId : userIds) {
      for (SurveyItem surveyItem : surveyItems) {
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setOrgId(surveyActivity.getOrgId());
        surveyResponse.setUserId(userId);
        surveyResponse.setSurveyActivityId(activityId);
        surveyResponse.setSurveyItemId(surveyItem.getSurveyItemId());
        surveyResponse.setSurveyItemType(surveyItem.getSurveyItemType());
        surveyResponse.setResponse(0);
        surveyResponse.setResponseDetail("");
        surveyResponse.setCreatedUserId(surveyActivity.getCreatedUserId());
        surveyResponses.add(surveyResponse);
      }
    }
    surveyResponseDao.batchInsertSurveyResponse(surveyResponses);
    return true;
  }

  private SurveyActivity getLatestSurveyActivity(long orgId) {
    List<SurveyActivity> surveyActivities = this.listSurveyActivities(orgId, 1, Integer.MAX_VALUE);
    if (CollectionUtils.isEmpty(surveyActivities)) {
      return null;
    } else {
      return surveyActivities.get(0);
    }
  }

  @Override
  @LogAround
  public SurveyActivity getSurveyActivityByOrgIdAndPrimaryKey(long orgId, long surveyActivityId) {
    return surveyActivityDao.getSurveyActivityByOrgIdAndPrimaryKey(orgId, surveyActivityId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchUpdateSurveyResponses(List<SurveyResponse> surveyResponses) {
    if (CollectionUtils.isEmpty(surveyResponses)) {
      return;
    }
    for (SurveyResponse surveyResponse : surveyResponses) {
      SurveyHelper.checkSurveyResponseUpdateParams(surveyResponse);
    }
    surveyResponseDao.batchUpdateSurveyResponse(surveyResponses);
  }

  @Override
  @LogAround
  public List<SurveyActivity> listSurveyActivities(long orgId, int pageNumber, int pageSize) {
    List<SurveyActivity> surveyActivities = surveyActivityDao.listSurveyActivityByOrgId(orgId, pageNumber, pageSize);
    return surveyActivities;
  }

  @Override
  @LogAround
  public List<SurveyActivity> listSurveyActivityByOrgIdAndStartTimeAndEndTime(long orgId, long startTime, long endTime) {
    return surveyActivityDao.listSurveyActivityByOrgIdAndStartTimeAndEndTime(orgId, startTime, endTime);
  }

  @Override
  @LogAround
  public List<SurveyResponse> listSurveyResponsesByOrgIdAndActivityIdAndUserId(long orgId, long userId, long activityId) {
    return surveyResponseDao.listSurveyResponsesByOrgIdAndActivityIdAndUserId(orgId, userId, activityId);
  }

  @Override
  @LogAround
  public int countSurveyResponseBySurveyItemId(long orgId, long surveyItemId) {
    return surveyResponseDao.countSurveyResponseBySurveyItemId(orgId, surveyItemId);
  }

  @Override
  @LogAround
  public List<SurveyResponse> listSurveyResponsesByOrgIdAndActivityIds(long orgId, List<Long> surveyActivityIds) {
    if (CollectionUtils.isEmpty(surveyActivityIds)) {
      return Collections.EMPTY_LIST;
    }
    return surveyResponseDao.listSurveyResponsesByOrgIdAndActivityIds(orgId, surveyActivityIds);
  }

  @Override
  @LogAround
  public List<SurveyResponse> searchResponsesByOrgIdAndActivityIdAndItemId(
          long orgId, long surveyActivityId, long surveyItemId, String keyword, int pageNumber, int pageSize) {
    return surveyResponseDao.searchResponsesByOrgIdAndActivityIdAndItemId(
            orgId, surveyActivityId, surveyItemId, keyword, pageNumber, pageSize);
  }
}
