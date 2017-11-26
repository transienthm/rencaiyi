package hr.wozai.service.nlp.server.model.labelcloud;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SurveyResponseModel {

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
