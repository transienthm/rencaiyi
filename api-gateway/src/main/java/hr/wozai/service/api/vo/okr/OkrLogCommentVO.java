package hr.wozai.service.api.vo.okr;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
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
public class OkrLogCommentVO {
  @JsonSerialize(using = EncodeSerializer.class)
  private Long commentId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long okrLogId;

  private CoreUserProfileVO actorUser;

  private String content;

  private List<CoreUserProfileVO> atUsers;

  private Long createdTime;
}
