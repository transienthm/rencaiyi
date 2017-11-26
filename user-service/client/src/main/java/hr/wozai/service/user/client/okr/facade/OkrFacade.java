package hr.wozai.service.user.client.okr.facade;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.common.dto.RemindSettingListDTO;
import hr.wozai.service.user.client.okr.dto.*;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftService
public interface OkrFacade {
  /**
   * 创建okr新周期
   * @return
   */
  @ThriftMethod
  public LongDTO createObjectivePeriod(ObjectivePeriodDTO objectivePeriodDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO deleteObjectivePeriod(long orgId, long objectivePeriodId, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO updateObjectivePeriod(ObjectivePeriodDTO objectivePeriodDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public ObjectivePeriodListDTO listObjectivePeriod(long orgId, int type, long ownerId, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectivePeriodDTO getObjectivePeriod(long orgId, long objectivePeriodId, long actorUserId, long adminUserId);
  //------------------目标操作--------------------------------

  @ThriftMethod
  public LongDTO createObjective(ObjectiveDTO objectiveDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO updateObjective(ObjectiveDTO objectiveDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO deleteObjective(long orgId, long objectiveId, long actorUserId, long adminUserId);

  @ThriftMethod
  public ObjectiveDTO getObjective(long orgId, long objectiveId, long actorUserId, long adminUserId);

  @ThriftMethod
  public ObjectiveListDTO listObjective(long orgId, int type, long ownerId, long objectivePeriodId, boolean aboutMe,
                                        int progressStatus, int orderBy, long actorUserId, long adminUserId);
  @ThriftMethod
  public ObjectiveListDTO listObjectivesByObjectiveIds(long orgId, List<Long> objectiveIds, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectiveListDTO searchObjectiveByKeywordInOrder(long orgId, long objectiveId, String keyword, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO moveObjective(long orgId, long objectiveId, int targetOrderIndex, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectiveListDTO listAncesterObjectives(long orgId, long objectiveId, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectiveListDTO listFirstLevelSubordinateObjectives(long orgId, long objectiveId, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectiveTreeDTO getBirdViewByObjectiveId(long orgId, long objectiveId, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectiveListDTO listObjectivesByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline);

  @ThriftMethod
  ObjectiveListDTO filterObjectives(
          long orgId, int priority, int progressStatus, boolean aboutMe,
          int orderItem, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  //------------------Key Result操作-------------------------

  @ThriftMethod
  public LongDTO createKeyResult(KeyResultDTO keyResultDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO updadteKeyResult(KeyResultDTO keyResultDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  public VoidDTO deleteKeyResult(long orgId, long keyResultId, long actorUserId, long adminUserId);

  @ThriftMethod
  public KeyResultDTO getKeyResult(long orgId, long keyResultId, long actorUserId, long adminUserId);


  @ThriftMethod
  public DirectorListDTO listObjectiveOrKeyResultDirector(long orgId, int type, long objectId,
                                                          long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectivePeriodDTO getObjectivePeriodWithObjectiveId(long orgId, long objectiveId, long actorUserId, long adminUserId);

  @ThriftMethod
  ObjectivePeriodDTO getObjectivePeriodWithKeyResultId(long orgId, long keyResultId, long actorUserId, long adminUserId);

  // ------------okr comment and okr update log
  @ThriftMethod
  LongDTO addOkrComment(long orgId, OkrCommentDTO okrCommentDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateOkrComment(long orgId, long okrCommentId, String content, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO deleteOkrComment(long orgId, long okrCommentId, long actorUserId, long adminUserId);

  @ThriftMethod
  OkrCommentListDTO listOkrComment(
          long orgId, long objectiveId, long keyResultId, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  // -------------okr remind setting
  @ThriftMethod
  OkrRemindSettingListDTO listOkrRemindSettingsByOrgId(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO batchUpdateOkrRemindSettings(
          long orgId, List<OkrRemindSettingDTO> okrRemindSettingDTOs, long actorUserId, long adminUserId);

}
