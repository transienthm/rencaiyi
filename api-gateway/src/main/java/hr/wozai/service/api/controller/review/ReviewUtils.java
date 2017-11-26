// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.vo.review.ReviewCommentVO;
import hr.wozai.service.api.vo.review.ReviewInvitationVO;
import hr.wozai.service.api.vo.review.ReviewProjectSimpleVO;
import hr.wozai.service.api.vo.review.ReviewQuestionVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionObj;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Component("reviewUtilsNew")
public class ReviewUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewUtils.class);

  @Autowired
  private PermissionUtil permissionUtil;

  @Autowired
  private FacadeFactory facadeFactory;

  @LogAround
  public boolean isPermitted(long orgId, long userId,
                             String resource, int action) {
    List<PermissionObj> permissionObjs = new ArrayList<>();
    PermissionObj obj = new PermissionObj();
    obj.setId(0L);
    obj.setOwnerId(0L);
    obj.setResourceType(ResourceType.ORG.getCode());
    permissionObjs.add(obj);
    permissionUtil.assignPermissionToObjList(orgId, userId, permissionObjs, resource, action);
    obj = permissionObjs.get(0);
    return obj.isHasPermission();
  }


  @LogAround
  public List<ReviewProjectSimpleVO> getReviewProjectSimpleVO(List<ReviewProjectDTO> reviewProjectDTOs) {

    if(null == reviewProjectDTOs) {
      return Collections.EMPTY_LIST;
    }

    List<ReviewProjectSimpleVO> projects = new ArrayList<>();
    for (ReviewProjectDTO reviewProjectDTO : reviewProjectDTOs) {
      ReviewProjectSimpleVO reviewProjectSimpleVO = new ReviewProjectSimpleVO();
      BeanUtils.copyProperties(reviewProjectDTO, reviewProjectSimpleVO);
      projects.add(reviewProjectSimpleVO);
    }

    return projects;
  }

  @LogAround
  public List<ReviewInvitationVO> getReviewInvitationVO(List<ReviewInvitationDTO> reviewInvitationDTOs,
                                                        long actorUserId, long adminUserId) {

    if(null == reviewInvitationDTOs) {
      return Collections.EMPTY_LIST;
    }

    List<ReviewInvitationVO> reviewInvitationVOs = new ArrayList<>();
    for (ReviewInvitationDTO reviewInvitationDTO : reviewInvitationDTOs) {
      ReviewInvitationVO reviewInvitationVO = new ReviewInvitationVO();
      BeanUtils.copyProperties(reviewInvitationDTO, reviewInvitationVO);

      long orgId = reviewInvitationDTO.getOrgId();
      long templateId = reviewInvitationDTO.getTemplateId();
      ReviewTemplateDTO reviewTemplateDTO = facadeFactory.getReviewTemplateFacade().findReviewTemplate(orgId, templateId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != reviewTemplateDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(reviewTemplateDTO.getServiceStatusDTO().getCode());
      }
      BeanUtils.copyProperties(reviewTemplateDTO, reviewInvitationVO);

      long revieweeId = reviewInvitationDTO.getRevieweeId();
      CoreUserProfileVO revieweeCoreUserProfile = getCoreUserProfileVO(orgId, revieweeId, actorUserId, adminUserId);
      reviewInvitationVO.setRevieweeUserProfile(revieweeCoreUserProfile);

      reviewInvitationVOs.add(reviewInvitationVO);
    }

    return reviewInvitationVOs;
  }

  @LogAround
  public long getManagerUserId(long orgId, long userId, long actorUserId, long adminUserId) {
    CoreUserProfileDTO coreUserProfileDTO = facadeFactory.getUserFacade().getReportorByUserIdAndOrgId(orgId, userId, actorUserId, adminUserId);

    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(coreUserProfileDTO.getServiceStatusDTO().getCode());
    }
    return coreUserProfileDTO.getUserId();
  }


  @LogAround
  public CoreUserProfileVO getCoreUserProfileVO(long orgId, long userId,
                                                long actorUserId, long adminUserId) {
    CoreUserProfileDTO coreUserProfileDTO = facadeFactory.getUserProfileFacade().getCoreUserProfile(orgId, userId,
        actorUserId, adminUserId);

    CoreUserProfileVO result = new CoreUserProfileVO();
    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      LOGGER.info("getCoreUserProfileVO()-user is deleted:{}", userId);
      result.setUserId(userId);
      return result;
    } else {
      BeanUtils.copyProperties(coreUserProfileDTO, result);
      return result;
    }
  }

  @LogAround
  public List<ReviewQuestionVO> getReviewQuestionVOs(long orgId, List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs,
                                                     long actorUserId, long adminUserId) {
    List<ReviewQuestionVO> reviewQuestionVOs = new ArrayList<>();
    for(ReviewQuestionDetailDTO reviewQuestionDetailDTO: reviewQuestionDetailDTOs) {
      ReviewQuestionVO reviewQuestionVO = new ReviewQuestionVO();

      BeanUtils.copyProperties(reviewQuestionDetailDTO, reviewQuestionVO);

      ReviewCommentDTO revieweeCommentDTO = reviewQuestionDetailDTO.getRevieweeComment();
      if(null != revieweeCommentDTO) {
        ReviewCommentVO revieweeCommentVO = getReviewCommentVO(orgId, revieweeCommentDTO, actorUserId, adminUserId);
        reviewQuestionVO.setRevieweeComment(revieweeCommentVO);
      }

      List<ReviewCommentDTO> submittedReviewCommentDTO = reviewQuestionDetailDTO.getSubmittedComment();
      if(null == submittedReviewCommentDTO) {
        submittedReviewCommentDTO = Collections.EMPTY_LIST;
      }
      List<ReviewCommentVO> submittedReviewCommentVO = new ArrayList<>();
      for(ReviewCommentDTO reviewCommentDTO: submittedReviewCommentDTO) {
        ReviewCommentVO reviewCommentVO = getReviewCommentVO(orgId, reviewCommentDTO, actorUserId, adminUserId);
        submittedReviewCommentVO.add(reviewCommentVO);
      }
      reviewQuestionVO.setSubmittedComment(submittedReviewCommentVO);

      ReviewCommentDTO reviewerCommentDTO = reviewQuestionDetailDTO.getReviewerComment();
      if(null != reviewerCommentDTO) {
        ReviewCommentVO reviewerCommentVO = getReviewCommentVO(orgId, reviewerCommentDTO, actorUserId, adminUserId);
        reviewQuestionVO.setReviewerComment(reviewerCommentVO);
      }

      reviewQuestionVOs.add(reviewQuestionVO);
    }

    return reviewQuestionVOs;
  }

  @LogAround
  public ReviewCommentVO getReviewCommentVO(long orgId, ReviewCommentDTO reviewCommentDTO,
                                            long actorUserId, long adminUserId) {

    ReviewCommentVO reviewCommentVO = new ReviewCommentVO();
    BeanUtils.copyProperties(reviewCommentDTO, reviewCommentVO);

    Long reviewerId = reviewCommentDTO.getReviewerId();

    CoreUserProfileVO coreUserProfileVO = getCoreUserProfileVO(orgId, reviewerId, actorUserId, adminUserId);
    reviewCommentVO.setUserProfile(coreUserProfileVO);

    return reviewCommentVO;
  }

  @LogAround
  public Map<Long, CoreUserProfileDTO> getCoreUserProfileMapFromReviewActivityUserDTOList(long orgId,
          List<ReviewActivityUserDTO> source, long actorUserId, long adminUserId) {
    Set<Long> userIdSet = new HashSet<>();
    for(ReviewActivityUserDTO reviewActivityUserDTO: source) {
      //get userIdList
      userIdSet.add(reviewActivityUserDTO.getActivityDTO().getRevieweeId());
      if (reviewActivityUserDTO.getManagerInvitationDTO() != null) {
        userIdSet.add(reviewActivityUserDTO.getManagerInvitationDTO().getRevieweeId());
        userIdSet.add(reviewActivityUserDTO.getManagerInvitationDTO().getReviewerId());
      }
      if (!CollectionUtils.isEmpty(reviewActivityUserDTO.getStaffInvitationDTOs())) {
        for (ReviewInvitationDTO reviewInvitationDTO : reviewActivityUserDTO.getStaffInvitationDTOs()) {
          userIdSet.add(reviewInvitationDTO.getRevieweeId());
          userIdSet.add(reviewInvitationDTO.getReviewerId());
        }
      }
    }

    List<Long> userIdList = new ArrayList<>(userIdSet);
    CoreUserProfileListDTO rpcUserList = facadeFactory.getUserProfileFacade()
            .listCoreUserProfile(orgId, userIdList, actorUserId, adminUserId);
    Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(rpcUserList.getCoreUserProfileDTOs())) {
      for (CoreUserProfileDTO coreUserProfileDTO: rpcUserList.getCoreUserProfileDTOs()) {
        coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
      }
    }

    return  coreUserProfileDTOMap;
  }


  public static void main(String[] args) throws Exception {

    String templateName = "First season 2016";
    String startTimeString = "01/01/2016";
    String endTimeString = "01/03/2016";

    String selfString = "18/04/2016";
    String peerString = "25/04/2016";
    String publicString = "01/05/2016";

    JSONObject jsonObject = new JSONObject();

    jsonObject.put("templateName", templateName);

    java.text.DateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy");

    java.util.Date date = formatter.parse(startTimeString);
    Long startTime = date.getTime();
    jsonObject.put("startTime", startTime.toString());

    date = formatter.parse(endTimeString);
    Long endTime = date.getTime();
    jsonObject.put("endTime", endTime.toString());

    date = formatter.parse(selfString);
    Long selfReviewDeadline = date.getTime();
    jsonObject.put("selfReviewDeadline", selfReviewDeadline.toString());

    date = formatter.parse(peerString);
    Long peerReviewDeadline = date.getTime();
    jsonObject.put("peerReviewDeadline", peerReviewDeadline.toString());


    date = formatter.parse(publicString);
    Long publicDeadline = date.getTime();
    jsonObject.put("publicDeadline", publicDeadline.toString());

    List<String> questions = new ArrayList<>();
    questions.add("你的优势");
    questions.add("你的不足");
    jsonObject.put("questions", questions);

    System.out.println(jsonObject.toString());

    String gh = "64";
    String wxh = "57";
    String zhe = "56";
    String lpj = "54";

    String ghStr = EncryptUtils.symmetricEncrypt(gh);
    String wxhStr = EncryptUtils.symmetricEncrypt(wxh);
    String zheStr = EncryptUtils.symmetricEncrypt(zhe);
    String lpjStr = EncryptUtils.symmetricEncrypt(lpj);

    System.out.println(gh + ":" + ghStr);
    System.out.println(wxh + ":" + wxhStr);
    System.out.println(zhe + ":" + zheStr);
    System.out.println(lpj + ":" + lpjStr);

    /**
     64:63676e85e93ff186
     57:aa4ef0ae9e98ced7
     56:b2f9d8f70c751363
     54:7dbea3d77f049d92
     */

  }

}
