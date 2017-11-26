package hr.wozai.service.thirdparty.server.model.message;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/6
 */
@Data
@NoArgsConstructor
public class Message {
  private Long messageId;

  private Long orgId;

  private List<String> senders;

  private Integer templateId;

  // PERSONAL:0  SYSTEM:1
  private Integer type;

  private Long objectId;

  private Long receiverId;

  private JSONObject objectContent;

  //unread:0 read:1
  private Integer isRead;

  private Long createdTime;

  private Integer isDeleted;
}
