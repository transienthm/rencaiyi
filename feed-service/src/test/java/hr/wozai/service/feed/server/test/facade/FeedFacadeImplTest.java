package hr.wozai.service.feed.server.test.facade;

import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.feed.server.test.utils.AopTargetUtils;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.feed.client.facade.FeedFacade;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-18
 */
public class FeedFacadeImplTest extends TestBase {

  long orgId = 99L;
  long teamId = 0L;

  long userId = 56L;
  long commentUserId = 57L;
  long rewardMedalId = 5l;

  @Autowired
  private FeedFacade feedFacade;

  @Autowired
  private IFeedService feedService;

  private FeedDTO feedDTO;

  private RewardDTO rewardDTO;

  CommentDTO commentDTO = new CommentDTO();

  List<String> atUsersStr = new ArrayList<>();

  @Mock
  IFeedService spyFeedService;



  @Before
  public void setup() throws Exception {

    String content = "Hello world!";


    atUsersStr.add("54");
    atUsersStr.add("55");

    List<String> images = new ArrayList<>();
    images.add("ali");
    images.add("aws");

    feedDTO = new FeedDTO();

    feedDTO.setOrgId(orgId);
    feedDTO.setUserId(userId);
    feedDTO.setTeamId(teamId);

    feedDTO.setContent(content);
    feedDTO.setAtUsers(atUsersStr);
    feedDTO.setImages(images);

    feedDTO.setLikeNumber(0L);
    feedDTO.setCommentNumber(0L);
    feedDTO.setLastModifiedUserId(userId);

    rewardDTO = new RewardDTO();
    rewardDTO.setRewardeeIds(Arrays.asList(userId));
    rewardDTO.setOrgId(orgId);
    rewardDTO.setRewardMedalId(rewardMedalId);

    rewardDTO.setLastModifiedUserId(userId);
    rewardDTO.setUserId(userId);
    rewardDTO.setFeedId(feedDTO.getFeedId());
    rewardDTO.setRewardType(0);


    commentDTO.setOrgId(orgId);
    commentDTO.setContent("My comment");
    commentDTO.setUserId(commentUserId);
    commentDTO.setLastModifiedUserId(commentUserId);

  }

  @Test
  public void testCreateFeed() throws Exception {

    LongDTO feedIdDTO = feedFacade.createFeed(orgId, feedDTO, 0L, 0L);

    Assert.assertEquals(feedIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    feedDTO.setRewardDTO(rewardDTO);

    feedIdDTO = feedFacade.createFeed(orgId, feedDTO, 0l, 0l);
    Assert.assertEquals(feedIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    long feedId = feedIdDTO.getData();

    FeedDTO feedDTO = feedFacade.findFeed(orgId, feedId, userId, userId);
    Assert.assertEquals(feedDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    LongDTO longDTO = feedFacade.countFeedComment(orgId, feedId, userId, userId);
    Assert.assertEquals(longDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    longDTO = feedFacade.countFeedOfOrgAndTeam(orgId, teamId, userId, userId);
    Assert.assertEquals(longDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    longDTO = feedFacade.countFeedComment(orgId, feedId, userId, userId);
    Assert.assertEquals(longDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    longDTO = feedFacade.countFeedOfTeam(orgId, teamId, userId, userId);
    Assert.assertEquals(longDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    BooleanDTO booleanDTO = feedFacade.isUserIdThumbupFeedId(orgId, userId, feedId, userId, userId);
    Assert.assertEquals(booleanDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    FeedListDTO feedListDTO = feedFacade.listFeedByFeedIds(orgId, Arrays.asList(feedId), userId, userId);
    Assert.assertEquals(feedListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    CommentListDTO commentListDTO = feedFacade.listFeedComment(orgId, feedId, userId, userId);
    Assert.assertEquals(commentListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    commentListDTO = feedFacade.listPageFeedComment(orgId, feedId, 1, 20, userId, userId);
    Assert.assertEquals(commentListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    feedListDTO = feedFacade.listPageFeedOfOrgAndTeam(orgId, teamId, 1, 20, userId, userId);
    Assert.assertEquals(feedListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    feedListDTO = feedFacade.listPageFeedOfTeam(orgId, teamId, 1, 20, userId, userId);
    Assert.assertEquals(feedListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ThumbupListDTO thumbupListDTO = feedFacade.listThumbupUserIdsOfFeedId(orgId, feedId, userId, userId);
    Assert.assertEquals(thumbupListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    LongListDTO longListDTO = feedFacade.filterUserLikedFeedIds(orgId, userId, Arrays.asList(feedId), userId, userId);
    Assert.assertEquals(longListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    commentDTO.setFeedId(feedId);
    LongDTO commentIdDTO = feedFacade.createComment(orgId, commentDTO, userId, userId);
    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    commentDTO = feedFacade.findComment(orgId, commentIdDTO.getData(), userId, userId);
    Assert.assertEquals(commentDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    VoidDTO voidDTO = feedFacade.thumbupFeed(orgId, userId, feedId, userId, userId);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    voidDTO = feedFacade.thumbupFeed(orgId, userId, feedId, userId + 1, userId);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    voidDTO = feedFacade.unThumbupFeed(orgId, userId, feedId, userId, userId);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    voidDTO = feedFacade.deleteComment(orgId, commentIdDTO.getData(), userId, userId, userId);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    voidDTO = feedFacade.deleteFeed(orgId, feedId, userId, userId, userId);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(feedFacade), "feedService", spyFeedService);

//    Mockito.when(spyFeedService.findFeed(
//            Mockito.anyLong(), Mockito.anyLong()
//    )).thenThrow(ServiceStatusException.class);
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).deleteFeed(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).createFeed(Mockito.anyObject());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).findFeed(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).countFeedComment(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).countFeedOfOrgAndTeam(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).countFeedOfTeam(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).isUserIdThumbupFeedId(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listFeedByFeedIds(Mockito.anyLong(), Mockito.anyList());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listFeedComment(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listFeedComment(Mockito.anyLong(), Mockito.anyLong());

    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listPageFeedComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listPageFeedOfOrgAndTeam(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listPageFeedOfTeam(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).thumbupFeed(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).unThumbupFeed(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listThumbupUserIdsOfFeedId(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).filterUserLikedFeedIds(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).deleteComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).findComment(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listFeedComment(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyFeedService).listPageFeedComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());


    longDTO = feedFacade.createFeed(orgId, feedDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), longDTO.getServiceStatusDTO().getCode());

    voidDTO = feedFacade.deleteFeed(orgId, feedId, userId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), voidDTO.getServiceStatusDTO().getCode());

    FeedDTO testFeedDTO = feedFacade.findFeed(orgId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), testFeedDTO.getServiceStatusDTO().getCode());


    longDTO = feedFacade.countFeedComment(orgId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), longDTO.getServiceStatusDTO().getCode());

    longDTO = feedFacade.countFeedOfOrgAndTeam(orgId, teamId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), longDTO.getServiceStatusDTO().getCode());

     longDTO =  feedFacade.countFeedOfTeam(orgId, teamId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), longDTO.getServiceStatusDTO().getCode());
     booleanDTO =  feedFacade.isUserIdThumbupFeedId(orgId, userId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), booleanDTO.getServiceStatusDTO().getCode());
     feedListDTO =  feedFacade.listFeedByFeedIds(orgId, Arrays.asList(feedId), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), feedListDTO.getServiceStatusDTO().getCode());
     commentListDTO =  feedFacade.listFeedComment(orgId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), commentListDTO.getServiceStatusDTO().getCode());
     commentListDTO =  feedFacade.listPageFeedComment(orgId, feedId, 1, 20, userId, userId);
    feedListDTO = feedFacade.listPageFeedOfOrgAndTeam(orgId, teamId, 1, 20, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), feedListDTO.getServiceStatusDTO().getCode());
     feedListDTO =  feedFacade.listPageFeedOfTeam(orgId, teamId, 1, 20, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), feedListDTO.getServiceStatusDTO().getCode());
    voidDTO = feedFacade.thumbupFeed(orgId, userId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), voidDTO.getServiceStatusDTO().getCode());
    voidDTO = feedFacade.unThumbupFeed(orgId, userId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), voidDTO.getServiceStatusDTO().getCode());
    thumbupListDTO = feedFacade.listThumbupUserIdsOfFeedId(orgId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), thumbupListDTO.getServiceStatusDTO().getCode());

    voidDTO = feedFacade.deleteComment(orgId, commentIdDTO.getData(), userId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), voidDTO.getServiceStatusDTO().getCode());

    commentDTO = feedFacade.findComment(orgId, commentIdDTO.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), commentDTO.getServiceStatusDTO().getCode());
    commentListDTO = feedFacade.listFeedComment(orgId, feedId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), commentListDTO.getServiceStatusDTO().getCode());

    commentListDTO = feedFacade.listPageFeedComment(orgId, feedId, 0, 1, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), commentListDTO.getServiceStatusDTO().getCode());

    Feed feed = new Feed();

    BeanUtils.copyProperties(feedDTO, feed);

    feedId = feedService.createFeed(feed);

    CommentDTO commentDTO = new CommentDTO();

    commentDTO.setOrgId(orgId);
    commentDTO.setFeedId(feedId);
    commentDTO.setContent("My comment");
    commentDTO.setUserId(commentUserId);
    commentDTO.setLastModifiedUserId(commentUserId);
    commentDTO.setAtUsers(atUsersStr);

//    commentIdDTO = feedFacade.createComment(orgId, commentDTO, 0L, 0L);
//    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

  }



}