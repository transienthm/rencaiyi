package hr.wozai.service.thirdparty.server.service;

import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.thirdparty.server.model.message.MessageLog;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/8
 */
public interface MessageCenterService {
  /**
   * 插入一条SYSTEM消息,由于是给每个人发,不需要往消息记录里写
   * @param message
   * @return
   */
  long insertSystemMessage(Message message);

  /**
   * 插入多条个人消息
   * @param messages
   * @return
   */
  void insertPersonalMessages(List<Message> messages);

  /**
   * 1. 分页列出用户的所有消息:已读与未读
   * 2. 把其中的未读消息更新成未读
   * @param orgId
   * @param receiverId
   * @param onboardingTime 入职时间
   */
  List<Message> listAllMessagesByReceiverId(long orgId, long receiverId, long onboardingTime,
                                               int pageNumber, int pageSize);

  /**
   * 非分页查询接口
   * @param orgId
   * @param receiverId
   * @param onboardingTime
     * @return
     */
  List<Message> listAllMessagesByReceiverIdWithoutPagedQuery(long orgId, long receiverId, long onboardingTime);
  /**
   * 获取未读的消息个数
   * @param orgId
   * @param receiverId
   * @param time
   * @return
   */
  int getUnReadMessageNumber(long orgId, long receiverId, long time);
}
