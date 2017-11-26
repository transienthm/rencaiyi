package hr.wozai.service.thirdparty.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.dto.MessageListDTO;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/12
 */
@ThriftService
public interface MessageCenterFacade {
  @ThriftMethod
  LongDTO addSystemMessage(MessageDTO messageDTO);

  @ThriftMethod
  VoidDTO addPersonalMessage(MessageDTO messageDTO, List<Long> receiverIds);

  @ThriftMethod
  MessageListDTO listAllMessages(long orgId, long receiverId, long onboardingTime, int pageNumber, int pageSize);

  @ThriftMethod
  IntegerDTO getUnReadMessageNumber(long orgId, long receiverId, long onboardingTime);
}
