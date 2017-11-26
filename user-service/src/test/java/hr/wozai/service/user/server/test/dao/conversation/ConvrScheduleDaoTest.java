// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.conversation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.server.dao.conversation.ConvrRecordDao;
import hr.wozai.service.user.server.dao.conversation.ConvrScheduleDao;
import hr.wozai.service.user.server.model.conversation.ConvrRecord;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class ConvrScheduleDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrScheduleDaoTest.class);

  @Autowired
  ConvrScheduleDao convrScheduleDao;

  @Autowired
  ConvrRecordDao convrRecordDao;

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
  public void testInsertConvrSchedule() {

    // prepare
    long addedScheduleId = convrScheduleDao.insertConvrSchedule(convrSchedule);

    // verify
    ConvrSchedule addedCS = convrScheduleDao.findConvrScheduleByPrimaryKeyAndOrgId(addedScheduleId, mockOrgId);
    Assert.assertEquals(RemindDay.MONDAY.getCode(), addedCS.getRemindDay());

  }

  /**
   * Case 01: normal
   * Test:
   *  findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId()
   *
   */
  @Test
  public void testFindConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId() {

    // prepare
    convrScheduleDao.insertConvrSchedule(convrSchedule);

    // verify
    ConvrSchedule addedCS = convrScheduleDao.findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId(
        mockSourceUserId, mockTargetUserId, mockOrgId);
    Assert.assertEquals(RemindDay.MONDAY.getCode(), addedCS.getRemindDay());

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
      scheduleIds.add(convrScheduleDao.insertConvrSchedule(convrSchedule));
    }

    // verify
    List<ConvrSchedule> addedCSs = convrScheduleDao.listConvrScheduleByPrimaryKeyAndOrgId(scheduleIds, mockOrgId);
    Assert.assertEquals(count, addedCSs.size());
  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrScheduleStatByPrimaryKeyAndOrgId()
   *
   */
  @Test
  public void testListConvrScheduleStatByPrimaryKeyAndOrgIdCase01() {

    // prepare
    long addedCSId = convrScheduleDao.insertConvrSchedule(convrSchedule);
    int count = 5;
    ConvrRecord convrRecord = new ConvrRecord();
    convrRecord.setOrgId(mockOrgId);
    convrRecord.setConvrScheduleId(addedCSId);
    convrRecord.setCreatedUserId(mockUserId);
    for (int i = 0; i < count; i++) {
      convrRecord.setConvrDate(i + 0L);
      convrRecordDao.insertConvrRecord(convrRecord);
    }

    // verify
    List<ConvrSchedule> addedCS =
        convrScheduleDao.listConvrScheduleStatByPrimaryKeyAndOrgId(Arrays.asList(addedCSId), mockOrgId);
    Assert.assertEquals(count, addedCS.get(0).getConvrCount().intValue());
    Assert.assertEquals(count - 1, addedCS.get(0).getLastConvrDate().longValue());

  }

  /**
   * Case 01: normal case
   * Test:
   *  listConvrScheduleByOrgIdAndSourceUserId()
   *  countConvrScheduleByOrgIdAndSourceUserId()
   *
   */
  @Test
  public void testListConvrScheduleByOrgIdAndSourceUserId() {

    // prepare
    int count = 5;
    for (int i = 0; i < count; i++) {
      convrScheduleDao.insertConvrSchedule(convrSchedule);
    }

    // verify
    int addedCount = convrScheduleDao.countConvrScheduleByOrgIdAndSourceUserId(mockSourceUserId, mockOrgId);
    List<ConvrSchedule> addedCSs =
        convrScheduleDao.listConvrScheduleByOrgIdAndSourceUserId(mockSourceUserId, 1, 20, mockOrgId);
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
  public void testUpdateConvrScheduleByPrimaryKeyAndOrgIdSelective() {

    // prepare
    long addedScheduleId = convrScheduleDao.insertConvrSchedule(convrSchedule);

    // verify
    ConvrSchedule addedCS = convrScheduleDao.findConvrScheduleByPrimaryKeyAndOrgId(addedScheduleId, mockOrgId);
    int updateRemindDay = RemindDay.SATURDAY.getCode();
    addedCS.setRemindDay(updateRemindDay);
    convrScheduleDao.updateConvrScheduleByPrimaryKeyAndOrgIdSelective(addedCS);
    ConvrSchedule updatedCS = convrScheduleDao.findConvrScheduleByPrimaryKeyAndOrgId(addedScheduleId, mockOrgId);
    Assert.assertEquals(updateRemindDay, updatedCS.getRemindDay().intValue());

  }

  /**
   * Case 01: normal
   * Test:
   *  listConvrScheduleByTargetUserIdAndOrgId()
   *
   */
  @Test
  public void testListConvrScheduleByTargetUserIdAndOrgId() {

    // prepare
    int count = 0;
    for (int i = 0; i < count; i++) {
      convrScheduleDao.insertConvrSchedule(convrSchedule);
    }

    // verify
    List<ConvrSchedule> addedCSs =
        convrScheduleDao.listConvrScheduleByTargetUserIdAndOrgId(mockTargetUserId, mockOrgId);
    Assert.assertEquals(count, addedCSs.size());
  }

  /**
   * Case 01: normal
   * Test:
   *  listTargetUserIdsBySourceUserIdAndOrgId()
   *
   */
  @Test
  public void testListTargetUserIdsBySourceUserIdAndOrgIdCase01() {

    // prepare
    int count = 0;
    for (int i = 0; i < count; i++) {
      mockTargetUserId += 1;
      convrSchedule.setTargetUserId(mockTargetUserId);
      convrScheduleDao.insertConvrSchedule(convrSchedule);
    }

    // verify
    List<Long> targetUserIds =
        convrScheduleDao.listTargetUserIdsBySourceUserIdAndOrgId(mockSourceUserId, mockOrgId);
    Assert.assertEquals(count, targetUserIds.size());
  }




//  /**
//   * Case 01: normal
//   * insertConvrRecord() & findConvrRecordByPrimaryKeyAndOrgId()
//   *
//   */
//  @Test
//  public void testInsertConvrRecordCase01() {
//
//    // prepare
//    long convrRecordId = convrRecordDao.insertConvrRecord(convrRecord);
//
//    // verify
//    ConvrRecord addedCR = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, mockOrgId);
//    Assert.assertEquals(topicProgress, addedCR.getTopicProgress());
//
//  }

//  /**
//   * Case 01: normal
//   * listConvrRecordByConvrScheduleIdAndOrgId()
//   *  & countConvrRecordByConvrScheduleIdAndOrgId()
//   *
//   */
//  @Test
//  public void testListConvrRecordByConvrScheduleIdAndTargetUserIdAndOrgIdCase01() {
//
//    // prepare
//    long tmpConvrScheduleId = 899999999L;
//    int sourceCount = 5;
//    int targetCount = 3;
//    for (int i = 0; i < sourceCount; i++) {
//      convrRecordDao.insertConvrRecord(convrRecord);
//    }
//    for (int i = 0; i < targetCount; i++) {
//      convrRecord.setConvrScheduleId(tmpConvrScheduleId);
//      convrRecordDao.insertConvrRecord(convrRecord);
//    }
//
//    // verify
//    List<Long> convrScheduleIds = new ArrayList<>();
//    convrScheduleIds.add(mockConvrScheduleId);
//    convrScheduleIds.add(tmpConvrScheduleId);
//    List<ConvrRecord> addedCRs = convrRecordDao.listConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, mockOrgId);
//    int addedCount = convrRecordDao.countConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, mockOrgId);
//    Assert.assertEquals(sourceCount + targetCount, addedCRs.size());
//    Assert.assertEquals(sourceCount + targetCount, addedCount);
//
//  }
//
//  /**
//   * Case 01: normal
//   *
//   */
//  @Test
//  public void testUpdateConvrRecordByPrimaryKeyAndOrgIdSelectiveCase01() {
//
//    // prepare
//    long convrRecordId = convrRecordDao.insertConvrRecord(convrRecord);
//
//    // verify
//    String updatedProgress = "updatedProgress";
//    ConvrRecord addedCR = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, mockOrgId);
//    addedCR.setTopicProgress(updatedProgress);
//    convrRecordDao.updateConvrRecordByPrimaryKeyAndOrgIdSelective(addedCR);
//
//    ConvrRecord updatedCR = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, mockOrgId);
//    Assert.assertEquals(updatedProgress, updatedCR.getTopicProgress());
//
//  }

}
