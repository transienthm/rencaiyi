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
import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.server.dao.conversation.ConvrRecordDao;
import hr.wozai.service.user.server.dao.document.DocumentDao;
import hr.wozai.service.user.server.model.conversation.ConvrRecord;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class ConvrRecordDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrRecordDaoTest.class);

  @Autowired
  ConvrRecordDao convrRecordDao;

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
  }

  @Before
  public void setup() {

  }

  /**
   * Case 01: normal
   * insertConvrRecord() & findConvrRecordByPrimaryKeyAndOrgId()
   *
   */
  @Test
  public void testInsertConvrRecordCase01() {

    // prepare
    long convrRecordId = convrRecordDao.insertConvrRecord(convrRecord);

    // verify
    ConvrRecord addedCR = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, mockOrgId);
    Assert.assertEquals(topicProgress, addedCR.getTopicProgress());

  }


  /**
   * Case 01: normal
   * listConvrRecordByConvrScheduleIdAndOrgId()
   *  & countConvrRecordByConvrScheduleIdAndOrgId()
   *
   */
  @Test
  public void testListConvrRecordByConvrScheduleIdAndTargetUserIdAndOrgIdCase01() {

    // prepare
    long tmpConvrScheduleId = 899999999L;
    int sourceCount = 5;
    int targetCount = 3;
    for (int i = 0; i < sourceCount; i++) {
      convrRecordDao.insertConvrRecord(convrRecord);
    }
    for (int i = 0; i < targetCount; i++) {
      convrRecord.setConvrScheduleId(tmpConvrScheduleId);
      convrRecordDao.insertConvrRecord(convrRecord);
    }

    // verify
    List<Long> convrScheduleIds = new ArrayList<>();
    convrScheduleIds.add(mockConvrScheduleId);
    convrScheduleIds.add(tmpConvrScheduleId);
    List<ConvrRecord> addedCRs =
        convrRecordDao.listConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, 1, 20, mockOrgId);
    int addedCount = convrRecordDao.countConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, mockOrgId);
    Assert.assertEquals(sourceCount + targetCount, addedCRs.size());
    Assert.assertEquals(sourceCount + targetCount, addedCount);

  }

  /**
   * Case 01: normal
   *
   */
  @Test
  public void testUpdateConvrRecordByPrimaryKeyAndOrgIdSelectiveCase01() {

    // prepare
    long convrRecordId = convrRecordDao.insertConvrRecord(convrRecord);

    // verify
    String updatedProgress = "updatedProgress";
    ConvrRecord addedCR = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, mockOrgId);
    addedCR.setTopicProgress(updatedProgress);
    convrRecordDao.updateConvrRecordByPrimaryKeyAndOrgIdSelective(addedCR);

    ConvrRecord updatedCR = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, mockOrgId);
    Assert.assertEquals(updatedProgress, updatedCR.getTopicProgress());

  }

//  /**
//   * Case 01: normal
//   * Test:
//   *  listConvrRecordByTargetUserIdAndOrgId()
//   *  countConvrRecordByTargetUserIdAndOrgId()
//   *
//   */
//  @Test
//  public void testListConvrRecordByTargetUserIdAndOrgIdCase01() {
//
//    // prepare
//    long tmpConvrScheduleId = 899999999L;
//    int count = 5;
//    for (int i = 0; i < count; i++) {
//      convrRecordDao.insertConvrRecord(convrRecord);
//    }
//
//    // verify
//    List<ConvrRecord> addedCRs =
//        convrRecordDao.listConvrRecordByTargetUserIdAndOrgId(mockTargetUserId, 1, 20, mockOrgId);
//    int addedCount = convrRecordDao.countConvrRecordByTargetUserIdAndOrgId(mockTargetUserId, mockOrgId);
//    Assert.assertEquals(count, addedCRs.size());
//    Assert.assertEquals(count, addedCount);
//
//  }

}
