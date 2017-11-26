package hr.wozai.service.user.server.model.survey;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
@Data
@NoArgsConstructor
public class SurveyConfig {
  private Long surveyConfigId;

  private Long orgId;

  private Integer frequency;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
