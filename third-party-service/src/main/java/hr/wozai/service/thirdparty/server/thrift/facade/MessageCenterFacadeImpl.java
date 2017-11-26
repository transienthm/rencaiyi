package hr.wozai.service.thirdparty.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.dto.MessageListDTO;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.server.helper.FacadeExceptionHelper;
import hr.wozai.service.thirdparty.server.model.message.Message;
import hr.wozai.service.thirdparty.server.service.MessageCenterService;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/12
 */
@Service("messageCenterFacade")
public class MessageCenterFacadeImpl implements MessageCenterFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaFacadeImpl.class);

    @Autowired
    MessageCenterService messageCenterService;

    @Override
    @LogAround
    public LongDTO addSystemMessage(MessageDTO messageDTO) {
        LongDTO result = new LongDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Message message = new Message();
            BeanHelper.copyPropertiesHandlingJSON(messageDTO, message);
            long id = messageCenterService.insertSystemMessage(message);
            result.setData(id);
        } catch (Exception e) {
            LOGGER.error("addSystemMessage()-error:{}", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @Override
    @LogAround
    public VoidDTO addPersonalMessage(MessageDTO messageDTO, List<Long> receiverIds) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Message message = new Message();
            BeanHelper.copyPropertiesHandlingJSON(messageDTO, message);
            //将List<Long>转为List<String>
            List<String> senders = convertGenericityLongToString(messageDTO.getSenders());
            message.setSenders(senders);

            List<Message> messages = new ArrayList<>();
            for (Long receiverId : receiverIds) {
                message.setReceiverId(receiverId);
                messages.add(message);
            }
            messageCenterService.insertPersonalMessages(messages);
        } catch (Exception e) {
            LOGGER.error("addPersonalMessage()-error:{}",e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }


    @Override
    @LogAround
    public MessageListDTO listAllMessages(long orgId, long receiverId,
                                          long onboardingTime, int pageNumber, int pageSize) {
        MessageListDTO result = new MessageListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Message> messages = messageCenterService.listAllMessagesByReceiverId(orgId, receiverId,
                    onboardingTime, pageNumber, pageSize);
            List<Message> totalMessages = messageCenterService.listAllMessagesByReceiverIdWithoutPagedQuery(orgId,
                    receiverId, onboardingTime);
            result.setTotalNumber(totalMessages.size());
            List<MessageDTO> messageDTOs = new ArrayList<>();

            for (Message message : messages) {
                MessageDTO messageDTO = new MessageDTO();
                BeanHelper.copyPropertiesHandlingJSON(message, messageDTO);
                //List<String>转List<Long>
                List<Long> senders = convertGenericityStringToLong(message.getSenders());
                messageDTO.setSenders(senders);
                messageDTOs.add(messageDTO);

        /*String date = getDate(message.getCreatedTime());
        LOGGER.info("####:{}", date);
        if (messageDTOs.containsKey(date)) {
          List<MessageDTO> exist = messageDTOs.get(date);
          exist.add(messageDTO);
          messageDTOs.put(date, exist);
        } else {
          List<MessageDTO> newOne = new ArrayList<>();
          newOne.add(messageDTO);
          messageDTOs.put(date, newOne);
        }*/
            }
            int unReadNumber = messageCenterService.getUnReadMessageNumber(orgId, receiverId, onboardingTime);
            result.setMessageDTOs(messageDTOs);
            result.setUnReadNumber(unReadNumber);
        } catch (Exception e) {
            LOGGER.error("listAllMessages()-error:{}",e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @Override
    @LogAround
    public IntegerDTO getUnReadMessageNumber(long orgId, long receiverId, long onboardingTime) {
        IntegerDTO result = new IntegerDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            int number = messageCenterService.getUnReadMessageNumber(orgId, receiverId, onboardingTime);
            result.setData(number);
        } catch (Exception e) {
            LOGGER.error("getUnReadMessageNumber()-error:{}", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    private List<String> convertGenericityLongToString(List<Long> longList) {
        if (!CollectionUtils.isEmpty(longList)) {
            List<String> result = new ArrayList<>();
            for (long l : longList) {
                //如果list中有null元素,则s为"null"
                String s = String.valueOf(l);
                result.add(s);
            }
            return result;
        } else {
            return null;
        }
    }

    private List<Long> convertGenericityStringToLong(List<String> stringList) {

        if (!CollectionUtils.isEmpty(stringList)) {
            List<Long> result = new ArrayList<>();
            for (String s : stringList) {
                if (s.equals("null")) {
                    continue;
                }
                long l = Long.valueOf(s);
                result.add(l);
            }
            return result;
        } else {
            return null;
        }
    }
}
