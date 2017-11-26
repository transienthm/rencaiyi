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
public class SurveyItem {
  public Long surveyItemId;

  public Long orgId;

  public Integer surveyItemType;

  public String question;

  public String description;

  public String lowLabel;

  public String highLabel;

  public Long startTime;

  public Long endTime;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;
}
