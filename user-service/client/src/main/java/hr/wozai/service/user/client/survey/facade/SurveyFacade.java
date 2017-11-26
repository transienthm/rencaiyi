package hr.wozai.service.user.client.survey.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.survey.dto.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
@ThriftService
public interface SurveyFacade {
  @ThriftMethod
  LongDTO addSurveyItem(long orgId, SurveyItemDTO surveyItemDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO deleteSurveyItem(long orgId, long surveyItemId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateSurveyItem(long orgId, SurveyItemDTO surveyItemDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  SurveyItemListDTO listSurveyItemsByOrgId(long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  SurveyItemListDTO listSurveyItemsByStartAndEndTime(
          long orgId, long startTime, long endTime, long actorUserId, long adminUserId);

  // ########SurveyConfig#######

  @ThriftMethod
  SurveyConfigDTO getSurveyConfig(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  LongDTO updateSurveyConfig(long orgId, SurveyConfigDTO surveyConfigDTO, long actorUserId, long adminUserId);

  // ########SurveyActivity && SurveyResponse#######

  @ThriftMethod
  SurveyActivityDTO getInPeriodAndUnSubmitedSurveyActivity(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO submitSurveyActivityByUser(long orgId, SurveyActivityDTO surveyActivityDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  SurveyActivityListDTO listSurveyActivitiesByOrgId(
          long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  /**
   * 图标部分:列出一个activity下的每个item的图表详情
   * @param orgId
   * @param surveyActivityId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  NewSurveyActivityDTO listSurveyResponsesByOrgIdAndActivityId(
          long orgId, long surveyActivityId, long actorUserId, long adminUserId);

  /**
   * 图标部分:列出一个activity下的特定item的图表详情,可根据关键字查询
   * @param orgId
   * @param surveyActivityId
   * @param surveyItemId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  NewSurveyActivityDTO listSurveyResponsesByOrgIdAndActivityIdAndItemId(
          long orgId, long surveyActivityId, long surveyItemId, String keyword, int pageNumber, int pageSize,
          long actorUserId, long adminUserId);

  /**
   * 列出时间范围内每个item的历史变化
   * @param orgId
   * @param startTime
   * @param endTime
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  SurveyItemHistoryListDTO listSurveyItemHistorysByOrgIdAndTimeRange(
          long orgId, long startTime, long endTime, long actorUserId, long adminUserId);
}
