package hr.wozai.service.thirdparty.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.dto.MessageListDTO;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.thirdparty.server.service.MessageCenterService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.thirdparty.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/13
 */
public class MessageCenterFacadeImplTest extends BaseTest {
  @Autowired
  MessageCenterFacade messageCenterFacade;

  @Mock
  MessageCenterService spyMessageCenterService;

  private long orgId = 10L;
  private List<Long> userIds = new ArrayList<>();
  private long recieverId = 11L;
  private long objectId1 = 10L;
  private long objectId2 = 11L;
  private int templateId = 10;

  @Test
  public void testAddSystemMessage() throws Exception {
    //插入一条系统消息
    MessageDTO systemMessage = new MessageDTO();
    systemMessage.setSenders(new ArrayList<>());
    systemMessage.setOrgId(orgId);
    systemMessage.setObjectId(objectId1);
    systemMessage.setTemplateId(templateId);
    messageCenterFacade.addSystemMessage(systemMessage);

    //插入一条个人消息
    MessageDTO personalMessage = new MessageDTO();
    personalMessage.setOrgId(orgId);
    userIds.add(10l);
    userIds.add(11l);
    personalMessage.setSenders(userIds);
    personalMessage.setObjectId(objectId2);
    personalMessage.setTemplateId(templateId);
    messageCenterFacade.addPersonalMessage(personalMessage, Arrays.asList(recieverId));

    personalMessage.setSenders(new ArrayList<>());
    messageCenterFacade.addPersonalMessage(personalMessage, Arrays.asList(recieverId));

    IntegerDTO number = messageCenterFacade.getUnReadMessageNumber(orgId, recieverId, Long.MIN_VALUE);
    Assert.assertEquals(2, number.getData());

    MessageListDTO inDb = messageCenterFacade.listAllMessages(orgId, recieverId, Long.MIN_VALUE, 1, 20);
    System.out.println(inDb);


    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(messageCenterFacade), "messageCenterService", spyMessageCenterService);

    Mockito.when(spyMessageCenterService.insertSystemMessage(Mockito.anyObject())).thenThrow(new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM));
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM)).when(spyMessageCenterService).insertPersonalMessages(Mockito.anyObject());
    Mockito.when(spyMessageCenterService.listAllMessagesByReceiverId(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()
    )).thenThrow(new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR));
    Mockito.when(spyMessageCenterService.listAllMessagesByReceiverIdWithoutPagedQuery(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()
    )).thenThrow(new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR));
    Mockito.when(spyMessageCenterService.getUnReadMessageNumber(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()
    )).thenThrow(new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR));

    try {
      messageCenterFacade.addPersonalMessage(new MessageDTO(), new ArrayList<>());
      messageCenterFacade.addSystemMessage(new MessageDTO());
      messageCenterFacade.getUnReadMessageNumber(10l, 10l, 10l);
      messageCenterFacade.listAllMessages(10l, 10l, 10l, 1, 20);
    } catch (Exception e) {

    }
  }

}