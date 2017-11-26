package hr.wozai.service.thirdparty.server.dao.message;

import hr.wozai.service.thirdparty.client.dto.MessageListDTO;
import hr.wozai.service.thirdparty.server.model.message.MessageLog;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
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
@Repository("messageLogDao")
public class MessageLogDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.thirdparty.server.dao.message.MessageLogMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public int batchInsertMessageLog(List<MessageLog> messageLogs) {
    int result = sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertMessageLog", messageLogs);

    return result;
  }

  @LogAround
  public MessageLog findMessageLog(long orgId, long messageId, long receiverId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("messageId", messageId);
    params.put("receiverId", receiverId);
    MessageLog result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findMessageLog", params);

    return result;
  }

  @LogAround
  public List<Long> listMessageIdsByRecId(long orgId, long receiverId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("receiverId", receiverId);
    List<Long> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listMessageIdsByRecId", params);

    return result;
  }
}
