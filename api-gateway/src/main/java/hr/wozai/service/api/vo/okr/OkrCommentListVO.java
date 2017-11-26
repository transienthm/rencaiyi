package hr.wozai.service.api.vo.okr;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/9/12
 */
@Data
@NoArgsConstructor
public class OkrCommentListVO {
  private List<OkrCommentVO> okrCommentVOList;

  private long totalRecordNum;
}
