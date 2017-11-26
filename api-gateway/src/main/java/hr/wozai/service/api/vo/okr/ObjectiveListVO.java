package hr.wozai.service.api.vo.okr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/1
 */
@Data
@NoArgsConstructor
public class ObjectiveListVO {
  private List<ObjectiveVO> objectiveVOList;

  private String totalProgress;

  private long beginTimestamp;

  private long endTimestamp;

  /*private boolean periodDeletable;

  private boolean objectiveDeletable;

  private boolean objectiveCreatable;

  private boolean objectiveEditable;*/
}
