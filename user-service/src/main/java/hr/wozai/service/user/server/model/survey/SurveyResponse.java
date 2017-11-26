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
public class SurveyResponse {
  private Long surveyResponseId;

  private Long orgId;

  private Long userId;

  private Long surveyActivityId;

  private Long surveyItemId;

  private Integer surveyItemType;

  private Integer response;

  private String responseDetail;

  private Integer isSubmit;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
