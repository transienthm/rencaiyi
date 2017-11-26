package hr.wozai.service.user.server.model.survey;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/24
 */
@Data
@NoArgsConstructor
public class SurveyActivity {
  private Long surveyActivityId;

  private Long orgId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
