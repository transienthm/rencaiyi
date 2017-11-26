package hr.wozai.service.api.vo.survey;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/12/1
 */
@Data
@NoArgsConstructor
public class SurveyActivityVO {
  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long surveyActivityId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  private Long createdTime;

  private List<SurveyResponseVO> surveyResponseVOs;

  private List<SurveyItemVO> surveyItemVOs;

  private String averageScore;

  private String percentage;

  private int totalResponseNumber;

  private List<SurveyTagVO> surveyTagVOs;
}
