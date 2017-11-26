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
public class OkrLogVO {
  @JsonSerialize(using = EncodeSerializer.class)
  private Long okrLogId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long objectiveId;

  private Integer type;

  private String content;

  private List<CoreUserProfileVO> atUsers;

  private CoreUserProfileVO actorUser;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Long commentNumber;
}
