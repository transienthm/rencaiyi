// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.test.dao;

import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.feed.server.dao.ThumbupDao;
import hr.wozai.service.feed.server.model.Thumbup;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
public class ThumbupDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ThumbupDaoTest.class);

  @Autowired
  ThumbupDao thumbupDao;

  @Test
  public void testAll() {

    long userId = 4l;
    long feedId = 18l;
    long orgId = 54l;

    int isLiked = 1;
    int rel = thumbupDao.updateThumbup(orgId, userId, feedId, isLiked);
    LOGGER.info("insert rel=" + rel);

    boolean isThumbup = thumbupDao.isUserIdThumbupFeedId(orgId, userId, feedId);
    Assert.assertEquals(isThumbup, true);

    for(long i = 5; i<8; i++) {
      thumbupDao.updateThumbup(orgId, i, feedId, isLiked);
    }

    List<Thumbup> thumbups = thumbupDao.listThumbupUserIdsOfFeedId(orgId, feedId);
    Assert.assertEquals(thumbups.size(), 4);

    for(long i = 16; i<18; i++) {
      thumbupDao.updateThumbup(orgId, userId, i, isLiked);
    }

    isLiked = 0;
    thumbupDao.updateThumbup(orgId, userId, feedId, isLiked);
    isThumbup = thumbupDao.isUserIdThumbupFeedId(orgId, userId, feedId);
    Assert.assertEquals(isThumbup, false);

    List<Long> feedIds = new ArrayList<>();
    for(long i = 16; i<19; i++) {
      feedIds.add(i);
    }
    LOGGER.info("feedIsList size " + feedIds.size());

    List<Long> likedFeedIds = thumbupDao.filterUserLikedFeedIds(orgId, userId, feedIds);
    LOGGER.info("liked number " + likedFeedIds.size());
  }

}
