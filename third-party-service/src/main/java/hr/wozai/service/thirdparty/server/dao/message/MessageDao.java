package hr.wozai.service.thirdparty.server.dao.message;

import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/8
 */
@Repository("messageDao")
public class MessageDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.thirdparty.server.dao.message.MessageMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public long insertMessage(Message message) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertMessage", message);
    return message.getMessageId();
  }

  @LogAround
  public Message findPersonalMessage(Message message) {
    Message result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findPersonalMessageByTypeAndTemplatedIdAndObjectIdAndReceiverIdAndStatus", message);
    return result;
  }

  @LogAround
  public Message findSystemMessage(Message message) {
    Message result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findSystemMessageByTypeAndTemplatedIdAndObjectId", message);
    return result;
  }

  @LogAround
  public int updateSenders(long orgId, long messageId, List<String> senders) {
    Map<String, Object> params = new HashMap();
    params.put("orgId", orgId);
    params.put("messageId", messageId);
    String listString = StringUtils.join(senders, ",");
    params.put("senders", listString);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "updateSenders", params);
    return result;
  }

  @LogAround
  public int batchUpdateStatus(long orgId, long receiverId, List<Long> messageIds, int isRead) {
    Map<String, Object> params = new HashMap();
    params.put("orgId", orgId);
    params.put("receiverId", receiverId);
    params.put("messageIds", messageIds);
    params.put("isRead", isRead);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateStatus", params);
    return result;
  }

  @LogAround
  public List<Message> listAllMessages(long orgId, long receiverId, long onboardingTime,
                                       List<Long> readSystemMessageIds, int pageNumber, int pageSize) {
    Map<String, Object> params = new HashMap();
    params.put("orgId", orgId);
    params.put("receiverId", receiverId);
    params.put("onboardingTime", onboardingTime);
    params.put("readSystemMessageIds", readSystemMessageIds);
    int pageStart = (pageNumber - 1) * pageSize;
    params.put("pageStart", pageStart);
    params.put("pageSize", pageSize);

    List<Message> messages = sqlSessionTemplate.selectList(BASE_PACKAGE + "listAllMessages", params);

    return messages;
  }

  @LogAround
  public List<Message> listAllMessagesWithoutPagedquery(long orgId, long receiverId, long onboardingTime, List<Long> readSystemMessageIds) {
    Map<String, Object> params = new HashMap();
    params.put("orgId", orgId);
    params.put("receiverId", receiverId);
    params.put("onboardingTime", onboardingTime);
    params.put("readSystemMessageIds", readSystemMessageIds);

    List<Message> messages = sqlSessionTemplate.selectList(BASE_PACKAGE + "listAllMessagesWithoutPagedQuery", params);

    return messages;
  }

  @LogAround
  public int getUnReadMessageNumber(long orgId, long receiverId, long onboardingTime, List<Long> readSystemMessageIds) {
    Map<String, Object> params = new HashMap();
    params.put("orgId", orgId);
    params.put("receiverId", receiverId);
    params.put("onboardingTime", onboardingTime);
    params.put("readSystemMessageIds", readSystemMessageIds);

    int result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "getUnReadMessageNumber", params);
    return result;
  }
}
