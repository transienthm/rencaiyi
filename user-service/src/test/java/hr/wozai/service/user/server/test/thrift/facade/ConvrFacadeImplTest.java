// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.thrift.facade;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrRecordDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrRecordListDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleDTO;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.client.conversation.facade.ConvrFacade;
import hr.wozai.service.user.server.service.ConvrScheduleService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ConvrFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrFacadeImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  private ConvrFacade convrFacade;

  @Autowired
      private ConvrScheduleService convrScheduleService;

  long mockOrgId = 199999999L;
  long mockUserId = 299999999;
  long mockConvrScheduleId = 399999999L;
  long mockSourceUserId = 499999999L;
  long mockTargetUserId = 599999999L;
  long convrDate = TimeUtils.getTimestampOfZeroOclockTodayOfInputTimestampInBeijingTime(
      TimeUtils.getNowTimestmapInMillis());
  String topicProgress = "progress";
  String topicPlan = "plan";
  String topicObstacle = "obstacle";
  String topicHelp = "help";
  String topicCareer = "career";
  String topicElse = "else";

  ConvrScheduleDTO convrScheduleDTO = null;
  ConvrRecordDTO convrRecordDTO = null;

  {
    convrScheduleDTO = new ConvrScheduleDTO();
    convrScheduleDTO.setOrgId(mockOrgId);
    convrScheduleDTO.setSourceUserId(mockSourceUserId);
    convrScheduleDTO.setTargetUserId(mockTargetUserId);
    convrScheduleDTO.setPeriodType(PeriodType.EVERY_WEEK.getCode());
    convrScheduleDTO.setRemindDay(RemindDay.MONDAY.getCode());
    convrScheduleDTO.setCreatedUserId(mockUserId);

    convrRecordDTO = new ConvrRecordDTO();
    convrRecordDTO.setOrgId(mockOrgId);
    convrRecordDTO.setConvrScheduleId(mockConvrScheduleId);
    convrRecordDTO.setConvrDate(convrDate);
    convrRecordDTO.setTopicProgress(topicProgress);
    convrRecordDTO.setTopicPlan(topicPlan);
    convrRecordDTO.setTopicObstacle(topicObstacle);
    convrRecordDTO.setTopicHelp(topicHelp);
    convrRecordDTO.setTopicCareer(topicCareer);
    convrRecordDTO.setTopicElse(topicElse);
    convrRecordDTO.setCreatedUserId(mockUserId);
  }

  @Before
  public void setup() {

  }

  /**
   * Case 01: normal
   * Test:
   *  addConvrSchedule()
   *  getConvrSchedule()
   *
   */
  @Test
  public void testAddConvrScheduleCase01() {

    // prepare
    LongDTO addedScheduleId = convrFacade.addConvrSchedule(mockOrgId, convrScheduleDTO, mockUserId, mockUserId);

    // verify
    Assert.assertNotNull(addedScheduleId.getData());
    ConvrScheduleDTO addedCSDTO =
        convrFacade.getConvrSchedule(mockOrgId, addedScheduleId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(RemindDay.MONDAY.getCode(), addedCSDTO.getRemindDay());

  }

  /**
   * Case 01: normal case
   * Test:
   *  listConvrScheduleBySourceUserId()
   *
   */
  @Test
  public void testListConvrScheduleBySourceUserIdCase01() {

    // TODO: make it right

    // prepare
//    int count = 5;
//    for (int i = 0; i < count; i++) {
//      convrFacade.addConvrSchedule(mockOrgId, convrScheduleDTO, mockUserId, mockUserId);
//    }
//
//    // verify
//    ConvrScheduleListDTO convrScheduleListDTO =
//        convrFacade.listConvrScheduleBySourceUserId(mockOrgId, mockSourceUserId, 1, 20, mockUserId, mockUserId);
//    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), convrScheduleListDTO.getServiceStatusDTO().getCode());
//    Assert.assertEquals(count, convrScheduleListDTO.getConvrScheduleDTOs().size());
//    Assert.assertEquals(count, convrScheduleListDTO.getTotalNumber().intValue());

  }

  /**
   * Case 01: normal
   * Test:
   *  updateConvrScheduleByPrimaryKeyAndOrgIdSelective()
   *
   */
  @Test
  public void testupdateConvrScheduleCase01() {

    // prepare
    LongDTO addedScheduleId = convrFacade.addConvrSchedule(mockOrgId, convrScheduleDTO, mockUserId, mockUserId);

    // verify
    Assert.assertNotNull(addedScheduleId.getData());
    ConvrScheduleDTO addedCSDTO =
        convrFacade.getConvrSchedule(mockOrgId, addedScheduleId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(RemindDay.MONDAY.getCode(), addedCSDTO.getRemindDay());
    int updatedRemindDay = RemindDay.WEDNESDAY.getCode();
    addedCSDTO.setRemindDay(updatedRemindDay);
    addedCSDTO.setLastModifiedUserId(mockUserId);
    convrFacade.updateConvrSchedule(mockOrgId, addedCSDTO, mockUserId, mockUserId);
    ConvrScheduleDTO updatedCSDTO =
        convrFacade.getConvrSchedule(mockOrgId, addedScheduleId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(updatedRemindDay, updatedCSDTO.getRemindDay().intValue());
  }

  /**
   * Case 01: normal
   * Test:
   *  addConvrRecord()
   *  getConvrRecord()
   *
   */
  @Test
  public void testAddConvrRecordCase01() {

    // prepare
    LongDTO crId = convrFacade.addConvrRecord(mockOrgId, convrRecordDTO, mockUserId, mockUserId);

    // verify
    Assert.assertNotNull(crId.getData());
    ConvrRecordDTO addedCR = convrFacade.getConvrRecord(mockOrgId, crId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(topicCareer, addedCR.getTopicCareer());

  }

  /**
   * Case 01: abnormal, invalid params
   * Test:
   *  addConvrRecord()
   *  getConvrRecord()
   *
   */
  @Test
  public void testAddConvrRecordCase02() {

    // prepare
    convrRecordDTO.setConvrDate(null);
    LongDTO crId = convrFacade.addConvrRecord(mockOrgId, convrRecordDTO, mockUserId, mockUserId);

    // verify
    Assert.assertEquals(ServiceStatus.COMMON_INVALID_PARAM.getCode(), crId.getServiceStatusDTO().getCode());

  }

  /**
   * Case 01: normal
   * Test:
   *  updateConvrRecord()
   *
   */
  @Test
  public void testUpdateConvrRecordCase01() {

    // prepare
    LongDTO crId = convrFacade.addConvrRecord(mockOrgId, convrRecordDTO, mockUserId, mockUserId);

    // verify
    Assert.assertNotNull(crId.getData());
    ConvrRecordDTO addedCR = convrFacade.getConvrRecord(mockOrgId, crId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(topicCareer, addedCR.getTopicCareer());
    String updatedTopicCareer = "updatedTopicCareer";
    addedCR.setTopicCareer(updatedTopicCareer);
    addedCR.setLastModifiedUserId(mockUserId);
    VoidDTO updateResult = convrFacade.updateConvrRecord(mockOrgId, addedCR, mockUserId, mockUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), updateResult.getServiceStatusDTO().getCode());
    ConvrRecordDTO updatedCR = convrFacade.getConvrRecord(mockOrgId, crId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(updatedTopicCareer, updatedCR.getTopicCareer());

  }

  /**
   * Case 02: abnormal, invalid params
   * Test:
   *  updateConvrRecord()
   *
   */
  @Test
  public void testUpdateConvrRecordCase02() {

    // prepare
    LongDTO crId = convrFacade.addConvrRecord(mockOrgId, convrRecordDTO, mockUserId, mockUserId);

    // verify
    Assert.assertNotNull(crId.getData());
    ConvrRecordDTO addedCR = convrFacade.getConvrRecord(mockOrgId, crId.getData(), mockUserId, mockUserId);
    Assert.assertEquals(topicCareer, addedCR.getTopicCareer());
    String updatedTopicCareer = "updatedTopicCareer";
    addedCR.setTopicCareer(updatedTopicCareer);
    addedCR.setConvrRecordId(null);
    VoidDTO updateDTO = convrFacade.updateConvrRecord(mockOrgId, addedCR, mockUserId, mockUserId);
    Assert.assertEquals(ServiceStatus.COMMON_INVALID_PARAM.getCode(), updateDTO.getServiceStatusDTO().getCode());

  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrRecordOfScheduleAsSourceUser()
   *
   */
  @Test
  public void testListAllConvrRecordIncludingSourceUserIdCase01() {

    // prepare
    long firstScheduleId = convrFacade.addConvrSchedule(mockOrgId, convrScheduleDTO, mockUserId, mockUserId).getData();
    int count = 3;
    for (int i = 0; i < count; i++) {
      convrRecordDTO.setConvrScheduleId(firstScheduleId);
      convrFacade.addConvrRecord(mockOrgId, convrRecordDTO, mockUserId, mockUserId);
    }

    // verify
    ConvrRecordListDTO addedCRs = convrFacade.listConvrRecordOfScheduleAsSourceUser(
        mockOrgId, firstScheduleId, 1, 20, mockUserId, mockUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), addedCRs.getServiceStatusDTO().getCode());
    Assert.assertEquals(count, addedCRs.getTotalNumber().intValue());
    Assert.assertEquals(count, addedCRs.getConvrRecordDTOs().size());

  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrRecordByTargetUserId()
   *
   */
  @Test
  public void testListConvrRecordByTargetUserIdCase01() {

    // prepare
    long firstScheduleId = convrFacade.addConvrSchedule(mockOrgId, convrScheduleDTO, mockUserId, mockUserId).getData();
    convrRecordDTO.setConvrScheduleId(firstScheduleId);
    int count = 3;
    for (int i = 0; i < count; i++) {
      convrFacade.addConvrRecord(mockOrgId, convrRecordDTO, mockUserId, mockUserId);
    }

    // verify
    ConvrRecordListDTO addedCRs = convrFacade.listConvrRecordByTargetUserId(
        mockOrgId, mockTargetUserId, 1, 20, mockUserId, mockUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), addedCRs.getServiceStatusDTO().getCode());
    Assert.assertEquals(count, addedCRs.getTotalNumber().intValue());
    Assert.assertEquals(count, addedCRs.getConvrRecordDTOs().size());
  }

  /**
   * Case 01: normal
   * Test:
   *  listTargetUserIdsOfSourceUser()
   *
   */
  @Test
  public void testListTargetUserIdsOfSourceUserCase01() {

    // prepare
    int count = 5;
    for (int i = 0; i < count; i++) {
      convrScheduleDTO.setTargetUserId(mockTargetUserId + i);
      convrFacade.addConvrSchedule(mockOrgId, convrScheduleDTO, mockUserId, mockUserId).getData();
    }

    // verify
    LongListDTO targetUserIds =
        convrFacade.listTargetUserIdsOfSourceUser(mockOrgId, mockUserId, mockUserId, mockUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), targetUserIds.getServiceStatusDTO().getCode());
    Assert.assertEquals(count, targetUserIds.getData().size());

  }


}
