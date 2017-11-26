package hr.wozai.service.thirdparty.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.server.dao.message.MessageDao;
import hr.wozai.service.thirdparty.server.dao.message.MessageLogDao;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.server.enums.MessageStatus;
import hr.wozai.service.thirdparty.server.enums.MessageType;
import hr.wozai.service.thirdparty.server.helper.MessageHelper;
import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.thirdparty.server.model.message.MessageLog;
import hr.wozai.service.thirdparty.server.service.MessageCenterService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/8
 */
@Service("messageCenterServiceImpl")
public class MessageCenterServiceImpl implements MessageCenterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCenterServiceImpl.class);

    @Autowired
    MessageDao messageDao;

    @Autowired
    MessageLogDao messageLogDao;

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long insertSystemMessage(Message message) {
        MessageHelper.checkMessageInsertParama(message);
        message.setType(MessageType.SYSTEM.getCode());
        message.setReceiverId(0L);
        message.setIsRead(0);

        messageDao.insertMessage(message);
        return message.getMessageId();
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertPersonalMessages(List<Message> messages) {
        for (Message message : messages) {
            MessageHelper.checkMessageInsertParama(message);
            message.setType(MessageType.PERSONAL.getCode());
            message.setIsRead(MessageStatus.UNREAD.getCode());

            Message inDb = messageDao.findPersonalMessage(message);
            if (inDb == null) {
                // mysql里没有同种类的未读消息
                messageDao.insertMessage(message);
            } else {
                List<String> preSenders = inDb.getSenders();
                if (CollectionUtils.isEmpty(message.getSenders())) {
                    return;
                }
                HashSet<String> h = new HashSet<>();
                for (String s : preSenders) {
                    h.add(s);
                }
                for (String s : message.getSenders()) {
                    h.add(s);
                }
                List<String> noDuplicate = new ArrayList<>();
                noDuplicate.addAll(h);
                messageDao.updateSenders(inDb.getOrgId(), inDb.getMessageId(), noDuplicate);
            }
        }
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public List<Message> listAllMessagesByReceiverIdWithoutPagedQuery(long orgId, long receiverId, long onboardingTime) {
        List<Long> readSystemMessageIds = messageLogDao.listMessageIdsByRecId(orgId, receiverId);
        List<Message> messages = messageDao.listAllMessagesWithoutPagedquery(orgId, receiverId, onboardingTime,
                readSystemMessageIds);


        List<Long> toUpdateMessageIds = new ArrayList<>();
        List<MessageLog> toInsertMessageLogs = new ArrayList<>();

        for (Message message : messages) {
            int type = message.getType();
            int isRead = message.getIsRead();
            if (type == MessageType.PERSONAL.getCode() && isRead == MessageStatus.UNREAD.getCode()) {
                toUpdateMessageIds.add(message.getMessageId());
            } else if (type == MessageType.SYSTEM.getCode() && isRead == MessageStatus.UNREAD.getCode()) {
                MessageLog messageLog = new MessageLog();
                messageLog.setOrgId(orgId);
                messageLog.setReceiverId(receiverId);
                messageLog.setMessageId(message.getMessageId());
                toInsertMessageLogs.add(messageLog);
            }
        }
        if (toUpdateMessageIds.size() > 0) {
            messageDao.batchUpdateStatus(orgId, receiverId, toUpdateMessageIds, MessageStatus.READ.getCode());
        }
        if (toInsertMessageLogs.size() > 0) {
            messageLogDao.batchInsertMessageLog(toInsertMessageLogs);
        }
        return messages;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public List<Message> listAllMessagesByReceiverId(long orgId, long receiverId,
                                                     long onboardingTime, int pageNumber, int pageSize) {

        List<Long> readSystemMessageIds = messageLogDao.listMessageIdsByRecId(orgId, receiverId);
        List<Message> messages = messageDao.listAllMessages(orgId, receiverId, onboardingTime,
                readSystemMessageIds, pageNumber, pageSize);


        List<Long> toUpdateMessageIds = new ArrayList<>();
        List<MessageLog> toInsertMessageLogs = new ArrayList<>();

        for (Message message : messages) {
            int type = message.getType();
            int isRead = message.getIsRead();
            if (type == MessageType.PERSONAL.getCode() && isRead == MessageStatus.UNREAD.getCode()) {
                toUpdateMessageIds.add(message.getMessageId());
            } else if (type == MessageType.SYSTEM.getCode() && isRead == MessageStatus.UNREAD.getCode()) {
                MessageLog messageLog = new MessageLog();
                messageLog.setOrgId(orgId);
                messageLog.setReceiverId(receiverId);
                messageLog.setMessageId(message.getMessageId());
                toInsertMessageLogs.add(messageLog);
            }
        }
        if (toUpdateMessageIds.size() > 0) {
            messageDao.batchUpdateStatus(orgId, receiverId, toUpdateMessageIds, MessageStatus.READ.getCode());
        }
        if (toInsertMessageLogs.size() > 0) {
            messageLogDao.batchInsertMessageLog(toInsertMessageLogs);
        }
        return messages;
    }

    @Override
    @LogAround
    public int getUnReadMessageNumber(long orgId, long receiverId, long time) {
        List<Long> readSystemMessageIds = messageLogDao.listMessageIdsByRecId(orgId, receiverId);
        int result = messageDao.getUnReadMessageNumber(orgId, receiverId, time, readSystemMessageIds);

        return result;
    }
}
