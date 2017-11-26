package hr.wozai.service.nlp.server.model.labelcloud;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.alibaba.fastjson.JSONObject;

@Data
@NoArgsConstructor
public class LabelCloudModel {

  private Long cloudId;

  private Long orgId;

  private Long surveyActivityId;

  private Long surveyItemId;

  private Long cloudVersion;

  private JSONObject labelCloud;

  private Long createdTime;

  private Long lastModifiedTime;

  private Integer isDeleted;

}