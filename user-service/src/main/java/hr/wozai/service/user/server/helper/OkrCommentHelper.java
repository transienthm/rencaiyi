// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.okr.enums.ObjectivePriority;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.server.enums.OkrLogAttribute;
import hr.wozai.service.user.server.model.okr.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import hr.wozai.service.user.server.model.okr.OkrComment;
import hr.wozai.service.user.server.model.okr.OkrUpdateLog;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-09-09
 */
public class OkrCommentHelper {

  public static void isAcceptableAddOkrCommentRequest(OkrComment okrComment) {
    if (null == okrComment
            || null == okrComment.getOrgId()
            || null == okrComment.getObjectiveId()
            || null == okrComment.getUserId()
            || null == okrComment.getContent()
            || null == okrComment.getCreatedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void isAcceptableUpdateOkrCommentRequest(OkrComment okrComment) {
    if (null == okrComment
            || null == okrComment.getOkrCommentId()
            || null == okrComment.getOrgId()
            || null == okrComment.getLastModifiedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void isAcceptableAddOkrUpdateLogRequest(OkrUpdateLog okrUpdateLog) {
    if (null == okrUpdateLog
            || null == okrUpdateLog.getOrgId()
            || null == okrUpdateLog.getOkrCommentId()
            || (null == okrUpdateLog.getAttribute() || !StringUtils.isValidVarchar100(okrUpdateLog.getAttribute()))
            || null == okrUpdateLog.getBeforeValue()
            || null == okrUpdateLog.getAfterValue()
            || null == okrUpdateLog.getCreatedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static List<OkrUpdateLog> generateOkrUpdateLogsWhenUpdateObjective(Objective before, Objective after) {
    List<OkrUpdateLog> okrUpdateLogs = new ArrayList<>();
    OkrUpdateLog okrUpdateLog;

    //目标内容
    if (!before.getContent().equals(after.getContent())) {
      okrUpdateLog = generateOkrUpdateLog(
              before.getOrgId(), "", OkrLogAttribute.OBJ_CONTENT.getDesc(),
              before.getContent(), after.getContent(), after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }

    // 上级目标
    if (!LongUtils.equals(before.getParentObjectiveId(), after.getParentObjectiveId())) {
      okrUpdateLog = OkrCommentHelper.generateOkrUpdateLog(before.getOrgId(), "",
              OkrLogAttribute.OBJ_PARENT.getDesc(),
              String.valueOf(before.getParentObjectiveId()),
              String.valueOf(after.getParentObjectiveId()),
              after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }

    // 可见性
    if (!IntegerUtils.equals(before.getIsPrivate(), after.getIsPrivate())) {
      okrUpdateLog = generateOkrUpdateLog(
              before.getOrgId(), "", OkrLogAttribute.OBJ_VISIBILITY.getDesc(),
              getNameByObjectiveTypeAndIsPrivate(before.getType(), before.getIsPrivate()),
              getNameByObjectiveTypeAndIsPrivate(after.getType(), after.getIsPrivate()),
              after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }
    // 截止日
    if (!LongUtils.equals(before.getDeadline(), after.getDeadline())) {
      okrUpdateLog = generateOkrUpdateLog(before.getOrgId(), "", OkrLogAttribute.OBJ_DEADLINE.getDesc(),
              getNameForDeadline(before.getDeadline()),
              getNameForDeadline(after.getDeadline()),
              after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }
    // 优先级
    if (!IntegerUtils.equals(before.getPriority(), after.getPriority())) {
      okrUpdateLog = generateOkrUpdateLog(before.getOrgId(), "", OkrLogAttribute.OBJ_PRIORITY.getDesc(),
              ObjectivePriority.getEnumByCode(before.getPriority()).getName(),
              ObjectivePriority.getEnumByCode(after.getPriority()).getName(),
              after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }
    // 自动计算
    String beforeValue;
    String afterValue;
    if (before.getIsAutoCalc() == 0 && after.getIsAutoCalc() == 1) {
      if (before.getProgressMetricType() == 1) {
        beforeValue = before.getCurrentAmount().stripTrailingZeros().toPlainString() + before.getUnit();
      } else {
        beforeValue = before.getCurrentAmount().stripTrailingZeros().toPlainString() + "/"
                + before.getGoalAmount().stripTrailingZeros().toPlainString()
                + " " + before.getUnit() + "（初始值："
                + before.getStartingAmount().stripTrailingZeros().toPlainString() + "）";
      }
      afterValue = "自动计算";
      okrUpdateLog = generateOkrUpdateLog(
              before.getOrgId(), "", OkrLogAttribute.OBJ_PROGRESS.getDesc(),
              beforeValue, afterValue, after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    } else if (before.getIsAutoCalc() == 0 && after.getIsAutoCalc() == 0) {
      BigDecimal oldStartAmount = before.getStartingAmount();
      BigDecimal oldGoalAmount = before.getGoalAmount();
      BigDecimal oldCurrentAmount = before.getCurrentAmount();
      BigDecimal newStartAmount = after.getStartingAmount();
      BigDecimal newGoalAmount = after.getGoalAmount();
      BigDecimal newCurrentAmount = after.getCurrentAmount();
      if (oldStartAmount.compareTo(newStartAmount) != 0 || oldGoalAmount.compareTo(newGoalAmount) != 0
              || oldCurrentAmount.compareTo(newCurrentAmount) != 0) {
        if (before.getProgressMetricType() == 1) {
          beforeValue = oldCurrentAmount.stripTrailingZeros().toPlainString() + before.getUnit();
        } else {
          beforeValue = oldCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                  + oldGoalAmount.stripTrailingZeros().toPlainString()
                  + " " + before.getUnit() + "（初始值："
                  + oldStartAmount.stripTrailingZeros().toPlainString() + "）";
        }

        if (after.getProgressMetricType() == 1) {
          afterValue = newCurrentAmount.stripTrailingZeros().toPlainString() + after.getUnit();
        } else {
          afterValue = newCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                  + newGoalAmount.stripTrailingZeros().toPlainString()
                  + " " + after.getUnit() + "（初始值："
                  + newStartAmount.stripTrailingZeros().toPlainString() + "）";
        }
        okrUpdateLog = generateOkrUpdateLog(
                before.getOrgId(), "", OkrLogAttribute.OBJ_PROGRESS.getDesc(),
                beforeValue, afterValue, after.getLastModifiedUserId());
        okrUpdateLogs.add(okrUpdateLog);
      }
    } else if (before.getIsAutoCalc() == 1 && after.getIsAutoCalc() == 0) {
      beforeValue = "自动计算";
      if (after.getProgressMetricType() == 1) {
        afterValue = after.getCurrentAmount().stripTrailingZeros().toPlainString() + after.getUnit();
      } else {
        afterValue = after.getCurrentAmount().stripTrailingZeros().toPlainString() + "/"
                + after.getGoalAmount().stripTrailingZeros().toPlainString()
                + " " + after.getUnit() + "（初始值："
                + after.getStartingAmount().stripTrailingZeros().toPlainString() + "）";
      }
      okrUpdateLog = generateOkrUpdateLog(
              before.getOrgId(), "", OkrLogAttribute.OBJ_PROGRESS.getDesc(),
              beforeValue, afterValue, after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    } else {

    }

    return okrUpdateLogs;
  }

  public static OkrUpdateLog generateOkrUpdateLog(
          long orgId, String title, String attribute, String beforeValue, String afterValue, long createdUserId) {
    OkrUpdateLog okrUpdateLog = new OkrUpdateLog();
    okrUpdateLog.setOrgId(orgId);
    okrUpdateLog.setAttribute(attribute);
    okrUpdateLog.setBeforeValue(beforeValue);
    okrUpdateLog.setAfterValue(afterValue);
    okrUpdateLog.setCreatedUserId(createdUserId);

    return okrUpdateLog;
  }

  public static String getNameByObjectiveTypeAndIsPrivate(int okrType, int isPrivate) {
    if (isPrivate == 0) {
      return "公开";
    } else if (isPrivate == 1 && okrType == OkrType.ORG.getCode()) {
      return "仅公司管理员可见";
    } else if (isPrivate == 1 && okrType == OkrType.TEAM.getCode()) {
      return "仅团队可见";
    } else {
      return "仅自己可见";
    }
  }

  public static String getNameForDeadline(Long deadline) {
    if (deadline == null || deadline == 0L) {
      return "无";
    } else {
      return TimeUtils.formatDateWithTimeZone(deadline, TimeUtils.BEIJING);
    }
  }

  public static List<OkrUpdateLog> generateOkrUpdateLogsWhenUpdateKeyresult(KeyResult before, KeyResult after) {
    List<OkrUpdateLog> okrUpdateLogs = new ArrayList<>();
    OkrUpdateLog okrUpdateLog;

    // 关键结果内容
    if (!before.getContent().equals(after.getContent())) {
      okrUpdateLog = generateOkrUpdateLog(before.getOrgId(), "", OkrLogAttribute.KR_CONTENT.getDesc(),
              before.getContent(), after.getContent(), after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }
    // 优先级
    if (!IntegerUtils.equals(before.getPriority(), after.getPriority())) {
      okrUpdateLog = generateOkrUpdateLog(before.getOrgId(), "", OkrLogAttribute.KR_PRIORITY.getDesc(),
              ObjectivePriority.getEnumByCode(before.getPriority()).getName(),
              ObjectivePriority.getEnumByCode(after.getPriority()).getName(),
              after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }

    // 截止日
    if (!LongUtils.equals(before.getDeadline(), after.getDeadline())) {
      okrUpdateLog = generateOkrUpdateLog(before.getOrgId(), "", OkrLogAttribute.KR_DEADLINE.getDesc(),
              getNameForDeadline(before.getDeadline()),
              getNameForDeadline(after.getDeadline()),
              after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }
    // 自动计算
    String beforeValue;
    String afterValue;

    BigDecimal oldStartAmount = before.getStartingAmount();
    BigDecimal oldGoalAmount = before.getGoalAmount();
    BigDecimal oldCurrentAmount = before.getCurrentAmount();
    BigDecimal newStartAmount = after.getStartingAmount();
    BigDecimal newGoalAmount = after.getGoalAmount();
    BigDecimal newCurrentAmount = after.getCurrentAmount();
    if (oldStartAmount.compareTo(newStartAmount) != 0 || oldGoalAmount.compareTo(newGoalAmount) != 0
            || oldCurrentAmount.compareTo(newCurrentAmount) != 0) {
      if (before.getProgressMetricType() == 1) {
        beforeValue = oldCurrentAmount.stripTrailingZeros().toPlainString() + before.getUnit();
      } else {
        beforeValue = oldCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                + oldGoalAmount.stripTrailingZeros().toPlainString()
                + " " + before.getUnit() + "（初始值："
                + oldStartAmount.stripTrailingZeros().toPlainString() + "）";
      }

      if (after.getProgressMetricType() == 1) {
        afterValue = newCurrentAmount.stripTrailingZeros().toPlainString() + after.getUnit();
      } else {
        afterValue = newCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                + newGoalAmount.stripTrailingZeros().toPlainString()
                + " " + after.getUnit() + "（初始值："
                + newStartAmount.stripTrailingZeros().toPlainString() + "）";
      }
      okrUpdateLog = generateOkrUpdateLog(
              before.getOrgId(), "", OkrLogAttribute.KR_PROGRESS.getDesc(),
              beforeValue, afterValue, after.getLastModifiedUserId());
      okrUpdateLogs.add(okrUpdateLog);
    }


    return okrUpdateLogs;
  }
}
