package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/15
 */
@ThriftService
public interface UserFacade {

  @ThriftMethod
  public VoidDTO initPassword(long orgId, long userId, String passwordPlainText);

  @ThriftMethod
  public VoidDTO changePassword(long orgId, long userId, String currentPassword, String newPassword,
                                long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO resetPasswordWhenOnboarding(long orgId, long userId, String password);

  @ThriftMethod
  public VoidDTO resetPasswordWhenMissingPwd(long orgId, long userId, String password);

  /* @ThriftMethod
   public BooleanDTO deleteUserAccount(long userId, long actorUserId, long adminUserId);

   @ThriftMethod
   public BooleanDTO updateUserAccount(UserAccountDTO userAccountDTO, long actorUserId, long adminUserId);

   @ThriftMethod
   public UserAccountDTO getUserAccountByUserId(long userId, long actorUserId, long adminUserId);
 */
  @ThriftMethod
  public UserAccountDTO getUserAccountByEmail(String mobilePhone, long actorUserId, long adminUserId);

  /*@ThriftMethod
  public IdListDTO listAllUserByOrgId(long orgId, long actorUserId, long adminUserId);*/

  @ThriftMethod
  public LongDTO getOrgIdByUserId(long actorUserId, long adminUserId);

  @ThriftMethod
  BooleanDTO hasPassword(long userId);

  @ThriftMethod
  BooleanDTO verifyUserAccountWithPassword(long orgId, long userId, String password, long adminUserId);

  // ###########################user相关接口##############################################

  @ThriftMethod
  public BooleanDTO signUpWithEmail(String orgName, String email, String password);

  @ThriftMethod
  public VoidDTO loginWithEmail(String email, String password, boolean captchaSuccess);

  @ThriftMethod
  public UserInfoDTO getUserInfoByEmail(String email);

  // ###########################team相关接口##############################################
  //新增一个team
  @ThriftMethod
  public LongDTO addTeam(TeamDTO teamDTO, long actorUserId, long adminUserId);

  //获得一个team
  @ThriftMethod
  public TeamDTO getTeam(long orgId, long teamId, long actorUserId, long adminUserId);

  //获得一个team及它的子树
  /*@ThriftMethod
  public TeamListDTO listSubTeams(long orgId, long teamId, long actorUserId, long adminUserId);
*/
  @ThriftMethod
  public TeamListDTO listNextLevelTeams(long orgId, long teamId, long actorUserId, long adminUserId);

  @ThriftMethod
  public LongDTO updateTeam(TeamDTO teamDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public BooleanDTO deleteTeam(long orgId, long teamId, long actorUserId, long adminUserId);

  @ThriftMethod
  public TeamListDTO listSubordinateTeamsAndMembers(long orgId, long teamId, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO transferTeamsAndTeamMembers(long orgId, List<Long> teamIds, List<Long> userIds,
                                             long toTeamId, long actorUserId, long adminUserId);

  // 列出一个公司的全部team
  @ThriftMethod
  public TeamListDTO listAllTeams(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  public TeamListDTO listUpTeamLineByTeamId(long orgId, long teamId, long actorUserId, long adminUserId);

  @ThriftMethod
  TeamListDTO listTeamsByTeamIds(long orgId, List<Long> teamIds, long actorUserId, long adminUserId);


  /*@ThriftMethod
  public TeamListDTO listTeamsByTeamNameOrPinyinOrAbbreviation(long orgId, String keyword, int pageNumber,
                                                                   int pageSize, long actorUserId, long adminUserId);
*/

  // ###########################teamMember相关接口##############################################

  @ThriftMethod
  public BooleanDTO assignUsersToTeam(long orgId, List<Long> userIds, long teamId, long actorUserId, long adminUserId);

  /*@ThriftMethod
  public BooleanDTO batchDeleteTeamMembers(long orgId, List<Long> userIds, long teamId, long actorUserId, long adminUserId);
*/
  /*@ThriftMethod
  public BooleanDTO transferUsers(long orgId, List<Long> userIds, long fromTeamId, long toTeamId,
                                  long actorUserId, long adminUserId);
*/
  @ThriftMethod
  public UserNameListDTO listTeamMembers(long orgId, long teamId, String keyword, int pageNumber,
                                         int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  public IdListDTO listUnResignedAndHasReportorTeamMembersForReview(
          long orgId, List<Long> teamIds, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateTeamAdmin(long orgId, TeamMemberDTO teamMemberDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public TeamMemberDTO getTeamMemberByUserId(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  public TeamMemberListDTO listTeamMemberInfoByUserIds(long orgId, List<Long> userIds,
                                                       long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileListDTO getTeamMembersByTeamId(long orgId, long teamId, long actorUserId, long adminUserId);

  // ######################################ProjectTeam相关接口###########################################

  @ThriftMethod
  LongDTO addProjectTeam(long orgId, ProjectTeamDTO projectTeamDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO deleteProjectTeam(long orgId, long projectTeamId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateProjectTeam(long orgId, ProjectTeamDTO projectTeamDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  ProjectTeamDTO getProjectTeamByPrimaryKeyAndOrgId(long orgId, long projectTeamId, long actorUserId, long adminUserId);

  @ThriftMethod
  ProjectTeamListDTO listProjectTeamsByOrgIdAndTeamId(long orgId, long teamId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO batchInsertProjectTeamMember(
          long orgId, ProjectTeamMemberListDTO projectTeamMemberListDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO batchDeleteProjectTeamMember(long orgId, ProjectTeamMemberListDTO projectTeamMemberListDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  ProjectTeamMemberListDTO listProjectTeamMembersByOrgIdAndUserId(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileListDTO listProjectTeamMembersByOrgIdAndProjectTeamId(long orgId, long projectTeamId, long actorUserId, long adminUserId);

  // ###########################ReportLine相关接口##############################################

  @ThriftMethod
  public CoreUserProfileListDTO listReporteesByUserIdAndOrgId(long orgId, long userId,
                                                              long actorUserId, long adminUserId);

  @ThriftMethod
  public CoreUserProfileDTO getReportorByUserIdAndOrgId(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO batchInsertReportLine(long orgId, List<Long> userIds, long reportUserId,
                                       long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO batchUpdateReportLine(long orgId, List<Long> userIds,
                                       long newReportUserId, long actorUserId, long adminUserId);

  @ThriftMethod
  ReportLineInfoDTO getReportLineInfo(long orgId, long userId, boolean needTeamInfo, long actorUserId, long adminUserId);

  @ThriftMethod
  ReportLineListDTO listReportLineByUserIds(long orgId, List<Long> userIds, long actorUserId, long adminUserId);

  @ThriftMethod
  TeamListDTO fetchTeamAndUserProfiles(long orgId, List<Long> teamIds, List<Long> userIds);

  @ThriftMethod
  IdListDTO listUsersWhoHasReportorByOrgId(long orgId, long actorUserId, long adminUserId);
}