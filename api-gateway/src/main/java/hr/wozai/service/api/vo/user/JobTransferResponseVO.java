// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
@Data
@NoArgsConstructor
public class JobTransferResponseVO {

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long jobTransferId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long userId;

  private SimpleUserProfileVO userSimpleUserProfileVO;

  private String transferType;

  private Long transferDate;

  private String description;

  private TeamVO beforeTeamVO;

  private SimpleUserProfileVO beforeReporterSimpleUserProfileVO;

  private OrgPickOptionVO beforeJobTitleOrgPickOptionVO;

  private OrgPickOptionVO beforeJobLevelOrgPickOptionVO;

  private TeamVO afterTeamVO;

  private SimpleUserProfileVO afterReporterSimpleUserProfileVO;

  private OrgPickOptionVO afterJobTitleOrgPickOptionVO;

  private OrgPickOptionVO afterJobLevelOrgPickOptionVO;

  private List<SimpleUserProfileVO> toNotifySimpleUserProfileVOs;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long createdUserId;

  private Long createdTime;

  @JsonSerialize(using = EncodeSerializer.class)
  @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
