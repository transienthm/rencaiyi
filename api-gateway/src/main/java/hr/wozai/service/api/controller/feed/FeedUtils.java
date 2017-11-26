// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.feed;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.feed.FeedInputVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.feed.client.dto.RewardDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionObj;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@Component
public class FeedUtils {

  @Autowired
  private FacadeFactory facadeFactory;

  @Autowired
  private PermissionUtil permissionUtil;

  @LogAround
  public boolean isPermitted(long orgId, long userId, long objId, long objOwnerUserId,
                             String resource, int action) {
    List<PermissionObj> permissionObjs = new ArrayList<>();
    PermissionObj obj = new PermissionObj();
    obj.setId(objId);
    obj.setOwnerId(objOwnerUserId);
    obj.setResourceType(ResourceType.PERSON.getCode());
    permissionObjs.add(obj);
    permissionUtil.assignPermissionToObjList(orgId, userId, permissionObjs, resource, action);
    obj = permissionObjs.get(0);
    return obj.isHasPermission();
  }

  @LogAround
  public boolean isReward(FeedInputVO feedInputVO) {
    if (feedInputVO.getRewardeeIds() != null || feedInputVO.getRewardType() != null
            || feedInputVO.getRewardMedalId() != null) {
      return true;
    }
    return false;
  }

  @LogAround
  public RewardDTO getRewardDTOFromFeedInputVO(long orgId,FeedInputVO feedInputVO,long actorUserId) {
    List<IdVO> rewardMedalIds = feedInputVO.getRewardMedalId();
    RewardDTO rewardDTO = new RewardDTO();
    BeanUtils.copyProperties(feedInputVO, rewardDTO);
    rewardDTO.setOrgId(orgId);
    rewardDTO.setRewardMedalId(convertListIdVOToListLong(rewardMedalIds).get(0));
    rewardDTO.setUserId(actorUserId);
    rewardDTO.setLastModifiedUserId(actorUserId);
    rewardDTO.setRewardeeIds(convertListIdVOToListLong(feedInputVO.getRewardeeIds()));
    return rewardDTO;
  }

  @LogAround
  public List<String> getAtUsers(String content, List<Long> uploadAtUsers, long orgId, long actorUserId, long adminUserId) {

    List<String> atUsers = new ArrayList<>();

    if (null == content || content.isEmpty()) {
      return Collections.EMPTY_LIST;
    }

    if (null == uploadAtUsers) {
      return Collections.EMPTY_LIST;
    } else {
      //uploadAtUsers = uploadAtListString.stream().map(Long::valueOf).collect(Collectors.toList());
      List<String> regexAtUsers = getRegexAtUsers(content);
      if (!CollectionUtils.isEmpty(uploadAtUsers)) {
        CoreUserProfileListDTO rpcList = facadeFactory.getUserProfileFacade()
            .listCoreUserProfile(orgId, uploadAtUsers, actorUserId, adminUserId);
        if (!CollectionUtils.isEmpty(rpcList.getCoreUserProfileDTOs())) {
          for (CoreUserProfileDTO coreUserProfileDTO : rpcList.getCoreUserProfileDTOs()) {
            String name = coreUserProfileDTO.getFullName();
            if (regexAtUsers.contains(name)) {
              atUsers.add(coreUserProfileDTO.getUserId().toString());
            }
          }
        }
      }
    }
    return atUsers;
  }

  @LogAround
  public List<CoreUserProfileVO> fillAtUsers(
      String content, List<String> atList, long orgId, long actorUserId, long adminUserId) {

    List<CoreUserProfileVO> result = new ArrayList<>();
    List<String> regexAtUsers = getRegexAtUsers(content);
    List<Long> userIds = convertStringToLongInList(atList);
    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
    if (!CollectionUtils.isEmpty(userIds)) {
      CoreUserProfileListDTO rpcList = facadeFactory.getUserProfileFacade().listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
      if (!CollectionUtils.isEmpty(rpcList.getCoreUserProfileDTOs())) {
        for (CoreUserProfileDTO coreUserProfileDTO : rpcList.getCoreUserProfileDTOs()) {
          String fullName = coreUserProfileDTO.getFullName();
          if (regexAtUsers.contains(fullName)) {
            CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
            BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
            coreUserProfileVOs.add(coreUserProfileVO);
          }
        }
      }
    }
    return coreUserProfileVOs;
  }

  @LogAround
  private List<String> getRegexAtUsers(String content) {

    List<String> result = new ArrayList<>();

    //Define rules regex
    Pattern atPattern = Pattern.compile("@.*? |@.*?$");

    Matcher matcher = atPattern.matcher(content);
    while (matcher.find()) {
      String idStr = content.substring(matcher.start(), matcher.end());
      idStr = idStr.trim().substring(1);
      result.add(idStr);
    }

    return result;
  }

  public static void main(String[] args) throws Exception {

    String cz = "55";
    String lx = "60";

    String czStr = EncryptUtils.symmetricEncrypt(cz);
    String lxStr = EncryptUtils.symmetricEncrypt(lx);

    System.out.println("cz:" + czStr);
    System.out.println("lx:" + lxStr);

    List<String> tmp = Collections.EMPTY_LIST;
    System.out.println("tmp size:" + tmp.size());

    List<String> temp = new ArrayList<>();
    System.out.println("temp size:" + temp.size());

    String listString;

    List<String> str = (List<String>)tmp;
    listString = StringUtils.join(str, ",");
    System.out.println(listString.isEmpty());

    listString = "";
    List<String> result;
    if(null == listString)
      result = null;
    else if(listString.isEmpty())
      result = Collections.EMPTY_LIST;
    else
      result = Arrays.asList(listString.split(","));

    System.out.println("result size:" + result.size());

  }

  private List<Long> convertStringToLongInList(List<String> rawList) {
    if (!CollectionUtils.isEmpty(rawList)) {
      List<Long> idList = new ArrayList<>();
      for (int i = 0; i < rawList.size(); i++) {
        idList.add(Long.parseLong(rawList.get(i)));
      }
      return idList;
    }
    return null;
  }


  @LogAround
  private List<Long> convertListIdVOToListLong(List<IdVO> idVOs) {
    List<Long> result = new ArrayList<>();
    if (org.springframework.util.CollectionUtils.isEmpty(idVOs)) {
      return result;
    }
    for (IdVO idVO : idVOs) {
      result.add(idVO.getIdValue());
    }
    return result;
  }

  @LogAround
  private List<IdVO> convertListLongToListIdVO(List<Long> ids) {
    List<IdVO> result = new ArrayList<>();

    if (org.springframework.util.CollectionUtils.isEmpty(ids)) {
      return result;
    }
    for (Long l : ids) {
      IdVO idVO = new IdVO();
      idVO.setIdValue(l);
      result.add(idVO);
    }
    return result;
  }
}
