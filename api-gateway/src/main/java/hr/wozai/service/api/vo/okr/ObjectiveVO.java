package hr.wozai.service.api.vo.okr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/9
 */
@Data
@NoArgsConstructor
public class ObjectiveVO {
  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long objectiveId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long parentObjectiveId;

  private Integer type;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long ownerId;

  @StringLengthConstraint(lengthConstraint = 140)
  private String content;

  private Integer priority;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long objectivePeriodId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long createdUserId;

  private Long createdTime;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long lastModifiedUserId;

  private CoreUserProfileVO lastModifiedUserProfile;

  private Long lastModifiedTime;

  private List<DirectorVO> directorList;

  private List<KeyResultVO> keyResultList;

  private boolean editable;

  private String progress;

  private Integer isAutoCalc;

  private Integer progressMetricType;

  private String startingAmount;

  private String goalAmount;

  private String currentAmount;

  private String unit;

  private Long deadline;

  private Integer orderIndex;

  private Integer isPrivate;

  @StringLengthConstraint(lengthConstraint = 2000)
  private String comment;

  private String objectivePeriodOwnerName;

  private String objectivePeriodName;

  private String parentObjectiveName;

  private Integer regularRemindType;

  private String objectivePeriodOwnerJobTitleName;

  private Integer hasSubordinate;

  private Integer hasParent;
}
