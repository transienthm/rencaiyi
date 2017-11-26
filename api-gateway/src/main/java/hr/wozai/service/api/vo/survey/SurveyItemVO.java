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
 * @created 16/11/29
 */
@Data
@NoArgsConstructor
public class SurveyItemVO {
  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  public Long surveyItemId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  public Long orgId;

  public Integer surveyItemType;

  public String question;

  public String description;

  public String lowLabel;

  public String highLabel;

  public Long startTime;

  public Long endTime;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long createdUserId;

  private Long createdTime;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private boolean deletable;

  private Integer status;

  private List<SurveyChartVO> surveyChartVOs;

  private List<SurveyResponseVO> surveyResponseVOs;

  private int totalNumber;

  private List<SurveyTagVO> surveyTagVOs;
}
