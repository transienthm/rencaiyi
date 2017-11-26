package hr.wozai.service.api.vo.survey;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/12/7
 */
@Data
@NoArgsConstructor
public class SurveyItemHistoryVO {
  private SurveyItemVO surveyItemVO;

  private List<SurveyActivityVO> surveyActivityVOs;
}
