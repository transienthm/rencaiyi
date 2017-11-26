package hr.wozai.service.feed.server.thrift.facade;

import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.feed.client.facade.FeedFacade;
import hr.wozai.service.feed.client.facade.RewardFacade;
import hr.wozai.service.feed.server.service.RewardMedalService;
import hr.wozai.service.feed.server.service.RewardQuotaSettingService;
import hr.wozai.service.feed.server.service.RewardService;
import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.feed.server.test.utils.AopTargetUtils;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/11/27.
 */
public class RewardFacadeImplTest extends TestBase {

    RewardQuotaSettingDTO rewardQuotaSettingDTO = new RewardQuotaSettingDTO();
    RewardMedalListDTO rewardMedalListDTO = new RewardMedalListDTO();
    RewardMedalDTO rewardMedalDTO = new RewardMedalDTO();
    List<RewardMedalDTO> rewardMedalDTOList = new ArrayList<>();
    long orgId = 999l;
    long userId = 99l;
    long teamId = 0L;
    int personalQuota = 5;
    int teamQuota = 5;
    long rewardMedalId = 5l;

    List<String> atUsersStr = new ArrayList<>();

    FeedDTO feedDTO = new FeedDTO();
    RewardDTO rewardDTO = new RewardDTO();

    @Autowired
    private RewardFacade rewardFacade;

    @Autowired
    private FeedFacade feedFacade;

    @Mock
    RewardMedalService spyRewardMedalService;

    @Mock
    RewardService spyRewardService;

    @Mock
    RewardQuotaSettingService spyRewardQuotaSettingService;


    @Before
    public void setUp() {

        String content = "Hello world!";

        atUsersStr.add("54");
        atUsersStr.add("55");

        List<String> images = new ArrayList<>();
        images.add("ali");
        images.add("aws");

        rewardQuotaSettingDTO.setOrgId(orgId);
        rewardQuotaSettingDTO.setPersonalQuota(personalQuota);
        rewardQuotaSettingDTO.setTeamQuota(teamQuota);
        rewardQuotaSettingDTO.setCreatedUserId(userId);
        rewardQuotaSettingDTO.setLastModifiedUserId(userId);
        rewardQuotaSettingDTO.setIsDeleted(0);

        rewardMedalDTO.setIsDeleted(0);
        rewardMedalDTO.setReceivedCount(1);
        rewardMedalDTO.setCreatedUserId(userId);
        rewardMedalDTO.setLastModifiedUserId(userId);
        rewardMedalDTO.setDescription("新勋章");
        rewardMedalDTO.setMedalName("new");
        rewardMedalDTO.setMedalIcon("123");
        rewardMedalDTO.setMedalType(0);
        rewardMedalDTO.setOrgId(orgId);
        rewardMedalDTOList.add(rewardMedalDTO);
        rewardMedalListDTO.setRewardMedalDTOList(rewardMedalDTOList);


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

    }

    @Test
    public void testAll() throws Exception {
        //rewardQuotaSetting
        VoidDTO voidDTO = rewardFacade.createRewardQuotaSetting(orgId, rewardQuotaSettingDTO, userId, userId);
        Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        rewardQuotaSettingDTO = rewardFacade.getRewardQuotaSettingByOrgId(orgId, userId, userId);
        Assert.assertEquals(new Integer(5), rewardQuotaSettingDTO.getTeamQuota());

        rewardQuotaSettingDTO.setTeamQuota(9);
        rewardFacade.updateRewardQuotaSetting(orgId, rewardQuotaSettingDTO, userId, userId);

        rewardQuotaSettingDTO = rewardFacade.getRewardQuotaSettingByOrgId(orgId, userId, userId);
        Assert.assertEquals(new Integer(9), rewardQuotaSettingDTO.getTeamQuota());

        RewardQuotaInfoDTO rewardQuotaInfoDTO = rewardFacade.findPersonalRewardQuotaInfo(orgId, userId, userId, userId);
        Assert.assertEquals(new Integer(0), rewardQuotaInfoDTO.getUsedPersonalQuota());

        //rewardMedal
        voidDTO = rewardFacade.batchCreateRewardMedal(orgId, rewardMedalListDTO, userId, userId);
        Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        RewardMedalListDTO rewardMedalListDTO = rewardFacade.listRewardMedalSetting(orgId, userId, userId);
        List<RewardMedalDTO> rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
        Assert.assertEquals("123", rewardMedalDTOs.get(0).getMedalIcon());

        long rewardMedalId = rewardMedalDTOs.get(0).getRewardMedalId();
        rewardMedalListDTO = rewardFacade.listRewardMedalSettingByRewardMedalIds(orgId, Arrays.asList(rewardMedalId), userId, userId);
        Assert.assertEquals("new", rewardMedalListDTO.getRewardMedalDTOList().get(0).getMedalName());

        RewardMedalListDTO rewardMedalListDTO1 = new RewardMedalListDTO();
        BeanUtils.copyProperties(rewardMedalListDTO, rewardMedalListDTO1);

        rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
        rewardMedalDTOs.get(0).setMedalName("update");
        RewardMedalListDTO update = new RewardMedalListDTO();
        update.setRewardMedalDTOList(rewardMedalDTOs);
        voidDTO = rewardFacade.batchUpdateRewardMedal(orgId, update, userId, userId);
        Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        rewardMedalListDTO = rewardFacade.listRewardMedalSetting(orgId, userId, userId);
        Assert.assertEquals("update", rewardMedalListDTO.getRewardMedalDTOList().get(0).getMedalName());

        voidDTO = rewardFacade.batchDeleteRewardMedal(orgId, rewardMedalListDTO, userId, userId);
        Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        rewardMedalListDTO = rewardFacade.listRewardMedalSetting(orgId, userId, userId);
        Assert.assertTrue(CollectionUtils.isEmpty(rewardMedalListDTO.getRewardMedalDTOList()));

        voidDTO = rewardFacade.batchCreateRewardMedal(orgId, rewardMedalListDTO, userId, userId);
        Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
        rewardMedalListDTO = rewardFacade.listRewardMedalSetting(orgId, userId, userId);
        Assert.assertTrue(CollectionUtils.isEmpty(rewardMedalListDTO.getRewardMedalDTOList()));

        RewardMedalListDTO rewardMedalListDTO2 = rewardFacade.listRewardMedalByRewardeeId(orgId, userId, userId, userId);
        Assert.assertTrue(CollectionUtils.isEmpty(rewardMedalListDTO2.getRewardMedalDTOList()));

        RewardListDTO rewardListDTO = rewardFacade.listRewardByOrgId(orgId, userId, userId);
        Assert.assertTrue(CollectionUtils.isEmpty(rewardListDTO.getRewardDTOList()));

        feedDTO.setRewardDTO(rewardDTO);

        LongDTO longDTO = feedFacade.createFeed(orgId, feedDTO, userId, userId);
        Assert.assertEquals(longDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        rewardListDTO = rewardFacade.listRewardByOrgId(orgId, userId, userId);
        Assert.assertEquals(rewardListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        rewardMedalListDTO = rewardFacade.listRewardMedalByRewardeeId(orgId, userId, userId, userId);
        Assert.assertEquals(rewardMedalListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

        voidDTO = rewardFacade.initRewardMedal(orgId, userId, userId);
        Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());


        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(rewardFacade), "rewardMedalService", spyRewardMedalService);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(rewardFacade), "rewardQuotaSettingService", spyRewardQuotaSettingService);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(rewardFacade), "rewardService", spyRewardService);

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardService).listRewardOfOrg(Mockito.anyLong());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardService).listRewardByRewardeeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardQuotaSettingService).createRewardSetting(Mockito.anyObject());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardQuotaSettingService).updateRewardQuota(Mockito.anyObject());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardQuotaSettingService).getRewardSettingByOrgId(Mockito.anyLong());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardMedalService).listRewardMedal(Mockito.anyLong());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardMedalService).listRewardMedalByRewardMedalIds(Mockito.anyLong(), Mockito.anyList());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardMedalService).batchCreateRewardMedal(Mockito.anyLong(), Mockito.anyList());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardMedalService).batchUpdateRewardMedal(Mockito.anyLong(), Mockito.anyList());
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyRewardMedalService).batchDeleteRewardMedal(Mockito.anyLong(), Mockito.anyList());

        rewardListDTO = rewardFacade.listRewardByOrgId(orgId, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), rewardListDTO.getServiceStatusDTO().getCode());

        VoidDTO result = rewardFacade.createRewardQuotaSetting(orgId, rewardQuotaSettingDTO, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
        result = rewardFacade.updateRewardQuotaSetting(orgId, rewardQuotaSettingDTO, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());

        RewardQuotaInfoDTO rewardQuotaInfoDTO1 = rewardFacade.findPersonalRewardQuotaInfo(orgId, userId, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), rewardQuotaInfoDTO1.getServiceStatusDTO().getCode());

        rewardQuotaSettingDTO = rewardFacade.getRewardQuotaSettingByOrgId(orgId, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), rewardQuotaSettingDTO.getServiceStatusDTO().getCode());

        RewardMedalListDTO rewardMedalListDTO3 = rewardFacade.listRewardMedalSetting(orgId, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), rewardMedalListDTO3.getServiceStatusDTO().getCode());

        rewardMedalListDTO2 = rewardFacade.listRewardMedalSettingByRewardMedalIds(orgId, new ArrayList<>(), userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), rewardMedalListDTO2.getServiceStatusDTO().getCode());

        result = rewardFacade.batchCreateRewardMedal(orgId, rewardMedalListDTO1, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());

        result = rewardFacade.batchUpdateRewardMedal(orgId, rewardMedalListDTO1, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());

        result = rewardFacade.batchDeleteRewardMedal(orgId, rewardMedalListDTO1, userId, userId);
        Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
    }

}