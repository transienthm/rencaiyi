// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.feed;

import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.feed.client.consts.FeedAndCommentConsts;
import hr.wozai.service.feed.client.dto.CommentDTO;
import hr.wozai.service.feed.client.dto.CommentListDTO;
import hr.wozai.service.feed.client.dto.FeedDTO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ControllerExceptionHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.feed.CommentInputVO;
import hr.wozai.service.api.vo.feed.CommentListVO;
import hr.wozai.service.api.vo.feed.CommentVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionObj;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-24
 */
@Controller("commentController")
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    private static final Integer DELETABLE = 1;
    private static final Integer UNDELETABLE = 0;

    @Autowired
    private PermissionUtil permissionUtil;

    @Autowired
    private FeedUtils feedUtils;

    @Autowired
    private FacadeFactory facadeFactory;

    @LogAround

    @RequestMapping(value = "/feeds/{feedId}/comments", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @BindingResultMonitor
    public Result<Object> createComment(
            @PathVariable(value = "feedId") String encryptedFeedId,
            @RequestBody @Valid CommentInputVO commentInputVO, BindingResult bindingResult,
            HttpServletRequest request
    ) throws Exception {

        Result<Object> result = new Result<>();
        try {
            Long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

            long orgId = AuthenticationInterceptor.orgId.get();
            long actorUserId = AuthenticationInterceptor.actorUserId.get();
            long adminUserId = AuthenticationInterceptor.adminUserId.get();

            //1. data preprocessing
            String content = commentInputVO.getContent();
            if (StringUtils.isNullOrEmpty(content)) {
                throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
            } else if (content.length() > FeedAndCommentConsts.MAX_LENGTH_OF_COMMENT) {
                throw new ServiceStatusException(ServiceStatus.FD_CONTENT_TOO_LONG);
            }

            List<IdVO> atUsersIdVO = commentInputVO.getAtUsers();
            if (null == atUsersIdVO) {
                atUsersIdVO = Collections.EMPTY_LIST;
            }
            List<Long> atUsersId = new ArrayList<>();
            for (IdVO idVO : atUsersIdVO) {
                atUsersId.add(idVO.getIdValue());
            }
            List<String> atUsers = feedUtils.getAtUsers(content, atUsersId, orgId, actorUserId, adminUserId);

            //2. permission check
            FeedDTO feedDTO = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
            if (ServiceStatus.COMMON_OK.getCode() != feedDTO.getServiceStatusDTO().getCode()) {
                throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedDTO.getServiceStatusDTO().getCode()));
            }

            boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, feedDTO.getFeedId(), feedDTO.getUserId(),
                    ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.READ.getCode());
            if (false == isPermitted) {
                throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
            }

            //3. do operation
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setOrgId(orgId);
            commentDTO.setFeedId(feedId);
            commentDTO.setUserId(actorUserId);
            commentDTO.setContent(content);
            commentDTO.setAtUsers(atUsers);
            commentDTO.setLastModifiedUserId(actorUserId);


            LongDTO commentIdDTO = facadeFactory.getFeedFacade().createComment(orgId, commentDTO, actorUserId, adminUserId);
            if (ServiceStatus.COMMON_OK.getCode() != commentIdDTO.getServiceStatusDTO().getCode()) {
                throw new ServiceStatusException(ServiceStatus.getEnumByCode(commentIdDTO.getServiceStatusDTO().getCode()));
            }

            IdVO idVO = new IdVO();
            idVO.setIdValue(commentIdDTO.getData());

            result.setCodeAndMsg(ServiceStatus.getEnumByCode(commentIdDTO.getServiceStatusDTO().getCode()));
            result.setData(idVO);

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("createComment()-fail", e);
        }

        return result;
    }

    @LogAround

    @RequestMapping(value = "/feeds/{feedId}/comments/{commentId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public Result<Object> deleteComment(
            @PathVariable(value = "feedId") String encryptedFeedId,
            @PathVariable(value = "commentId") String encryptedCommentId,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        Result<Object> result = new Result<>();

        try {

            long orgId = AuthenticationInterceptor.orgId.get();
            long actorUserId = AuthenticationInterceptor.actorUserId.get();
            long adminUserId = AuthenticationInterceptor.adminUserId.get();

            Long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));
            Long commentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedCommentId));

            CommentDTO commentDTO = facadeFactory.getFeedFacade().findComment(orgId, commentId, actorUserId, adminUserId);
            if (ServiceStatus.COMMON_OK.getCode() != commentDTO.getServiceStatusDTO().getCode()) {
                throw new ServiceStatusException(ServiceStatus.getEnumByCode(commentDTO.getServiceStatusDTO().getCode()));
            }
            if (feedId.longValue() != commentDTO.getFeedId().longValue()) {
                throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
            }

            boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, commentDTO.getFeedId(), commentDTO.getUserId(),
                    ResourceCode.NEWS_FEED_COMMENT.getResourceCode(), ActionCode.DELETE.getCode());
            if (false == isPermitted) {
                throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
            }

            VoidDTO remoteResult = facadeFactory.getFeedFacade().deleteComment(orgId, commentId, actorUserId, actorUserId, adminUserId);
            if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
                throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
            }
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

        } catch (Exception e) {
            LOGGER.error("deleteComment()-fail", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @LogAround

    @RequestMapping(value = "/feeds/{feedId}/comments", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listPageComment(
            @PathVariable(value = "feedId") String encryptedFeedId,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
        if (!isValid) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        Long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

        FeedDTO feedDTO = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
        if (ServiceStatus.COMMON_OK.getCode() != feedDTO.getServiceStatusDTO().getCode()) {
            throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedDTO.getServiceStatusDTO().getCode()));
        }

        boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, feedDTO.getFeedId(), feedDTO.getUserId(),
                ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.READ.getCode());
        if (false == isPermitted) {
            throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
        }

        CommentListDTO remoteResult = facadeFactory.getFeedFacade().listPageFeedComment(orgId, feedId, pageNumber, pageSize,
                actorUserId, adminUserId);
        if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
            throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
        }

        LongDTO amount = facadeFactory.getFeedFacade().countFeedComment(orgId, feedId, actorUserId, adminUserId);
        if (ServiceStatus.COMMON_OK.getCode() != amount.getServiceStatusDTO().getCode()) {
            throw new ServiceStatusException(ServiceStatus.getEnumByCode(amount.getServiceStatusDTO().getCode()));
        }

        List<CommentVO> commentVOs = fillCommentStatus(actorUserId, adminUserId, feedId, orgId, remoteResult.getCommentDTOList());

        CommentListVO commentListVO = new CommentListVO();
        commentListVO.setAmount(amount.getData());
        commentListVO.setComments(commentVOs);

        result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
        result.setData(commentListVO);

        return result;
    }

    @LogAround
    private List<CommentVO> fillCommentStatus(long actorUserId, long adminUserId, long feedId, long orgId,
                                              List<CommentDTO> commentDTOs) throws Exception {

        // comment deletable
        List<PermissionObj> permissionObjs = new ArrayList<>();
        for (int i = 0; i < commentDTOs.size(); i++) {
            CommentDTO commentDTO = commentDTOs.get(i);
            PermissionObj obj = new PermissionObj();
            obj.setId(commentDTO.getCommentId());
            obj.setOwnerId(commentDTO.getUserId());
            obj.setResourceType(ResourceType.PERSON.getCode());
            permissionObjs.add(obj);
        }
        permissionUtil.assignPermissionToObjList(orgId, actorUserId, permissionObjs,
                ResourceCode.NEWS_FEED_COMMENT.getResourceCode(), ActionCode.DELETE.getCode());

        Set<Long> userIdSet = new HashSet<>();
        for (CommentDTO commentDTO : commentDTOs) {
            userIdSet.add(commentDTO.getUserId());
        }
        List<Long> userIdList = new ArrayList<>(userIdSet);
        CoreUserProfileListDTO rpcUserList = facadeFactory.getUserProfileFacade()
                .listCoreUserProfile(orgId, userIdList, actorUserId, adminUserId);
        Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(rpcUserList.getCoreUserProfileDTOs())) {
            for (CoreUserProfileDTO coreUserProfileDTO : rpcUserList.getCoreUserProfileDTOs()) {
                coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
            }
        }

        List<CommentVO> commentVOs = new ArrayList<>();
        for (int i = 0; i < commentDTOs.size(); i++) {

            CommentDTO commentDTO = commentDTOs.get(i);
            CommentVO commentVO = new CommentVO();

            BeanHelper.copyPropertiesHandlingJSON(commentDTO, commentVO);

            if (coreUserProfileDTOMap.containsKey(commentDTO.getUserId())) {
                CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                BeanUtils.copyProperties(coreUserProfileDTOMap.get(commentDTO.getUserId()), coreUserProfileVO);
                commentVO.setCommentUser(coreUserProfileVO);
            }

            String content = commentDTO.getContent();
            List<String> atUsers = commentDTO.getAtUsers();
            if (null != atUsers) {
                List<CoreUserProfileVO> userProfileVOs = feedUtils.fillAtUsers(content, atUsers, orgId, actorUserId, adminUserId);
                commentVO.setAtUsers(userProfileVOs);
            }

            PermissionObj obj = permissionObjs.get(i);
            int deletable = obj.isHasPermission() ? DELETABLE : UNDELETABLE;
            commentVO.setIsDeletable(deletable);

            commentVOs.add(commentVO);
        }
        return commentVOs;
    }

}
