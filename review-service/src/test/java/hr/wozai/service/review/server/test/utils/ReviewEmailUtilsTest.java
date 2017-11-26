
package hr.wozai.service.review.server.test.utils;

import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.server.model.*;
import hr.wozai.service.review.server.service.*;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.review.server.utils.ReviewEmailUtils;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;



/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */

public class ReviewEmailUtilsTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewEmailUtilsTest.class);

  @Autowired
  private ReviewEmailUtils reviewEmailUtils;


  @Before
  public void setup() {

  }

  @Test
  public void test() {
    CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
    coreUserProfileDTO.setEmailAddress("lepujiu@sqian.com");
    coreUserProfileDTO.setFullName("lele");
    // reviewEmailUtils.sendEmailAboutReviewPeerBatchInvite("lele", "1", "1111", "lele", coreUserProfileDTO, 1);

    // reviewEmailUtils.sendEmailAboutReviewManagerInvite("lele", "1", "1111", "lele", coreUserProfileDTO, 1);
    // reviewEmailUtils.sendEmailAboutReviewPeerNotifyManager("lele1", "lele2", "1111", "org", coreUserProfileDTO, 1);
  }

}
