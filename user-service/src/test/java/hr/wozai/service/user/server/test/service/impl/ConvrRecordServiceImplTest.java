// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.server.model.conversation.ConvrRecord;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.service.ConvrRecordService;
import hr.wozai.service.user.server.service.ConvrScheduleService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
public class ConvrRecordServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrRecordServiceImplTest.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  private ConvrRecordService convrRecordService;

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
  ConvrRecord convrRecord = null;
  ConvrSchedule convrSchedule = null;

  {
    convrRecord = new ConvrRecord();
    convrRecord.setOrgId(mockOrgId);
    convrRecord.setConvrScheduleId(mockConvrScheduleId);
    convrRecord.setConvrDate(convrDate);
    convrRecord.setTopicProgress(topicProgress);
    convrRecord.setTopicPlan(topicPlan);
    convrRecord.setTopicObstacle(topicObstacle);
    convrRecord.setTopicHelp(topicHelp);
    convrRecord.setTopicCareer(topicCareer);
    convrRecord.setTopicElse(topicElse);
    convrRecord.setCreatedUserId(mockUserId);

    convrSchedule = new ConvrSchedule();
    convrSchedule.setOrgId(mockOrgId);
    convrSchedule.setSourceUserId(mockSourceUserId);
    convrSchedule.setTargetUserId(mockTargetUserId);
    convrSchedule.setPeriodType(PeriodType.EVERY_WEEK.getCode());
    convrSchedule.setRemindDay(RemindDay.MONDAY.getCode());
    convrSchedule.setCreatedUserId(mockUserId);
  }

  @Before
  public void setup() {

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
    long convrRecordId = convrRecordService.addConvrRecord(convrRecord);

    // verify
    ConvrRecord addedCR = convrRecordService.getConvrRecord(mockOrgId, convrRecordId);
    Assert.assertEquals(topicProgress, addedCR.getTopicProgress());

  }

  /**
   * Case 02: invalid param
   * Test:
   *  addConvrRecord()
   *  getConvrRecord()
   *
   */
  @Test
  public void testAddConvrRecordCase02() {

    convrRecord.setConvrDate(null);
    thrown.expect(ServiceStatusException.class);
    long convrRecordId = convrRecordService.addConvrRecord(convrRecord);

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
    long convrRecordId = convrRecordService.addConvrRecord(convrRecord);

    // verify
    ConvrRecord addedCR = convrRecordService.getConvrRecord(mockOrgId, convrRecordId);
    String updatedTopicPlan = "updatedTopicPlan";
    addedCR.setTopicPlan(updatedTopicPlan);
    addedCR.setLastModifiedUserId(mockUserId);
    convrRecordService.updateConvrRecord(addedCR);
    ConvrRecord updatedCR = convrRecordService.getConvrRecord(mockOrgId, convrRecordId);
    Assert.assertEquals(updatedTopicPlan, updatedCR.getTopicPlan());

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
    long convrRecordId = convrRecordService.addConvrRecord(convrRecord);

    // verify
    ConvrRecord addedCR = convrRecordService.getConvrRecord(mockOrgId, convrRecordId);
    addedCR.setLastModifiedUserId(null);
    thrown.expect(ServiceStatusException.class);
    convrRecordService.updateConvrRecord(addedCR);

  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrRecordOfScheduleAsSourceUser()
   *  countAllConvrRecordIncludingSourceUserId()
   *
   */
  @Test
  public void testListAllConvrRecordIncludingSourceUserId01() {

    // prepare
    long firstScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);
    convrSchedule.setSourceUserId(mockTargetUserId);
    convrSchedule.setTargetUserId(mockSourceUserId);
    long secondScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);
    int count = 3;
    for (int i = 0; i < count; i++) {
      convrRecord.setConvrScheduleId(firstScheduleId);
      convrRecordService.addConvrRecord(convrRecord);
    }
    for (int i = 0; i < count; i++) {
      convrRecord.setConvrScheduleId(secondScheduleId);
      convrRecordService.addConvrRecord(convrRecord);
    }

    // verify
    List<ConvrRecord> addedCRs = convrRecordService
        .listAllConvrRecordIncludingSourceUserId(mockOrgId, mockSourceUserId, mockTargetUserId, 1, 20);
    int addedCount =
        convrRecordService.countAllConvrRecordIncludingSourceUserId(mockOrgId, mockSourceUserId, mockTargetUserId);
    Assert.assertEquals(count + count, addedCRs.size());
    Assert.assertEquals(count + count, addedCount);

  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrRecordByTargetUserId()
   *  countConvrRecordByTargetUserId()
   *
   */
  @Test
  public void testListConvrRecordByTargetUserIdCase01() {

    // prepare
    long addedCSId = convrScheduleService.addConvrSchedule(convrSchedule);
    convrRecord.setConvrScheduleId(addedCSId);
    int count = 0;
    for (int i = 0; i < count; i++) {
      convrRecordService.addConvrRecord(convrRecord);
    }

    // verify
    List<ConvrRecord> addedCRs = convrRecordService.listConvrRecordByTargetUserId(mockOrgId, mockTargetUserId, 1, 20);
    int addedCSCount = convrRecordService.countConvrRecordByTargetUserId(mockOrgId, mockTargetUserId);
    Assert.assertEquals(count, addedCRs.size());
    Assert.assertEquals(count, addedCSCount);
  }


}
