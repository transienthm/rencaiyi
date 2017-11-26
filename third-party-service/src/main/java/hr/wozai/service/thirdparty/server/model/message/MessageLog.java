package hr.wozai.service.thirdparty.server.model.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/6
 */
@Data
@NoArgsConstructor
public class MessageLog {
  private Long messageLogId;

  private Long orgId;

  private Long receiverId;

  private Long messageId;

  private Long createdTime;

  private Integer isDeleted;
}
