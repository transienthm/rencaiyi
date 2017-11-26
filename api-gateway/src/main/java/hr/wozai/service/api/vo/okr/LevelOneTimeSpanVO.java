package hr.wozai.service.api.vo.okr;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/6/23
 */
@Data
@NoArgsConstructor
public class LevelOneTimeSpanVO {
  private Integer type;

  private String name;

  private boolean isDefault = false;
}
