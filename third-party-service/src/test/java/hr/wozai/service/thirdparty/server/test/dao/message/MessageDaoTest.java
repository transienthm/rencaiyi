package hr.wozai.service.thirdparty.server.test.dao.message;

import hr.wozai.service.thirdparty.server.dao.message.MessageDao;
import hr.wozai.service.thirdparty.server.dao.message.MessageLogDao;
import hr.wozai.service.thirdparty.server.enums.MessageStatus;
import hr.wozai.service.thirdparty.server.enums.MessageType;
import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.thirdparty.server.model.message.MessageLog;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/11
 */
public class MessageDaoTest extends BaseTest {
  @Autowired
  MessageDao messageDao;

  @Autowired
  MessageLogDao messageLogDao;

  @Test
  public void testListAllMessages() throws Exception {
    long orgId = 11L;
    long senderId = 11L;
    int templateId = 11;
    long objectId = 11L;
    long receiverId = 11L;
    Message message = new Message();
    message.setOrgId(orgId);
    message.setSenders(Arrays.asList(String.valueOf(senderId)));
    message.setTemplateId(templateId);
    message.setType(MessageType.PERSONAL.getCode());
    message.setObjectId(objectId);
    message.setReceiverId(receiverId);
    message.setIsRead(MessageStatus.UNREAD.getCode());

    long messageId1 = messageDao.insertMessage(message);

    Message message2 = new Message();
    message2.setOrgId(orgId);
    message2.setSenders(Arrays.asList(String.valueOf(senderId)));
    message2.setTemplateId(templateId);
    message2.setType(MessageType.SYSTEM.getCode());
    message2.setObjectId(objectId);
    message2.setReceiverId(0L);
    message2.setIsRead(MessageStatus.UNREAD.getCode());
    long messageId2 = messageDao.insertMessage(message2);


    Message inDb = messageDao.findSystemMessage(message2);
    Assert.assertEquals(messageId2, inDb.getMessageId().longValue());
    messageDao.updateSenders(orgId, messageId2, Arrays.asList("11", "12"));
    inDb = messageDao.findSystemMessage(message2);
    Assert.assertEquals(2, inDb.getSenders().size());
    Assert.assertEquals("11", inDb.getSenders().get(0));

    MessageLog messageLog = new MessageLog();
    messageLog.setOrgId(orgId);
    messageLog.setReceiverId(receiverId);
    messageLog.setMessageId(message2.getMessageId());
    List<MessageLog> messageLogs = new ArrayList<>();
    messageLogs.add(messageLog);
    int result = messageLogDao.batchInsertMessageLog(messageLogs);
    Assert.assertEquals(1, result);

    MessageLog messageLog1 = messageLogDao.findMessageLog(orgId, messageId2, receiverId);
    Assert.assertEquals(new Long(messageId2),messageLog1.getMessageId());

    List<Long> messageIds = messageLogDao.listMessageIdsByRecId(orgId, receiverId);
    Assert.assertEquals(new Long(messageId2), messageIds.get(0));


    Message personal = messageDao.findPersonalMessage(message);
    Assert.assertNotNull(personal);
    messageDao.batchUpdateStatus(orgId, receiverId, Arrays.asList(messageId1), MessageStatus.READ.getCode());
    personal = messageDao.findPersonalMessage(message);
    Assert.assertNull(personal);

    List<Long> param = new ArrayList<>();
    List<Message> resultList = messageDao.listAllMessages(orgId, receiverId, Long.MIN_VALUE, param, 1, 20);
    Assert.assertEquals(2, resultList.size());
    resultList = messageDao.listAllMessagesWithoutPagedquery(orgId, receiverId, Long.MIN_VALUE, param);
    Assert.assertEquals(2, resultList.size());

    int number = messageDao.getUnReadMessageNumber(orgId, receiverId, Long.MIN_VALUE, param);
    Assert.assertEquals(1, number);


  }
}