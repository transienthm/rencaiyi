// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.thrift.facade;

import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.service.RewardService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.feed.client.facade.FeedFacade;
import hr.wozai.service.feed.server.helper.FacadeExceptionHelper;
import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Thumbup;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.feed.server.utils.FeedEmailUtils;
import hr.wozai.service.feed.server.utils.FeedMessageUtils;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;

import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;

import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.common.dto.RemindSettingDTO;
import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.client.common.facade.CommonToolFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
@Service("feedFacade")
public class FeedFacadeImpl implements FeedFacade {

    private static Logger LOGGER = LoggerFactory.getLogger(FeedFacadeImpl.class);

    @Autowired
    private IFeedService feedService;

    @Autowired
    private FeedEmailUtils feedEmailUtils;

    @Autowired
    private FeedMessageUtils feedMessageUtils;

    @Autowired
    private RewardService rewardService;

    @Autowired
    @Qualifier("commonToolFacadeProxy")
    private ThriftClientProxy commonToolFacadeProxy;

    private CommonToolFacade commonToolFacade;

    @PostConstruct
    public void init() throws Exception {
        commonToolFacade = (CommonToolFacade) commonToolFacadeProxy.getObject();
    }

    @LogAround
    @Override
    public LongDTO createFeed(long orgId, FeedDTO feedDTO, long actorUserId, long adminUserId) {
        LongDTO result = new LongDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Feed feed = new Feed();
            BeanHelper.copyPropertiesHandlingJSON(feedDTO, feed);
            RewardDTO rewardDTO = feedDTO.getRewardDTO();
            long feedId;
            if (rewardDTO == null) {
                feedId = feedService.createFeed(feed);
            } else {
                List<Reward> rewards = new ArrayList<>();
                for (long rewardeeId : rewardDTO.getRewardeeIds()) {
                    Reward reward = new Reward();
                    BeanUtils.copyProperties(rewardDTO, reward);
                    reward.setRewardeeId(rewardeeId);
                    rewards.add(reward);
                }
                feedId = feedService.createFeedAndReward(feed, rewards);
            }
            result.setData(feedId);

            long feedUserId = feed.getUserId();
            List<String> atUsers = feedDTO.getAtUsers();
            for (String atUser : atUsers) {
                Long atUserId = Long.parseLong(atUser);
                if (atUserId != actorUserId) {
                    try {
                        RemindSettingDTO remindSettingDTO = commonToolFacade
                                .getRemindSettingByUserIdAndRemindType(orgId, atUserId, RemindType.NEWSFEED_AT.getCode(),
                                        actorUserId, adminUserId);
                        if (remindSettingDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
                            LOGGER.error("getRemindSetting fail");
                            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
                        }
                        if (remindSettingDTO.getStatus() == 1) {
                            feedEmailUtils.sendFeedAtEmail(orgId, feedId, feedUserId, atUserId, actorUserId, adminUserId);
                        }

                        feedMessageUtils.sendFeedAtMessage(orgId, feedId, atUserId, actorUserId, adminUserId);
                    } catch (Exception e) {
                        LOGGER.error(e.toString());
                    }
                }
            }

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("createFeed()-error", e);
        }

        return result;
    }

    @LogAround
    @Override
    public VoidDTO deleteFeed(long orgId, long feedId, long userId,
                              long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            feedService.deleteFeed(orgId, feedId, userId);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        return result;

    }

    @LogAround
    @Override
    public FeedDTO findFeed(long orgId, long feedId, long actorUserId, long adminUserId) {

        FeedDTO result = new FeedDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Feed feed = feedService.findFeed(orgId, feedId);
            BeanHelper.copyPropertiesHandlingJSON(feed, result);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public LongDTO countFeedOfOrgAndTeam(long orgId, long teamId, long actorUserId, long adminUserId) {

        LongDTO result = new LongDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Long remoteResult = feedService.countFeedOfOrgAndTeam(orgId, teamId);
            result.setData(remoteResult);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public LongDTO countFeedOfTeam(long orgId, long teamId, long actorUserId, long adminUserId) {

        LongDTO result = new LongDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Long remoteResult = feedService.countFeedOfTeam(orgId, teamId);
            result.setData(remoteResult);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public FeedListDTO listPageFeedOfOrgAndTeam(long orgId, long teamId,
                                                int pageNumber, int pageSize,
                                                long actorUserId, long adminUserId) {
        FeedListDTO result = new FeedListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Feed> feedList = feedService.listPageFeedOfOrgAndTeam(orgId, teamId,
                    pageNumber, pageSize);

            List<Long> feedIds = new ArrayList<>();
            for (Feed feed : feedList) {
                feedIds.add(feed.getFeedId());
            }

            List<Reward> rewards = rewardService.listRewardsByFeedIds(orgId, feedIds);
            Map<Long, List<Reward>> map = new HashMap<>();
            for (Reward reward : rewards) {
                if (map.containsKey(reward.getFeedId())) {
                    List<Reward> list = map.get(reward.getFeedId());
                    list.add(reward);
                    map.put(reward.getFeedId(), list);
                } else {
                    List<Reward> list = new ArrayList<>();
                    list.add(reward);
                    map.put(reward.getFeedId(), list);
                }
            }

            List<FeedDTO> feedDTOList = new ArrayList<>();
            for (Feed feed : feedList) {
                FeedDTO feedDTO = new FeedDTO();
                BeanHelper.copyPropertiesHandlingJSON(feed, feedDTO);
                if (map.containsKey(feed.getFeedId())) {
                    List<Reward> rewardList = map.get(feed.getFeedId());
                    RewardDTO rewardDTO = new RewardDTO();
                    List<Long> rewardeeIds = new ArrayList<>();
                    if (rewardList == null) {
                        feedDTOList.add(feedDTO);
                        continue;
                    }
                    BeanUtils.copyProperties(rewardList.get(0), rewardDTO);
                    for (Reward reward : rewardList) {
                        rewardeeIds.add(reward.getRewardeeId());
                    }
                    rewardDTO.setRewardeeIds(rewardeeIds);
                    feedDTO.setRewardDTO(rewardDTO);
                }
                feedDTOList.add(feedDTO);
            }
            result.setFeedDTOList(feedDTOList);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        return result;
    }

    @LogAround
    @Override
    public FeedListDTO listPageFeedOfTeam(long orgId, long teamId,
                                          int pageNumber, int pageSize,
                                          long actorUserId, long adminUserId) {

        FeedListDTO result = new FeedListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Feed> feedList = feedService.listPageFeedOfTeam(orgId, teamId,
                    pageNumber, pageSize);

            List<FeedDTO> feedDTOList = new ArrayList<>();
            for (Feed feed : feedList) {
                FeedDTO feedDTO = new FeedDTO();
                BeanHelper.copyPropertiesHandlingJSON(feed, feedDTO);
                feedDTOList.add(feedDTO);
            }

            result.setFeedDTOList(feedDTOList);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public FeedListDTO listFeedByFeedIds(long orgId, List<Long> feedIds,
                                         long actorUserId, long adminUserId) {

        FeedListDTO result = new FeedListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Feed> feedList = feedService.listFeedByFeedIds(orgId, feedIds);

            List<FeedDTO> feedDTOList = new ArrayList<>();
            for (Feed feed : feedList) {
                FeedDTO feedDTO = new FeedDTO();
                BeanHelper.copyPropertiesHandlingJSON(feed, feedDTO);
                feedDTOList.add(feedDTO);
            }

            result.setFeedDTOList(feedDTOList);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }


    @LogAround
    @Override
    public BooleanDTO isUserIdThumbupFeedId(long orgId, long userId, long feedId,
                                            long actorUserId, long adminUserId) {
        BooleanDTO result = new BooleanDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            boolean remoteResult = feedService.isUserIdThumbupFeedId(orgId, userId, feedId);
            result.setData(remoteResult);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;

    }

    @LogAround
    @Override
    public VoidDTO thumbupFeed(long orgId, long userId, long feedId,
                               long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            feedService.thumbupFeed(orgId, userId, feedId);
            Feed feed = feedService.findFeed(orgId, feedId);
            if (feed.getUserId() != actorUserId) {
                try {
                    feedMessageUtils.sendFeedThumbupMessage(orgId, userId, feedId, actorUserId, adminUserId);
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public VoidDTO unThumbupFeed(long orgId, long userId, long feedId,
                                 long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            feedService.unThumbupFeed(orgId, userId, feedId);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public ThumbupListDTO listThumbupUserIdsOfFeedId(long orgId, long feedId,
                                                     long actorUserId, long adminUserId) {

        ThumbupListDTO result = new ThumbupListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Thumbup> thumbupList = feedService.listThumbupUserIdsOfFeedId(orgId, feedId);

            List<ThumbupDTO> thumbupDTOList = new ArrayList<>();
            for (Thumbup thumbup : thumbupList) {
                ThumbupDTO thumbupDTO = new ThumbupDTO();BeanHelper.copyPropertiesHandlingJSON(thumbup, thumbupDTO);thumbupDTOList.add(thumbupDTO);
            }

            result.setThumbupDTOList(thumbupDTOList);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public LongListDTO filterUserLikedFeedIds(long orgId, long userId, List<Long> feedIds,
                                              long actorUserId, long adminUserId) {

        LongListDTO result = new LongListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Long> rel = feedService.filterUserLikedFeedIds(orgId, userId, feedIds);
            result.setData(rel);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("filterUserLikedFeedIds()-error", e);
        }

        return result;
    }

    @LogAround
    @Override
    public LongDTO createComment(long orgId, CommentDTO commentDTO, long actorUserId, long adminUserId) {
        LongDTO result = new LongDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Comment comment = new Comment();
            BeanHelper.copyPropertiesHandlingJSON(commentDTO, comment);
            long rel = feedService.createComment(comment);
            result.setData(rel);

            long feedId = comment.getFeedId();
            Feed feed = feedService.findFeed(orgId, feedId);
            long feedUserId = feed.getUserId();
            long commentUserId = comment.getUserId();

            try {
                RemindSettingDTO remindSettingDTO = commonToolFacade.getRemindSettingByUserIdAndRemindType(orgId, feedUserId, RemindType.NEWSFEED_COMMENT.getCode(),
                        actorUserId, adminUserId);
                if (remindSettingDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
                    LOGGER.error("getRemindSetting fail");throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
                }
                if (remindSettingDTO.getStatus() == 1
                        && feedUserId != commentUserId) {
                    feedEmailUtils.sendFeedCommentEmail(orgId, feedId, feedUserId, commentUserId, actorUserId, adminUserId);
                    feedMessageUtils.sendFeedCommentMessage(orgId, feedId, commentUserId, actorUserId, adminUserId);
                }
                List<String> atUsers = commentDTO.getAtUsers();
                for (String atUser : atUsers) {
                    Long atUserId = Long.parseLong(atUser);
                    remindSettingDTO = commonToolFacade.getRemindSettingByUserIdAndRemindType(
                            orgId, atUserId, RemindType.NEWSFEED_COMMENT.getCode(), actorUserId, adminUserId);
                    if (remindSettingDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
                        LOGGER.error("getRemindSetting fail");throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
                    }
                    if (remindSettingDTO.getStatus() == 1
                            && commentUserId != atUserId) {
                        feedEmailUtils.sendFeedCommentAtEmail(orgId, feedId, commentUserId, atUserId, actorUserId, adminUserId);
                        feedMessageUtils.sendFeedCommentAtMessage(orgId, feedId, commentUserId, atUserId, actorUserId, adminUserId);
                    }
                }

            } catch (Exception e) {
                LOGGER.error(e.toString());
            }

        } catch (Exception e) {FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public VoidDTO deleteComment(long orgId, long commentId, long userId,
                                 long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            feedService.deleteComment(orgId, commentId, userId);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public CommentDTO findComment(long orgId, long commentId,
                                  long actorUserId, long adminUserId) {

        CommentDTO result = new CommentDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Comment comment = feedService.findComment(orgId, commentId);
            BeanHelper.copyPropertiesHandlingJSON(comment, result);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }


    @LogAround
    @Override
    public LongDTO countFeedComment(long orgId, long feedId,
                                    long actorUserId, long adminUserId) {

        LongDTO result = new LongDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Long remoteResult = feedService.countFeedComment(orgId, feedId);
            result.setData(remoteResult);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @LogAround
    @Override
    public CommentListDTO listFeedComment(long orgId, long feedId,
                                          long actorUserId, long adminUserId) {
        CommentListDTO result = new CommentListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Comment> commentList = feedService.listFeedComment(orgId, feedId);

            List<CommentDTO> commentDTOList = new ArrayList<>();
            for (Comment comment : commentList) {
                CommentDTO commentDTO = new CommentDTO();
                BeanHelper.copyPropertiesHandlingJSON(comment, commentDTO);
                commentDTOList.add(commentDTO);
            }

            result.setCommentDTOList(commentDTOList);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("listFeedComment()-error", e);
        }

        return result;
    }

    @LogAround
    @Override
    public CommentListDTO listPageFeedComment(long orgId, long feedId,
                                              int pageNumber, int pageSize,
                                              long actorUserId, long adminUserId) {
        CommentListDTO result = new CommentListDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<Comment> commentList = feedService.listPageFeedComment(orgId, feedId,
                    pageNumber, pageSize);

            List<CommentDTO> commentDTOList = new ArrayList<>();
            for (Comment comment : commentList) {
                CommentDTO commentDTO = new CommentDTO();
                BeanHelper.copyPropertiesHandlingJSON(comment, commentDTO);
                commentDTOList.add(commentDTO);
            }

            result.setCommentDTOList(commentDTOList);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("listPageFeedComment()-error", e);
        }

        return result;
    }

}
