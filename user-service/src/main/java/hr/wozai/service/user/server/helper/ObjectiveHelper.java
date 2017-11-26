package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.okr.enums.*;
import hr.wozai.service.user.server.model.okr.Director;
import hr.wozai.service.user.server.model.okr.KeyResult;
import hr.wozai.service.user.server.model.okr.Objective;
import hr.wozai.service.user.server.model.okr.ObjectivePeriod;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
public class ObjectiveHelper {
  public static void checkObjectivePeriodInsertParams(ObjectivePeriod objectivePeriod) {
    if (objectivePeriod == null
            || objectivePeriod.getOrgId() == null
            || (objectivePeriod.getType() == null || !OkrType.isValidType(objectivePeriod.getType()))
            || objectivePeriod.getOwnerId() == null
            || objectivePeriod.getPeriodTimeSpanId() == null
            || !PeriodTimeSpan.isValidPeriodTimeSpan(objectivePeriod.getPeriodTimeSpanId())
            || objectivePeriod.getYear() == null
            || (StringUtils.isNullOrEmpty(objectivePeriod.getName())
            || !StringUtils.isValidVarchar100(objectivePeriod.getName()))
            || objectivePeriod.getCreatedUserId() == null
            || objectivePeriod.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkObjectivePeriodUpdateParams(ObjectivePeriod objectivePeriod) {
    if (objectivePeriod == null
            || objectivePeriod.getObjectivePeriodId() == null
            || objectivePeriod.getOrgId() == null
            || objectivePeriod.getPeriodTimeSpanId() == null
            || !PeriodTimeSpan.isValidPeriodTimeSpan(objectivePeriod.getPeriodTimeSpanId())
            || objectivePeriod.getYear() == null
            || (StringUtils.isNullOrEmpty(objectivePeriod.getName())
            || !StringUtils.isValidVarchar100(objectivePeriod.getName()))
            || objectivePeriod.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkObjectiveInsertParams(Objective objective) {
    if (objective == null
            || objective.getOrgId() == null
            || (objective.getParentObjectiveId() == null || objective.getParentObjectiveId() < 0)
            || (objective.getType() == null || !OkrType.isValidType(objective.getType()))
            || objective.getOwnerId() == null
            || (StringUtils.isNullOrEmpty(objective.getContent())
            || !StringUtils.isValidVarchar255(objective.getContent()))
            || (objective.getPriority() == null || !ObjectivePriority.isValidPriority(objective.getPriority()))
            || objective.getObjectivePeriodId() == null
            || objective.getIsAutoCalc() == null
            || (objective.getIsAutoCalc() == 0 && (objective.getProgressMetricType() == null
            || objective.getStartingAmount() == null
            || objective.getGoalAmount() == null
            || objective.getCurrentAmount() == null
            || objective.getUnit() == null))
            || objective.getIsPrivate() == null
            || (objective.getRegularRemindType() == null || !RegularRemindType.isValidType(objective.getRegularRemindType()))
            || objective.getCreatedUserId() == null
            || objective.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkObjectiveUpdateParams(Objective objective) {
    if (objective == null
            || objective.getObjectiveId() == null
            || objective.getOrgId() == null
            || (null != objective.getContent() && (StringUtils.isNullOrEmpty(objective.getContent())
            || !StringUtils.isValidVarchar255(objective.getContent())))
            || (objective.getPriority() != null && !ObjectivePriority.isValidPriority(objective.getPriority()))
            || (objective.getRegularRemindType() != null && !RegularRemindType.isValidType(objective.getRegularRemindType()))
            || objective.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkDirectorInsertParams(Director director) {
    if (director == null
            || director.getOrgId() == null
            || director.getUserId() == null
            || director.getCreatedUserId() == null
            || director.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkKeyResultInsertParams(KeyResult keyResult) {
    if (keyResult == null
            || keyResult.getOrgId() == null
            || (StringUtils.isNullOrEmpty(keyResult.getContent())
            || !StringUtils.isValidVarchar255(keyResult.getContent()))
            || keyResult.getObjectiveId() == null
            || (keyResult.getPriority() == null || !ObjectivePriority.isValidPriority(keyResult.getPriority()))
            || (keyResult.getProgressMetricType() == null || !ProgressMetric.isValidType(keyResult.getProgressMetricType()))
            || keyResult.getStartingAmount() == null
            || keyResult.getGoalAmount() == null
            || keyResult.getCurrentAmount() == null
            || keyResult.getUnit() == null
            || keyResult.getCreatedUserId() == null
            || keyResult.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkKeyResultUpdateParams(KeyResult keyResult) {
    if (keyResult == null
            || keyResult.getKeyResultId() == null
            || keyResult.getOrgId() == null
            || (null != keyResult.getContent() && (StringUtils.isNullOrEmpty(keyResult.getContent())
            || !StringUtils.isValidVarchar255(keyResult.getContent())))
            || (keyResult.getPriority() != null && !ObjectivePriority.isValidPriority(keyResult.getPriority()))
            || keyResult.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }
}
