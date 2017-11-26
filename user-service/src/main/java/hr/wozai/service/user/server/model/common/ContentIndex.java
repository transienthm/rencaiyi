package hr.wozai.service.user.server.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/21
 */
@Data
@NoArgsConstructor
public class ContentIndex {
  private String contentIndexId;

  private Long orgId;

  private Integer type;

  private Long contentObjectId;

  private String content;

  private String pinyin;

  private String abbreviation;

  private Integer isDeleted;
}
