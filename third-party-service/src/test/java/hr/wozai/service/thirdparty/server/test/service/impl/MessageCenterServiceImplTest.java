package hr.wozai.service.thirdparty.server.test.service.impl;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.server.service.MessageCenterService;
import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.thirdparty.server.model.message.MessageLog;
import hr.wozai.service.thirdparty.server.service.MessageCenterService;
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
public class MessageCenterServiceImplTest extends BaseTest {
  @Autowired
  MessageCenterService messageCenterService;

  private long orgId = 12L;
  private long userId = 12L;
  private long recieverId = 12L;
  private long objectId1 = 12L;
  private long objectId2 = 13L;
  private int templateId = 10;

  @Test
  public void testAll() throws Exception {
    //插入一条系统消息
    Message systemMessage = new Message();
    systemMessage.setSenders(new ArrayList<>());
    systemMessage.setOrgId(orgId);
    systemMessage.setObjectId(objectId1);
    systemMessage.setTemplateId(templateId);
    systemMessage.setIsRead(0);
    messageCenterService.insertSystemMessage(systemMessage);

    //插入一条个人消息
    Message personalMessage = new Message();
    personalMessage.setOrgId(orgId);
    personalMessage.setSenders(Arrays.asList(String.valueOf(userId)));
    personalMessage.setObjectId(objectId2);
    personalMessage.setTemplateId(templateId);
    personalMessage.setReceiverId(recieverId);
    messageCenterService.insertPersonalMessages(Arrays.asList(personalMessage));

    personalMessage.setSenders(new ArrayList<>());
    messageCenterService.insertPersonalMessages(Arrays.asList(personalMessage));


    //插入一条同种类的个人消息
    personalMessage.setSenders(Arrays.asList("10", "11", "12"));
    messageCenterService.insertPersonalMessages(Arrays.asList(personalMessage));

    personalMessage.setSenders(Arrays.asList());
    messageCenterService.insertPersonalMessages(Arrays.asList(personalMessage));


    //查看未读消息数目
    int number = messageCenterService.getUnReadMessageNumber(orgId, recieverId, Long.MIN_VALUE);
    Assert.assertEquals(2, number);


    //查看消息列表
    List<Message> messages = messageCenterService.listAllMessagesByReceiverId(orgId,
            recieverId, Long.MIN_VALUE, 1, 20);
    System.out.println("+++++" + messages);
    Assert.assertEquals(2, messages.size());
    messages = messageCenterService.listAllMessagesByReceiverIdWithoutPagedQuery(orgId,
            recieverId, Long.MIN_VALUE);
    Assert.assertEquals(2, messages.size());
    Message m1 = messages.get(0);
    Message m2 = messages.get(1);
    Assert.assertEquals(objectId2, m1.getObjectId().longValue());
    Assert.assertEquals(objectId1, m2.getObjectId().longValue());

    //再次查看未读消息
    number = messageCenterService.getUnReadMessageNumber(orgId, recieverId, Long.MIN_VALUE);
    Assert.assertEquals(0, number);

    //再次插入一条个人消息
    personalMessage.setSenders(Arrays.asList("11", "12"));
    messageCenterService.insertPersonalMessages(Arrays.asList(personalMessage));

    number = messageCenterService.getUnReadMessageNumber(orgId, recieverId, Long.MIN_VALUE);
    Assert.assertEquals(1, number);

    messages = messageCenterService.listAllMessagesByReceiverId(orgId,
            recieverId, Long.MIN_VALUE, 1, 20);
    System.out.println("+++++" + messages);
    Assert.assertEquals(3, messages.size());

    messageCenterService.insertPersonalMessages(Arrays.asList(personalMessage));
    messageCenterService.insertSystemMessage(systemMessage);
    messages = messageCenterService.listAllMessagesByReceiverIdWithoutPagedQuery(orgId, recieverId, Long.MIN_VALUE);
    Assert.assertEquals(5, messages.size());
  }
}