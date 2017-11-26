package hr.wozai.service.api.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.orgteam.ProjectTeamVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/22
 */
@Data
@NoArgsConstructor
public class CoreUserProfileVO {

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long userProfileId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long userId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long profileTemplateId;

//  private Integer userStatus;

  /* fields */

  private String fullName;

  private String emailAddress;

  private String mobilePhone;

  private String avatarUrl;

  private TeamVO teamVO;

  private UserEmploymentVO userEmploymentVO;

  private String jobTitleName;

  private String reporterFullName;

  private Long enrollDate;

  private Integer isUserDeletable;

  private Long createdTime;

  private boolean isTeamAdmin;

  private boolean hasReportee;

  private List<String> roleNameList;

  private List<ProjectTeamVO> projectTeamVOs;
}