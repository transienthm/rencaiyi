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

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.user.server.service.OssAvatarService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
public class OssServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OssServiceImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  private OssAvatarService ossAvatarService;

  // data
  String avatarKey = "AJSNDASDBANVAOHO";
  long effectiveTime = 1000 * 60 * 60 * 24;

  @Before
  public void setup() {
  }

  /**
   * Method: generatePresignedPutUrlFromAvatarKey():
   * Case:   #1, normal
   *
   */
  @Test
  public void testGeneratePresignedPutUrlFromAvatarKeyCase1() {
    ossAvatarService.generatePresignedPutUrlFromAvatarKey(avatarKey, effectiveTime);
  }

  /**
   * Method: generatePresignedPutUrlFromAvatarKey():
   * Case:   #2, abnormal, avatarKey == null
   *
   */
  @Test
  public void testGeneratePresignedPutUrlFromAvatarKeyCase2() {
    thrown.expect(ServiceStatusException.class);
    ossAvatarService.generatePresignedPutUrlFromAvatarKey(null, effectiveTime);
  }

  /**
   * Method: generatePresignedPutUrlFromAvatarKey():
   * Case:   #1, normal
   *
   */
  @Test
  public void testeneratePublicGetUrlFromAvatarKeyCase1() {
    ossAvatarService.generatePublicGetUrlFromAvatarKey(avatarKey);
  }

  /**
   * Method: generatePresignedPutUrlFromAvatarKey():
   * Case:   #2, abnormal, avatarKey == null
   *
   */
  @Test
  public void testeneratePublicGetUrlFromAvatarKeyCase2() {
//    thrown.expect(ServiceStatusException.class);
//    ossAvatarService.generatePublicGetUrlFromAvatarKey(avatarKey);
  }

  /**
   * Method: generatePresignedPutUrlFromPublicGetUrl()
   * Case:   #1, normal
   *
   */
  @Test
  public void testGeneratePresignedPutUrlFromPublicGetUrl() {
    String publicGetUrl = ossAvatarService.generatePublicGetUrlFromAvatarKey(avatarKey);
    ossAvatarService.generatePresignedPutUrlFromPublicGetUrl(publicGetUrl, effectiveTime);
  }

}
