// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.client.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-3-11
 */
public enum MessageTemplate {

  NEWS_FEED_AT(1, "{{users}} 在首页动态 {{content}} 中@了你。", 1),
  NEWS_FEED_DIANZAN(2, "{{users}} 在首页动态 {{content}} 中给你点了赞。", 1),
  NEWS_FEED_COMMENT(3, "{{users}} 在首页动态 {{content}} 中回复了你。", 1),
  NEWS_FEED_COMMENT_AT(4,"{{users}} 在首页动态 {{content}} 的评论中@了你。",1),

  ONBOARDING_SUBMIT(101, "{{users}} 提交了入职资料，请查看并审核。{{link}}", 2),
  ONBOARDING_REJECT(102, "请补充填写入职资料，重新提交审核，谢谢！{{link}}", 2),
  ONBOARDING_APPROVE(103, "你的资料已经审核通过，跟大家打个招呼吧！{{link}}", 2),

  REVIEW_ACTIVITY_BEGIN(201, "{{templateName}} 反馈活动已开始，请提交自评。{{link}}", 3),
  REVIEW_ACTIVITY_CANCEL(202, "{{templateName}} 反馈活动已取消，请留意。", 3),
  REVIEW_ONGOING(203, "{{users}} 邀请您对他做出同事反馈，本次活动同事反馈截止日为 {{peerReviewDeadline}}。{{link}}", 3),
  REVIEW_FINISH(205, "{{templateName}} 反馈活动结果已公示，{{link}}", 3),
  REVIEW_PEER_BATCH_INVITE(206, "{{users}}等{{peerCount}}位同事邀请您对他做出同事反馈。{{link}}", 3),
  REVIEW_MANAGER_BATCH_INVITE(207, "{{users}}等{{peerCount}}位同事邀请您对他做出主管反馈。{{link}}", 3),
  REVIEW_PEER_NOTIFY_MANAGER(208, "{{users}}提交了对{{revieweeName}}的同事反馈。{{link}}", 3),
  REVIEW_ACTIVITY_AUTO_CANCEL(209, "{{templateName}} 反馈活动因逾期而自动取消。", 3),
  REVIEW_INVITATION_AUTO_CANCEL(210, "您在 {{templateName}} 反馈活动中, 对 {{revieweeName}} 的同事反馈因逾期而自动取消。", 3),
  REVIEW_MANAGER_INVITE(211, "{{users}}邀请您对他做出主管反馈。{{link}}", 3),

  OKR_UPDATE(301, "你的团队目标 {{content}} 已更新。", 4),
  OKR_ADD(302, "{{users}} 给你添加了目标。", 4),
  OKR_LOG_AT(304, "{{users}} 在目标动态 {{content}} 中@了你，{{link}}", 4),
  OKR_LOG_COMMENT(305, "{{users}} 在目标动态 {{content}} 中回复了你，{{link}}", 4),

  RESIGN_NOTIFICATION(401, "{{userName}}已完成离职操作,请知悉。",5),
  TRANSFER_NOTIFICATION(402, "{{userName}} 已完成调岗操作，请点击个人主页查看最新岗位信息。{{link}}", 5),
  PASS_PROBATION_TO_DIRECTOR(403,"{{userName}} 已完成转正操作，转正日期为 {{date}}，请知悉。",5),
  PASS_PROBATION_TO_STAFF(404, "恭喜，你已完成转正操作，转正日期为 {{date}}。", 5),

  SURVEY_PUSH(501,"全员匿名调研问卷已送达！请{{link}}，花一分钟填写。",6),
  CONVR_SCHEDULE_REMINDER(601,"提醒您与 {{users}} 等同事做定期交谈。{{link}}",7);


  private Integer code;
  private String content;
  private Integer messageType;

  MessageTemplate(Integer code, String desc, Integer messageType) {
    this.code = code;
    this.content = desc;
    this.messageType = messageType;
  }

  public static MessageTemplate getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (MessageTemplate messageTemplate : MessageTemplate.values()) {
      if (IntegerUtils.equals(messageTemplate.code, code)) {
        return messageTemplate;
      }
    }

    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getContent() {
    return content;
  }

  public Integer getMessageType() {
    return messageType;
  }

  public static boolean isValidTemplate(int type) {
    for (MessageTemplate messageTemplate : MessageTemplate.values()) {
      if (messageTemplate.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
