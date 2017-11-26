package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.document.dto.OssAvatarPutRequestDTO;
import hr.wozai.service.user.client.document.facade.AvatarFacade;
import hr.wozai.service.user.server.test.base.TestBase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AvatarFacadeImpl Tester.
 *
 * @author Zhe Chen
 * @version 1.0
 * @since <pre>Mar 17, 2016</pre>
 */
public class AvatarFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(AvatarFacadeImplTest.class);

  @Autowired
  AvatarFacade avatarFacade;

  private long orgId = 10L;
  private long userId = 20L;
  private long adminUserId = 30L;

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: addAvatar(long orgId, long userId, long adminUserId)
   * Case:   #1, normal
   */
  @Test
  public void testAddAvatarCase1() throws Exception {

    OssAvatarPutRequestDTO result = avatarFacade.addAvatar(orgId,  "1", "1", "2.2", userId, adminUserId);
    Assert.assertTrue(!StringUtils.isNullOrEmpty(result.getPresignedPutUrl()));

  }

  /**
   * Method: updateAvatar(String publicGetUrl, long orgId, long userId, long adminUserId)
   * Case:   #1, normal
   */
  @Test
  public void testUpdateAvatarCase1() throws Exception {

    OssAvatarPutRequestDTO addResult = avatarFacade.addAvatar(orgId, "1", "1", "2.2", userId, adminUserId);
    Assert.assertTrue(!StringUtils.isNullOrEmpty(addResult.getPresignedPutUrl()));

    OssAvatarPutRequestDTO updateResult =
        avatarFacade.updateAvatar(orgId, addResult.getPublicGetUrl(),  "1", "1", "2.2", userId, adminUserId);
    Assert.assertTrue(!StringUtils.isNullOrEmpty(updateResult.getPresignedPutUrl()));

  }

  /**
   * Method: updateAvatar(String publicGetUrl, long orgId, long userId, long adminUserId)
   * Case:   #2, abnormal
   */
  @Test
  public void testUpdateAvatarCase2() throws Exception {

    OssAvatarPutRequestDTO addResult = avatarFacade.addAvatar(orgId, "1", "1", "2.2", userId, adminUserId);
    Assert.assertTrue(!StringUtils.isNullOrEmpty(addResult.getPresignedPutUrl()));

    OssAvatarPutRequestDTO updateResult =
        avatarFacade.updateAvatar(orgId, "1", "1", "2.2", null, userId, adminUserId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), updateResult.getServiceStatusDTO().getCode());

  }


} 
