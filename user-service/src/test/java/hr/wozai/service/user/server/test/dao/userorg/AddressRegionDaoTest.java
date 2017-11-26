package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.AddressRegionDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.AddressRegion;
import hr.wozai.service.user.server.model.userorg.PickOption;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public class AddressRegionDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(AddressRegionDaoTest.class);

  @Autowired
  AddressRegionDao addressRegionDao;

  @Test
  public void testListAddressRegionByParentId() throws Exception {

    List<AddressRegion> addressRegions = null;
    int parentId = 0;

    addressRegions = addressRegionDao.listAddressRegionByParentId(parentId);
    Assert.assertEquals(1, addressRegions.size());

    parentId = 10;
    addressRegions = addressRegionDao.listAddressRegionByParentId(parentId);
    Assert.assertTrue(addressRegions.size() > 1);

  }

}
