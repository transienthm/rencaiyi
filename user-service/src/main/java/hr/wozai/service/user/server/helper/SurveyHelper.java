package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.server.enums.SurveyFrequency;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.model.survey.SurveyConfig;
import hr.wozai.service.user.server.model.survey.SurveyItem;
import hr.wozai.service.user.server.model.survey.SurveyResponse;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
public class SurveyHelper {
  public static void checkSurveyItemAddParamsWithSurveyConfig(SurveyItem surveyItem, SurveyConfig surveyConfig) {
    long curTime = System.currentTimeMillis();
    if (null == surveyItem
            || null == surveyItem.getOrgId()
            || null == surveyItem.getSurveyItemType()
            || null == surveyItem.getQuestion()
            || null == surveyItem.getStartTime()
            || null == surveyItem.getEndTime()
            || (surveyItem.getStartTime() < curTime || surveyItem.getEndTime() < surveyItem.getStartTime())
            || null == surveyItem.getCreatedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    if (IntegerUtils.equals(surveyItem.getSurveyItemType(), SurveyItemAttribute.SCALE_QUESTION.getCode())) {
      if (null == surveyItem.getDescription()
              || null == surveyItem.getLowLabel()
              || null == surveyItem.getHighLabel()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    } else if (IntegerUtils.equals(surveyItem.getSurveyItemType(), SurveyItemAttribute.BOOLEAN_QUESTION.getCode())) {
      if (null == surveyItem.getLowLabel()
              || null == surveyItem.getHighLabel()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    } else if (IntegerUtils.equals(surveyItem.getSurveyItemType(), SurveyItemAttribute.COMMON_QUESTION.getCode())) {
      if (null == surveyItem.getDescription()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    checkStartAndEndTime(
            surveyItem.getStartTime(), surveyItem.getEndTime(), SurveyFrequency.getEnumByCode(surveyConfig.getFrequency()));
  }

  // 检测生效时间差必须至少包含一个时间周期
  public static void checkStartAndEndTime(long startTime, long endTime, SurveyFrequency surveyFrequency) {

    long oneDay = 3600 * 24 * 1000;
    int timesOfSurvey = 0;
    for (long i = startTime; i <= endTime; i = i + oneDay) {
      if (TimeUtils.getWeekFromTimestamp(i, TimeUtils.BEIJING) == 5) {
        timesOfSurvey++;
      }
    }
    if (timesOfSurvey < surveyFrequency.getTimes()) {
      throw new ServiceStatusException(ServiceStatus.SUR_TIME_PERIOD_TOO_SHORT);
    }
  }

  public static void checkSurveyItemUpdateParamsWithSurveyConfig(SurveyItem surveyItem, SurveyConfig surveyConfig) {
    if (null == surveyItem
            || null == surveyItem.getOrgId()
            || null == surveyItem.getSurveyItemId()
            || null == surveyItem.getSurveyItemType()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    if (surveyItem.getEndTime() != null) {
      checkStartAndEndTime(
              surveyItem.getStartTime(), surveyItem.getEndTime(), SurveyFrequency.getEnumByCode(surveyConfig.getFrequency()));
    }
  }

  public static void checkSurveyConfigAddParams(SurveyConfig surveyConfig) {
    if (null == surveyConfig
            || null == surveyConfig.getOrgId()
            || (null == surveyConfig.getFrequency() || !SurveyFrequency.isValidType(surveyConfig.getFrequency()))
            || null == surveyConfig.getCreatedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkSurveyConfigUpdateParams(SurveyConfig surveyConfig) {
    if (null == surveyConfig
            || null == surveyConfig.getOrgId()
            || null == surveyConfig.getSurveyConfigId()
            || (null != surveyConfig.getFrequency() && !SurveyFrequency.isValidType(surveyConfig.getFrequency()))
            || null == surveyConfig.getLastModifiedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkSurveyActivityAddParams(SurveyActivity surveyActivity){
    if (null == surveyActivity
            || null == surveyActivity.getOrgId()
            || null == surveyActivity.getCreatedUserId()
            || null == surveyActivity.getCreatedTime()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkSurveyResponseUpdateParams(SurveyResponse surveyResponse) {
    System.out.println(surveyResponse);
    if (null == surveyResponse
            || null == surveyResponse.getOrgId()
            || null == surveyResponse.getSurveyResponseId()
            || null == surveyResponse.getSurveyItemType()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    if (IntegerUtils.equals(surveyResponse.getSurveyItemType(), SurveyItemAttribute.SCALE_QUESTION.getCode())) {
      if (null == surveyResponse.getResponse()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    } else if (IntegerUtils.equals(surveyResponse.getSurveyItemType(), SurveyItemAttribute.BOOLEAN_QUESTION.getCode())) {
      if (null == surveyResponse.getResponse()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    } else if (IntegerUtils.equals(surveyResponse.getSurveyItemType(), SurveyItemAttribute.COMMON_QUESTION.getCode())) {
      if (null == surveyResponse.getResponseDetail()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    }
  }

  public static boolean checkActivityIsNotInPeriod(SurveyActivity surveyActivity, SurveyConfig surveyConfig) {
    if (surveyActivity == null) {
      return true;
    }
    long oneDay = 3600 * 24 * 1000;
    long lastCreatedTime = surveyActivity.getCreatedTime();
    long days = (System.currentTimeMillis() - lastCreatedTime) / oneDay;
    return days >= SurveyFrequency.getEnumByCode(surveyConfig.getFrequency()).getDays().intValue();
  }
}
