package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.model.survey.SurveyConfig;
import hr.wozai.service.user.server.model.survey.SurveyItem;
import hr.wozai.service.user.server.model.survey.SurveyResponse;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
public interface SurveyService {
  long insertSurveyItem(SurveyItem surveyItem);

  int deleteSurveyItemByPrimaryKey(long orgId, long surveyItemId, long actorUserId);

  int updateSurveyItem(SurveyItem surveyItem);

  /**
   * 1. 分页列出全公司的item,timestamp传0
   * 2. 列出时间周期内的所有item, 初始化activity用
   * @param orgId
   * @param timestamp
   * @param pageNumber
   * @param pageSize
   * @return
   */
  List<SurveyItem> listAvailableSurveyItemsByOrgIdAndTimestamp(
          long orgId, long timestamp, int pageNumber, int pageSize);

  /**
   * 获取总数,分页使用
   * @param orgId
   * @return
   */
  int countSurveyItemsByOrgId(long orgId);

  /**
   * 列出统一时间跨度内的item
   * @param orgId
   * @param startTime
   * @param endTime
   * @return
   */
  List<SurveyItem> listSamePeriodSurveyItemsByOrgId(long orgId, long startTime, long endTime);

  SurveyItem findSurveyItemByPrimaryKey(long orgId, long surveyItemId);

  List<SurveyItem> listSurveyItemsByOrgIdAndItemIds(long orgId, List<Long> surveyItemIds);

  //########################################################################

  long insertSurveyConfig(SurveyConfig surveyConfig);

  SurveyConfig getSurveyConfig(long orgId);

  /**
   * 更新频率
   * @param surveyConfig
   * @return
   */
  long updateSurveyConfig(SurveyConfig surveyConfig);

  //########################################################################

  /**
   * 初始化一个survey活动
   * 1) 为每个人生成一个activity
   * 2) 为每个activity生成item
   */
  boolean initSurveyActivity(long orgId, SurveyActivity surveyActivity, List<Long> userIds);

  SurveyActivity getSurveyActivityByOrgIdAndPrimaryKey(long orgId, long surveyActivityId);

  /**
   * 提交survey报告
   * @param surveyResponses
   */
  void batchUpdateSurveyResponses(List<SurveyResponse> surveyResponses);

  /**
   * 分页列出所有调研活动
   * @param orgId
   * @param pageNumber
   * @param pageSize
   * @return
   */
  List<SurveyActivity> listSurveyActivities(long orgId, int pageNumber, int pageSize);

  /**
   * 列出一定时间范围内的调研活动
   * @param orgId
   * @param startTime
   * @param endTime
   * @return
   */
  List<SurveyActivity> listSurveyActivityByOrgIdAndStartTimeAndEndTime(
          long orgId, long startTime, long endTime);

  List<SurveyResponse> listSurveyResponsesByOrgIdAndActivityIdAndUserId(long orgId, long userId, long activityId);

  int countSurveyResponseBySurveyItemId(long orgId, long surveyItemId);

  /**
   * 列出一系列调研活动下的所有response
   * @param orgId
   * @param surveyActivityIds
   * @return
   */
  List<SurveyResponse> listSurveyResponsesByOrgIdAndActivityIds(
          long orgId, List<Long> surveyActivityIds);

  /**
   * 根据关键字列出一个调研活动下某个问题的所有回答
   * @param orgId
   * @param surveyActivityId
   * @param surveyItemId
   * @param keyword
   * @param pageNumber
   *@param pageSize @return
   */
  List<SurveyResponse> searchResponsesByOrgIdAndActivityIdAndItemId(
          long orgId, long surveyActivityId, long surveyItemId, String keyword, int pageNumber, int pageSize);
}
