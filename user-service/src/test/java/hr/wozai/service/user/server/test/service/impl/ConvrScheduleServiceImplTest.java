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

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.service.ConvrScheduleService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
public class ConvrScheduleServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrScheduleServiceImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  private ConvrScheduleService convrScheduleService;

  long mockOrgId = 199999999L;
  long mockUserId = 299999999;
  long mockConvrScheduleId = 399999999L;
  long mockSourceUserId = 499999999L;
  long mockTargetUserId = 599999999L;
  ConvrSchedule convrSchedule = null;

  {
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
   *  insertConvrSchedule()
   *  findConvrScheduleByPrimaryKeyAndOrgId()
   *
   */
  @Test
  public void testInsertConvrScheduleCase01() {

    // prepare
    long addedScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);

    // verify
    ConvrSchedule addedCS = convrScheduleService.findConvrSchedule(addedScheduleId, mockOrgId);
    Assert.assertEquals(RemindDay.MONDAY.getCode(), addedCS.getRemindDay());

  }

  /**
   * Case 02: abnormal, not found
   * Test:
   *  insertConvrSchedule()
   *  findConvrScheduleByPrimaryKeyAndOrgId()
   *
   */
  @Test
  public void testInsertConvrScheduleCase02() {

    // prepare
    long addedScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);

    // verify
    thrown.expect(ServiceStatusException.class);
    ConvrSchedule addedCS = convrScheduleService.findConvrSchedule(addedScheduleId + 1000, mockOrgId);

  }

  /**
   * Case 03: abnormal, invalid param
   * Test:
   *  insertConvrSchedule()
   *  findConvrScheduleByPrimaryKeyAndOrgId()
   *
   */
  @Test
  public void testInsertConvrScheduleCase03() {

    // prepare
    convrSchedule.setPeriodType(null);
    thrown.expect(ServiceStatusException.class);
    convrScheduleService.addConvrSchedule(convrSchedule);

  }


  /**
   * Case 01: normal
   * Test:
   *  listConvrScheduleByPrimaryKeyAndOrgId()
   *
   */
  @Test
  public void testListConvrScheduleByPrimaryKeyAndOrgIdCase01() {

    // prepare
    int count = 5;
    List<Long> scheduleIds = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      scheduleIds.add(convrScheduleService.addConvrSchedule(convrSchedule));
    }

    // verify
    List<ConvrSchedule> addedCSs = convrScheduleService.listConvrScheduleByPrimaryKey(scheduleIds, mockOrgId);
    Assert.assertEquals(count, addedCSs.size());
  }

  /**
   * Case 01: normal case
   * Test:
   *  listConvrScheduleByOrgIdAndSourceUserId()
   *  countConvrScheduleByOrgIdAndSourceUserId()
   *
   */
  @Test
  public void testListConvrScheduleByOrgIdAndSourceUserIdCase01() {

    // prepare
    int count = 5;
    for (int i = 0; i < count; i++) {
      convrScheduleService.addConvrSchedule(convrSchedule);
    }

    // verify
    int addedCount = convrScheduleService.countConvrScheduleBySourceUserId(mockSourceUserId, mockOrgId);
    List<ConvrSchedule> addedCSs =
        convrScheduleService.listConvrScheduleBySourceUserId(mockSourceUserId, 1, 20, mockOrgId);
    LOGGER.info("XXXXX: size={}", addedCSs.size());
    Assert.assertEquals(addedCount, count);
    Assert.assertEquals(addedCSs.size(), count);

  }

  /**
   * Case 01: normal
   * Test:
   *  updateConvrScheduleByPrimaryKeyAndOrgIdSelective()
   *
   */
  @Test
  public void testUpdateConvrScheduleByPrimaryKeyAndOrgIdSelectiveCase01() {

    // prepare
    long addedScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);

    // verify
    ConvrSchedule addedCS = convrScheduleService.findConvrSchedule(addedScheduleId, mockOrgId);
    int updateRemindDay = RemindDay.SATURDAY.getCode();
    addedCS.setRemindDay(updateRemindDay);
    convrScheduleService.updateConvrSchedule(addedCS);
    ConvrSchedule updatedCS = convrScheduleService.findConvrSchedule(addedScheduleId, mockOrgId);
    Assert.assertEquals(updateRemindDay, updatedCS.getRemindDay().intValue());

  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrScheduleByTargetUserId()
   *
   */
  @Test
  public void testListConvrScheduleByTargetUserId() {

    // prepare
    int count = 0;
    for (int i = 0; i < count; i++) {
      convrScheduleService.addConvrSchedule(convrSchedule);
    }

    // verify
    List<ConvrSchedule> addedCSs =
        convrScheduleService.listAllConvrScheduleByTargetUserId(mockTargetUserId, mockOrgId);
    Assert.assertEquals(count, addedCSs.size());
  }

  /**
   * Case 01: normal
   * Test:
   *  listTargetUserIdBySourceUserId()
   */
  @Test
  public void testListTargetUserIdBySourceUserIdCase01() {

    // prepare
    int count = 0;
    for (int i = 0; i < count; i++) {
      mockTargetUserId += 1;
      convrSchedule.setTargetUserId(mockTargetUserId);
      convrScheduleService.addConvrSchedule(convrSchedule);
    }

    // verify
    List<Long> targetUserIds = convrScheduleService.listTargetUserIdBySourceUserId(mockSourceUserId, mockOrgId);
    Assert.assertEquals(count, targetUserIds.size());
  }

  /**
   * Case 02: abnormal, invalid params
   * Test:
   *  updateConvrScheduleByPrimaryKeyAndOrgIdSelective()
   *
   */
  @Test
  public void testUpdateConvrScheduleByPrimaryKeyAndOrgIdSelectiveCase02() {

    // prepare
    long addedScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);

    // verify
    ConvrSchedule addedCS = convrScheduleService.findConvrSchedule(addedScheduleId, mockOrgId);
    int updateRemindDay = RemindDay.SATURDAY.getCode();
    addedCS.setRemindDay(updateRemindDay);
    addedCS.setLastModifiedUserId(null);
    thrown.expect(ServiceStatusException.class);
    convrScheduleService.updateConvrSchedule(addedCS);

  }

}
