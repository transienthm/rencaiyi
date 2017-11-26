package hr.wozai.service.api.vo.okr;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/26
 */
@Data
@NoArgsConstructor
public class OkrUpdateLogVO {
  @JsonSerialize(using = EncodeSerializer.class)
  private Long okrUpdateLogId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long okrCommentId;

  private String attribute;

  private String beforeValue;

  private String afterValue;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long createdUserId;

  private Long createdTime;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long lastModifiedUserId;

  private Long lastModifiedTime;
}
