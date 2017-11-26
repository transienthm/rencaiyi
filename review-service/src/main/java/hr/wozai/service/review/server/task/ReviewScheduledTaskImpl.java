/**
 * Copyright (c) 2016 WOZAI Inc.
 * All rights reserved.
 *
 * "Review Scheduled Task" version 1.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *    * Neither the name of Wozai Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ---
 * @Author:  Zich Liu
 * @Version: 1.0
 * @Created: 2016-09-26 12:20:00
 * @E-mail:  liuzhenfu@sqian.com
 *
 * ---
 * Description:
 *   Review-Scheduled-Task.
 */

package hr.wozai.service.review.server.task;

import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.thirdparty.client.bean.BatchEmail;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.model.ReviewInvitation;

import hr.wozai.service.review.server.utils.ReviewEmailUtils;
import hr.wozai.service.review.server.utils.ReviewMessageUtils;

import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import org.springframework.util.CollectionUtils;

/**
 * @Author: Zich Liu
 * @Version: 1.0
 * @Created: 2016-09-26 12:20:00
 */
@Component
public class ReviewScheduledTaskImpl {

  //*******************************************************************************************************************

  // 处理时间的相关枚举类型
  private enum Dater {
    // "指定时间"的{前两天/当天/后一天}
    PREVIOUS_TWO_DAYS(-2), PREVIOUS_ONE_DAY(-1), CURRENT(0), NEXT_ONE_DAY(1);

    // "-2" 表示 "前两天"
    // "-1" 表示 "前一天"
    // "0"  表示 "当天"
    // "1"  表示 "后一天"
    private int dateType;

    // 构造函数, 用于初始化枚举类型内的同类型常量
    private Dater(int dateType) {
      this.dateType = dateType;
    } // Class-Enum-Function-End: Dater()

    // 根据日期类型, 获取指定时间的{前两天/当天/后一天}时间
    public static long getDate(Dater dater, long dateStamp) {
      Date date = new Date(dateStamp);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(Calendar.DAY_OF_MONTH, dater.dateType);
      date = calendar.getTime();
      return date.getTime();
    } // Class-Enum-Function-End: getDate()
  } // Class-Enum-End: Dater

  //*******************************************************************************************************************

  // 用于标记"反馈活动"进行阶段的枚举类型
  private enum Period {
    NONE, // 不表示任何阶段, 一个默认值
    START, // 表示"开始"阶段, 暂时无用
    SELF, // 表示"自我评价"阶段
    SELF_PREVIOUS_TWO_DAYS, // 表示"自我评价"阶段截止日的前两天
    SELF_PREVIOUS_ONE_DAY, // 表示"自我评价"阶段截止日的前一天
    SELF_NEXT_ONE_DAY, // 表示"自我评价"阶段截止日的后一天
    PEER, // 表示"同事互评"阶段
    PEER_PREVIOUS_TWO_DAYS, // 表示"同事互评"阶段截止日的前两天
    PEER_PREVIOUS_ONE_DAY, // 表示"同事互评"阶段截止日的前一天
    PEER_NEXT_ONE_DAY, // 表示"同事互评"阶段截止日的后一天
    PUBLIC, // 表示"最终评价"阶段
    PUBLIC_PREVIOUS_TWO_DAYS, // 表示"最终评价"阶段截止日的前两天
    PUBLIC_PREVIOUS_ONE_DAY, // 表示"最终评价"阶段截止日的前一天
    PUBLIC_NEXT_ONE_DAY, // 表示"最终评价"阶段截止日的后一天
    FINISH; // 表示"流程截止"阶段

    public static Period getEvaluatePeriod(ReviewTemplate reviewTemplate) {
      // 设置当前"定时任务"执行时的当前时间
      long currentTime = System.currentTimeMillis();

      // 获取"自我评价"阶段的截止日期时间戳
      long selfDeadline = reviewTemplate.getSelfReviewDeadline();
      // 获取"自我评价"阶段的截止日期前两天时间戳
      long selfDeadlinePreviousTwoDays = Dater.getDate(Dater.PREVIOUS_TWO_DAYS, selfDeadline);
      // 当前时间在"自我评价"阶段的截止日期前两天之前
      if (currentTime <= selfDeadlinePreviousTwoDays) {
        return Period.SELF;
      }
      // 获取"自我评价"阶段的截止日期前一天时间戳
      long selfDeadlinePreviousOneDay = Dater.getDate(Dater.PREVIOUS_ONE_DAY, selfDeadline);
      // 当前时间为"自我评价"阶段的截止日期前两天
      if (currentTime > selfDeadlinePreviousTwoDays && currentTime <= selfDeadlinePreviousOneDay) {
        return Period.SELF_PREVIOUS_TWO_DAYS;
      }
      // 当前时间为"自我评价"阶段的截止日期前一天
      if (currentTime > selfDeadlinePreviousOneDay && currentTime <= selfDeadline) {
        return Period.SELF_PREVIOUS_ONE_DAY;
      }
      // 获取"自我评价"阶段的截止日期后一天时间戳
      long selfDeadlineNextOneDay = Dater.getDate(Dater.NEXT_ONE_DAY, selfDeadline);
      // 当前时间为"自我评价"阶段的截止日期后一天
      if (currentTime > selfDeadline && currentTime <= selfDeadlineNextOneDay) {
        return Period.SELF_NEXT_ONE_DAY;
      }

      // 获取"同事互评"阶段的截止日期时间戳
      long peerDeadline = reviewTemplate.getPeerReviewDeadline();
      // 获取"同事互评"阶段的截止日期前两天时间戳
      long peerDeadlinePreviousTwoDays = Dater.getDate(Dater.PREVIOUS_TWO_DAYS, peerDeadline);
      // 当前时间在"自我评价"阶段截止日后一天与"同事互评"阶段截止日前两天之间
      if (currentTime > selfDeadlineNextOneDay && currentTime <= peerDeadlinePreviousTwoDays) {
        return Period.PEER;
      }
      // 获取"同事互评"阶段的截止日期前一天时间戳
      long peerDeadlinePreviousOneDay = Dater.getDate(Dater.PREVIOUS_ONE_DAY, peerDeadline);
      // 当前时间为"同事互评"阶段的截止日期前两天
      if (currentTime > peerDeadlinePreviousTwoDays && currentTime <= peerDeadlinePreviousOneDay) {
        return Period.PEER_PREVIOUS_TWO_DAYS;
      }
      // 当前时间为"同事互评"阶段的截止日期前一天
      if (currentTime > peerDeadlinePreviousOneDay && currentTime <= peerDeadline) {
        return Period.PEER_PREVIOUS_ONE_DAY;
      }
      // 获取"同事互评"阶段的截止日期后一天时间戳
      long peerDeadlineNextOneDay = Dater.getDate(Dater.NEXT_ONE_DAY, peerDeadline);
      // 当前时间为"同事互评"阶段的截止日期后一天
      if (currentTime > peerDeadline && currentTime <= peerDeadlineNextOneDay) {
        return Period.PEER_NEXT_ONE_DAY;
      }

      // 获取"最终评价"阶段的截止日期时间戳
      long publicDeadline = reviewTemplate.getPublicDeadline();
      // 获取"最终评价"阶段的截止日期前两天时间戳
      long publicDeadlinePreviousTwoDays = Dater.getDate(Dater.PREVIOUS_TWO_DAYS, publicDeadline);
      // 当前时间在"同事互评"阶段截止日后一天与"最终评价"阶段截止日前两天之间
      if (currentTime > peerDeadlineNextOneDay && currentTime <= publicDeadlinePreviousTwoDays) {
        return Period.PUBLIC;
      }
      // 获取"最终评价"阶段的截止日期前一天时间戳
      long publicDeadlinePreviousOneDay = Dater.getDate(Dater.PREVIOUS_ONE_DAY, publicDeadline);
      // 当前时间为"最终评价"阶段的截止日期前两天
      if (currentTime > publicDeadlinePreviousTwoDays && currentTime <= publicDeadlinePreviousOneDay) {
        return Period.PUBLIC_PREVIOUS_TWO_DAYS;
      }
      // 当前时间为"最终评价"阶段的截止日期前一天
      if (currentTime > publicDeadlinePreviousOneDay && currentTime <= publicDeadline) {
        return Period.PUBLIC_PREVIOUS_ONE_DAY;
      }
      // 获取"最终评价"阶段的截止日期后一天时间戳
      long publicDeadlineNextOneDay = Dater.getDate(Dater.NEXT_ONE_DAY, publicDeadline);
      // 当前时间为"最终评价"阶段的截止日期后一天
      if (currentTime > publicDeadline && currentTime <= publicDeadlineNextOneDay) {
        return Period.PUBLIC_NEXT_ONE_DAY;
      }

      // 当前时间在"最终评价"阶段的截止日期后一天之后
      if (currentTime > publicDeadlineNextOneDay) {
        return Period.FINISH;
      }

      // 返回默认值
      return Period.NONE;
    } // Class-Enum-Function-End: getEvaluatePeriod()
  } // Class-Enum-End: Period

  //*******************************************************************************************************************

  // 全局的"日志记录器"
  private Logger logger = LoggerFactory.getLogger(ReviewScheduledTaskImpl.class);

  // 从 Spring 容器自动注入的一些成员变量
  @Autowired
  private ReviewEmailUtils reviewEmailUtils;
  @Autowired
  private ReviewMessageUtils reviewMessageUtils;
  @Autowired
  private ReviewCommentService reviewCommentService;
  @Autowired
  private ReviewTemplateService reviewTemplateService;
  @Autowired
  private ReviewActivityService reviewActivityService;
  @Autowired
  private ReviewInvitationService reviewInvitationService;

  // 全局的用户 ID
  private long actorUserID = -1;
  private long adminUserID = -1;

  //*******************************************************************************************************************

  // 针对"自我评价"相关情况对"发出被评价"人员进行邮件和消息的通知
  @LogAround
  private void noticeActivitySelfEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"当前活动"对应的"发出被评价"的人员
    long revieweeID = reviewActivity.getRevieweeId();
    // 获取"当前活动"对应的活动 ID
    long activityID = reviewActivity.getActivityId();
    // 向"当前活动"对应的"发出被评价"的人员发送"通知邮件"
    this.reviewEmailUtils.sendActivityExpireSoonEmailForSelf(
            orgID, templateID, activityID, revieweeID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"发出被评价"的人员发送"通知消息"
    this.reviewMessageUtils.sendActivityExpireSoonMessageForSelf(
            orgID, reviewActivity, revieweeID
    );
  } // Class-Function-End: noticeActivitySelfEvaluatePreviousTwoDays()

  // 处理"自我评价"阶段截止日的前两天
  @LogAround
  private void processSelfEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    if (reviewActivity.getIsSubmitted() == 0) {
      // 当前设定为如果自评还未提交，则提醒自评人员填写评价
      this.noticeActivitySelfEvaluatePreviousTwoDays(
              orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
      );
    }
  } // Class-Function-End: processSelfEvaluatePreviousTwoDays()

  //*******************************************************************************************************************

  // 针对"同事互评"相关情况对"受邀写评价"人员进行邮件和消息的通知
  @LogAround
  private void noticeInvitationSelfEvaluateNextOneDay(
          long orgID,
          long templateID,
          long revieweeID,
          ReviewInvitation reviewInvitation,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"受邀写评价"人员的 ID
    long reviewerID = reviewInvitation.getReviewerId();
    // 获取"受邀写评价"人员的 Invitation ID
    long invitationID = reviewInvitation.getInvitationId();
    // 向"当前活动"对应的"受邀写评价"的人员发送"通知邮件"
    this.reviewEmailUtils.sendInvitationBeginEmailForPeer(
            orgID, templateID, invitationID, revieweeID, reviewerID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"受邀写评价"的人员发送"通知消息"
    this.reviewMessageUtils.sendInvitationBeginMessageForPeer(
            orgID, templateID, invitationID, revieweeID, reviewerID
    );
  } // Class-Function-End: noticeInvitationSelfEvaluateNextOneDay()

  // 处理"自我评价"阶段截止日的后一天
  @LogAround
  private void processSelfEvaluateNextOneDay(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    if (reviewActivity.getIsSubmitted() == 1) {
      // 获取"当前活动"对应的"发出被评价"的人员
      long revieweeID = reviewActivity.getRevieweeId();
      // 获取"当前活动"对应的"受邀写评价"的人员列表
      List<ReviewInvitation> reviewInvitations = this.reviewInvitationService
              .listAllReviewInvitationByTemplateIdAndRevieweeId(orgID, templateID, revieweeID);
      // 当前设定为如果自评提交完毕，则会提醒被邀请互评人员填写评价，不包括主管
      for (ReviewInvitation reviewInvitation : reviewInvitations) {
        // 过滤掉主管
        if (reviewInvitation.getIsManager() == 1) {
          continue;
        }
        // 过滤掉已提交同事
        if (reviewInvitation.getIsSubmitted() == 1) {
          continue;
        }
        this.noticeInvitationSelfEvaluateNextOneDay(
                orgID, templateID, revieweeID, reviewInvitation, mapEmailTemplateIDToBatchEmails
        );
      }
    }
  } // Class-Function-End: processSelfEvaluateNextOneDay()

  //*******************************************************************************************************************

  // 针对"自我评价"相关情况对"发出被评价"人员进行邮件和消息的通知
  @LogAround
  private void noticeActivityPeerEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"当前活动"对应的"发出被评价"的人员
    long revieweeID = reviewActivity.getRevieweeId();
    // 获取"当前活动"对应的活动 ID
    long activityID = reviewActivity.getActivityId();
    // 向"当前活动"对应的"发出被评价"的人员发送"通知邮件"
    this.reviewEmailUtils.sendActivityHasExpiredEmailForSelf(
            orgID, templateID, activityID, revieweeID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"发出被评价"的人员发送"通知消息"
    this.reviewMessageUtils.sendActivityHasExpiredMessageForSelf(
            orgID, reviewActivity, revieweeID
    );
  } // Class-Function-End: noticeActivityPeerEvaluatePreviousTwoDays()

  // 针对"同事互评"相关情况对"受邀写评价"人员进行邮件和消息的通知
  @LogAround
  private void noticeInvitationPeerEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          long revieweeID,
          ReviewInvitation reviewInvitation,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"受邀写评价"人员的 ID
    long reviewerID = reviewInvitation.getReviewerId();
    // 获取"受邀写评价"人员的 Invitation ID
    long invitationID = reviewInvitation.getInvitationId();
    // 向"当前活动"对应的"受邀写评价"的人员发送"通知邮件"
    this.reviewEmailUtils.sendInvitationExpireSoonEmailForPeer(
            orgID, templateID, invitationID, reviewerID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"受邀写评价"的人员发送"通知消息"
    this.reviewMessageUtils.sendInvitationExpireSoonMessageForPeer(
            orgID, templateID, invitationID, revieweeID, reviewerID, this.actorUserID, this.adminUserID
    );
  } // Class-Function-End: noticeInvitationPeerEvaluatePreviousTwoDays()

  // 处理"同事互评"阶段截止日的前两天
  @LogAround
  private void processPeerEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    if (reviewActivity.getIsSubmitted() == 0) {
      // 当前设定为如果自评还未提交，则提醒自评人员填写评价
      this.noticeActivityPeerEvaluatePreviousTwoDays(
              orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
      );
    } else {
      // 获取"当前活动"对应的"发出被评价"的人员
      long revieweeID = reviewActivity.getRevieweeId();
      // 获取"当前活动"对应的"受邀写评价"的人员列表
      List<ReviewInvitation> reviewInvitations = this.reviewInvitationService
              .listAllReviewInvitationByTemplateIdAndRevieweeId(orgID, templateID, revieweeID);
      // 当前设定为如果自评提交完毕，则会提醒"未提交"被邀请互评人员填写评价，不包括主管
      for (ReviewInvitation reviewInvitation : reviewInvitations) {
        // 过滤掉主管
        if (reviewInvitation.getIsManager() == 1) {
          continue;
        }
        // 过滤掉已提交同事
        if (reviewInvitation.getIsSubmitted() == 1) {
          continue;
        }
        this.noticeInvitationPeerEvaluatePreviousTwoDays(
                orgID, templateID, revieweeID, reviewInvitation, mapEmailTemplateIDToBatchEmails
        );
      }
    }
  } // Class-Function-End: processPeerEvaluatePreviousTwoDays()

  //*******************************************************************************************************************

  // 设置 Activity 为 "取消"状态
  @LogAround
  private void setActivityCanceled(ReviewActivity reviewActivity) {
    // 获取组织 ID
    long orgID = reviewActivity.getOrgId();
    // 获取模板 ID
    long templateID = reviewActivity.getTemplateId();
    // 获取"被评价"人员 ID
    long revieweeID = reviewActivity.getRevieweeId();
    // 设置数据库中相应的 Activity 状态为"已取消"
    // (即设置 template_activity 表中 is_canceled 字段值为 1)
    reviewActivity.setIsCanceled(1);
    reviewActivity.setLastModifiedUserId(revieweeID);
    this.reviewActivityService.updateReviewActivity(reviewActivity);
    // 删除数据库中与该"发出被评价"人员相关的评价信息
    // (设置 template_comment 表中 is_delete 字段值为 1)
    this.reviewCommentService.deleteReviewCommentByReviewer(
            orgID, templateID, revieweeID, revieweeID, revieweeID
    );
  } // Class-Function-End: setActivityCanceled()

  // 针对"自我评价"相关情况对"发出被评价"人员进行邮件和消息的"取消"通知
  @LogAround
  private void noticeActivityPeerEvaluateNextOneDay(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // TODO: 需要在流程结束后, 将"未提交" Activity 取消的话, 可将此注释去掉即可
    // this.setActivityCanceled(reviewActivity);

    // 获取"当前活动"对应的"发出被评价"的人员
    long revieweeID = reviewActivity.getRevieweeId();

    // 向"当前活动"对应的"发出被评价"的人员发送"取消通知邮件"
    this.reviewEmailUtils.sendActivityCancelEmailForSelf(
            orgID, templateID, revieweeID, this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"发出被评价"的人员发送"取消通知消息"
    this.reviewMessageUtils.sendActivityCancelMessageForSelf(
            orgID, templateID, revieweeID)
    ;
  } // Class-Function-End: noticeActivityPeerEvaluateNextOneDay()

  // 针对"同事互评"相关情况对"受邀写评价"人员进行邮件和消息的通知
  @LogAround
  private void noticeInvitationPeerEvaluateNextOneDay(
          long orgID,
          long templateID,
          long revieweeID,
          ReviewInvitation reviewInvitation,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"受邀写评价"人员的 ID
    long reviewerID = reviewInvitation.getReviewerId();
    // 获取"受邀写评价"人员的 Invitation ID
    long invitationID = reviewInvitation.getInvitationId();
    // 向"当前活动"对应的"受邀写评价"的人员发送"通知邮件"
    this.reviewEmailUtils.sendInvitationBeginEmailForManager(
            orgID, templateID, invitationID, revieweeID, reviewerID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"受邀写评价"的人员发送"通知消息"
    this.reviewMessageUtils.sendInvitationBeginMessageForManager(
            orgID, templateID, invitationID, revieweeID, reviewerID
    );
  } // Class-Function-End: noticeInvitationPeerEvaluateNextOneDay()

  // 处理"同事互评"阶段截止日的后一天
  @LogAround
  private void processPeerEvaluateNextOneDay(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    if (reviewActivity.getIsSubmitted() == 0) {
      // 当前设定为如果自评还未提交，则会取消该 Activity，并通知自评人取消消息
      this.noticeActivityPeerEvaluateNextOneDay(
              orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
      );
    } else {
      // 获取"当前活动"对应的"发出被评价"的人员
      long revieweeID = reviewActivity.getRevieweeId();
      // 获取"当前活动"对应的"受邀写评价"的人员列表
      List<ReviewInvitation> reviewInvitations = this.reviewInvitationService
              .listAllReviewInvitationByTemplateIdAndRevieweeId(orgID, templateID, revieweeID);
      // 当前设定为如果自评提交完毕，则会提醒被邀请主管填写评价
      for (ReviewInvitation reviewInvitation : reviewInvitations) {
        // 过滤掉非主管同事
        if (reviewInvitation.getIsManager() == 0) {
          continue;
        }
        // 过滤掉已提交主管
        if (reviewInvitation.getIsSubmitted() == 1) {
          continue;
        }
        this.noticeInvitationPeerEvaluateNextOneDay(
                orgID, templateID, revieweeID, reviewInvitation, mapEmailTemplateIDToBatchEmails
        );
      }
    }
  } // Class-Function-End: processPeerEvaluateNextOneDay()

  //*******************************************************************************************************************

  // 针对"同事互评"相关情况对"受邀写评价"人员进行邮件和消息的通知
  @LogAround
  private void noticeInvitationPublicEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          long revieweeID,
          ReviewInvitation reviewInvitation,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"受邀写评价"人员的 ID
    long reviewerID = reviewInvitation.getReviewerId();
    // 获取"受邀写评价"人员的 Invitation ID
    long invitationID = reviewInvitation.getInvitationId();
    if (reviewInvitation.getIsManager() == 1) {
      // 向"当前活动"对应的"受邀写评价"的主管发送"通知邮件"
      this.reviewEmailUtils.sendInvitationExpireSoonEmailForManager(
              orgID, templateID, invitationID, reviewerID,
              this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
      );
      // 向"当前活动"对应的"受邀写评价"的主管发送"通知消息"
      this.reviewMessageUtils.sendInvitationExpireSoonMessageForManager(
              orgID, templateID, invitationID, revieweeID, reviewerID, this.actorUserID, this.adminUserID
      );
    } else {
      // 向"当前活动"对应的"受邀写评价"的非主管人员发送"通知邮件"
      this.reviewEmailUtils.sendInvitationHasExpiredEmailForPeer(
              orgID, templateID, invitationID, revieweeID, reviewerID,
              this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
      );
      // 向"当前活动"对应的"受邀写评价"的非主管人员发送"通知消息"
      this.reviewMessageUtils.sendInvitationHasExpiredMessageForPeer(
              orgID, templateID, invitationID, revieweeID, reviewerID, this.actorUserID, this.adminUserID
      );
    }
  } // Class-Function-End: noticeInvitationPublicEvaluatePreviousTwoDays()

  // 处理"最终评价"阶段截止日的前两天
  @LogAround
  private void processPublicEvaluatePreviousTwoDays(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    if (reviewActivity.getIsSubmitted() == 1) {
      // 获取"当前活动"对应的"发出被评价"的人员
      long revieweeID = reviewActivity.getRevieweeId();
      // 获取"当前活动"对应的"受邀写评价"的人员列表
      List<ReviewInvitation> reviewInvitations = this.reviewInvitationService
              .listAllReviewInvitationByTemplateIdAndRevieweeId(orgID, templateID, revieweeID);
      // 当前设定为如果自评提交完毕，则会提醒被邀请"未提交"互评人员及主管填写评价
      for (ReviewInvitation reviewInvitation : reviewInvitations) {
        // 过滤掉已提交同事及已提交主管
        if (reviewInvitation.getIsSubmitted() == 1) {
          continue;
        }
        this.noticeInvitationPublicEvaluatePreviousTwoDays(
                orgID, templateID, revieweeID, reviewInvitation, mapEmailTemplateIDToBatchEmails
        );
      }
    }
  } // Class-Function-End: processPublicEvaluatePreviousTwoDays()

  //*******************************************************************************************************************

  // 设置 Invitation 为"取消"状态
  @LogAround
  private void setInvitationCanceled(
          ReviewInvitation reviewInvitation
  ) {
    // 获取"受邀写评价"人员的 ID
    long reviewerID = reviewInvitation.getReviewerId();
    // 设置数据库中相应的 Invitation 状态为"已取消"
    // (设置 template_invitation 表中 is_canceled 字段值为 1)
    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerID);
    this.reviewInvitationService.updateReviewInvitation(reviewInvitation);
    // 获取组织 ID
    long orgID = reviewInvitation.getOrgId();
    // 获取模板 ID
    long templateID = reviewInvitation.getTemplateId();
    // 获取"自评"人员 ID
    long revieweeID = reviewInvitation.getRevieweeId();
    // 删除数据库中与该"受邀写评价"人员相关的评价信息
    // (设置 template_comment 表中 is_delete 字段值为 1)
    this.reviewCommentService.deleteReviewCommentByReviewer(
            orgID, templateID, revieweeID, reviewerID, reviewerID
    );
  } // Class-Function-End: setInvitationCanceled()

  // 针对"同事互评"相关情况对"受邀写评价"人员进行邮件和消息的"取消"通知
  @LogAround
  private void noticeInvitationPublicEvaluateNextOneDay(
          long orgID,
          long templateID,
          long revieweeID,
          ReviewInvitation reviewInvitation,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // TODO: 需要在流程结束后, 将"未提交" Invitation 取消的话, 可将此注释去掉即可
    // this.setInvitationCanceled(reviewInvitation);

    // 获取"受邀写评价"人员的 ID
    long reviewerID = reviewInvitation.getReviewerId();
    // 向"当前活动"对应的"受邀写评价"的人员发送"取消通知邮件"
    this.reviewEmailUtils.sendInvitationCancelEmailForPeer(
            orgID, templateID, revieweeID, reviewerID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"受邀写评价"的人员发送"取消通知消息"
    this.reviewMessageUtils.sendInvitationCancelMessageForPeer(
            orgID, templateID, revieweeID, reviewerID
    );
  } // Class-Function-End: noticeInvitationPublicEvaluateNextOneDay()

  // 如果主管评价已提交, 则通知"自评"人员"流程完成"
  @LogAround
  private void noticeActivityPublicEvaluateNextOneDay(
          long orgID,
          long templateID,
          long revieweeID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // 获取"当前活动"对应的活动 ID
    long activityID = reviewActivity.getActivityId();
    // 向"当前活动"对应的"发出被评价"的人员发送"通知邮件"
    this.reviewEmailUtils.sendActivityFinishedEmailForSelf(
            orgID, templateID, activityID, revieweeID,
            this.actorUserID, this.adminUserID, mapEmailTemplateIDToBatchEmails
    );
    // 向"当前活动"对应的"发出被评价"的人员发送"通知消息"
    this.reviewMessageUtils.sendActivityFinishedMessageForSelf(
            orgID, templateID, activityID, revieweeID
    );
  } // Class-Function-End: noticeActivityPublicEvaluateNextOneDay()

  // 处理"最终评价"阶段截止日的后一天
  @LogAround
  private void processPublicEvaluateNextOneDay(
          long orgID,
          long templateID,
          ReviewActivity reviewActivity,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    if (reviewActivity.getIsSubmitted() == 1) {
      // 获取"当前活动"对应的"发出被评价"的人员
      long revieweeID = reviewActivity.getRevieweeId();
      // 获取"当前活动"对应的"受邀写评价"的人员列表
      List<ReviewInvitation> reviewInvitations = this.reviewInvitationService
              .listAllReviewInvitationByTemplateIdAndRevieweeId(orgID, templateID, revieweeID);
      ReviewInvitation managerInvitation = null;
      for (ReviewInvitation reviewInvitation : reviewInvitations) {
        // 对于"主管人员", 不予发送"取消通知"
        if (reviewInvitation.getIsManager() == 1) {
          managerInvitation = reviewInvitation;
          continue;
        }
        // 对于"已提交"人员, 不予发送"取消通知"
        if (reviewInvitation.getIsSubmitted() == 1) {
          continue;
        }
        // 当前设定为如果自评已经提交，但互评未提交, 则会取消该 Invitation,
        // 并通知非主管互评人取消消息;
        this.noticeInvitationPublicEvaluateNextOneDay(
                orgID, templateID, revieweeID, reviewInvitation, mapEmailTemplateIDToBatchEmails
        );
      }
      // 如果主管已经提交评价, 则会将该 Activity 设置为 FINISH 状态(该需求目前无法实现),
      // 并通知给包括所有已提交未提交的{自评}在内的所有人员.
      if (managerInvitation != null) {
        if (managerInvitation.getIsSubmitted() == 1) {
          this.noticeActivityPublicEvaluateNextOneDay(
                  orgID, templateID, revieweeID, reviewActivity, mapEmailTemplateIDToBatchEmails
          );
        }
      }
    }
  } // Class-Function-End: processPublicEvaluateNextOneDay()

  //*******************************************************************************************************************

  // 设置 Template 状态为"完成"
  @LogAround
  private void setTemplateFinished(ReviewTemplate reviewTemplate) {
    // 获取组织 ID
    long orgID = reviewTemplate.getOrgId();
    // 获取模板 ID
    long templateID = reviewTemplate.getTemplateId();
    // 设置 Template 状态为"完成"(即数据库中 state 值设置为 4)
    this.reviewTemplateService.finishReviewTemplate(orgID, templateID);
  } // Class-Function-End: setTemplateFinished()

  // 处理"流程结束"阶段
  @LogAround
  private void processTemplateFinished(
          ReviewTemplate reviewTemplate,
          HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails
  ) throws Exception {
    // TODO: 需要在流程结束后设置 Template 为 Finish 状态的话, 可将此注释去掉即可
    // this.setTemplateFinished(reviewTemplate);
  } // Class-Function-End: processTemplateFinished()

  //*******************************************************************************************************************

  // ReviewScheduledTaskImpl 类的核心处理函数
  @LogAround
  @Scheduled(cron="0 0 8 * * ?")
  public void processScheduledTask() {
    this.logger.info("Review-Scheduled-Task start ... ...");

    try {
      // 定义存放 邮件模板 ID 到批量邮件集合的映射
      HashMap<Integer, BatchEmail> mapEmailTemplateIDToBatchEmails = new HashMap<>();

      // 获取数据库中正在进行中的(即数据库中 state 为 2 的 Template)所有 Template
      List<ReviewTemplate> reviewTemplates = this.reviewTemplateService.listActiveReviewTemplate();
      if (!CollectionUtils.isEmpty(reviewTemplates)) {
        for (ReviewTemplate reviewTemplate : reviewTemplates) {
          // 获取组织 ID 以及模版 ID
          long orgID = reviewTemplate.getOrgId();
          long templateID = reviewTemplate.getTemplateId();

          // TODO: This line should be deleted after testing.
          // if (templateID != 713) {
          //   continue;
          // }

          // 获取当前 Template 对应的"未删除"以及"未取消"的 Activity 列表
          List<ReviewActivity> reviewActivities =
                  this.reviewActivityService.listUnCanceledReviewActivityOfTemplate(orgID, templateID);
          if (CollectionUtils.isEmpty(reviewActivities)) {
            continue;
          }

          for (ReviewActivity reviewActivity : reviewActivities) {
            // 根据当前时间, 确定当前阶段
            Period period = Period.getEvaluatePeriod(reviewTemplate);
            switch (period) {
              case START:
                // "开始"阶段, 仅做标记用, 暂时无他用
              case SELF:
                // 处理"自我评价"阶段截止日的两天之前, 目前无需通知
                break;
              case SELF_PREVIOUS_TWO_DAYS:
                // 处理"自我评价"阶段截止日的前两天
                this.processSelfEvaluatePreviousTwoDays(
                        orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
                );
                break;
              case SELF_PREVIOUS_ONE_DAY:
                // 处理"自我评价"阶段截止日的前一天, 目前无需通知
                break;
              case SELF_NEXT_ONE_DAY:
                // 处理"自我评价"阶段截止日的后一天
                this.processSelfEvaluateNextOneDay(
                        orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
                );
                break;
              case PEER:
                // 处理"同事互评"阶段截止日的两天之前, 目前无需通知
                break;
              case PEER_PREVIOUS_TWO_DAYS:
                // 处理"同事互评"阶段截止日的前两天
                this.processPeerEvaluatePreviousTwoDays(
                        orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
                );
                break;
              case PEER_PREVIOUS_ONE_DAY:
                // 处理"同事互评"阶段截止日的前一天, 目前无需通知
                break;
              case PEER_NEXT_ONE_DAY:
                // 处理"同事互评"阶段截止日的后一天
                this.processPeerEvaluateNextOneDay(
                        orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
                );
                break;
              case PUBLIC:
                // 处理"最终评价"阶段截止日的两天之前, 目前无需通知
                break;
              case PUBLIC_PREVIOUS_TWO_DAYS:
                // 处理"最终评价"阶段截止日的前两天
                this.processPublicEvaluatePreviousTwoDays(
                        orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
                );
                break;
              case PUBLIC_PREVIOUS_ONE_DAY:
                // 处理"最终评价"阶段截止日的前一天, 目前无需通知
                break;
              case PUBLIC_NEXT_ONE_DAY:
                // 处理"最终评价"阶段截止日的后一天
                this.processPublicEvaluateNextOneDay(
                        orgID, templateID, reviewActivity, mapEmailTemplateIDToBatchEmails
                );
              case FINISH:
                // "流程结束"阶段, 目前无需通知, 将 Template 状态设置为"完成"
                this.processTemplateFinished(
                        reviewTemplate, mapEmailTemplateIDToBatchEmails
                );
                break;
              default:
                // "其他"阶段, 目前无需通知
                break;
            }
          }
        }
      }

      // 开始批量发送邮件
      this.reviewEmailUtils.sendBatchEmails(mapEmailTemplateIDToBatchEmails);

    } catch (Exception e) {
      this.logger.error("processScheduledTask: ", e);
    }

    this.logger.info("Review-Scheduled-Task finished!");
  } // Class-Function-End: processScheduledTask()
} // Class-End: ReviewScheduledTaskImpl
