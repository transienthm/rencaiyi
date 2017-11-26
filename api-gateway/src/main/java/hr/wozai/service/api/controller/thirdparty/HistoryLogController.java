// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.thirdparty;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.historylog.HistoryLogListVO;
import hr.wozai.service.api.vo.historylog.HistoryLogVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileListVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.thirdparty.client.dto.HistoryLogDTO;
import hr.wozai.service.thirdparty.client.dto.HistoryLogListDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: wang bin
 * @Version: 1.0
 * @Created: 2016-04-11
 */
@Controller("historyLogController")
public class HistoryLogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryLogController.class);

    @Autowired
    FacadeFactory facadeFactory;

    @LogAround

    @RequestMapping(
            value = "/history-logs/user/{encryptedUserId}",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public Result<HistoryLogListVO> listHistoryLogByUserId(@PathVariable("encryptedUserId") String encryptedUserId,
                                                           @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNum,
                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
        Result<HistoryLogListVO> result = new Result<>();

        boolean isValid = PageUtils.isPageParamValid(pageNum, pageSize);
        if(!isValid) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        long userId = 0 ;

        try{
            userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
        }catch (Exception e){
            LOGGER.error("getHistoryLog()-error:invalid params");
            throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
        }

        try {
            HistoryLogListDTO listResult = facadeFactory.getHistoryLogFacade()
                    .listHistoryLogByUserId(authedOrgId,userId,authedAdminUserId,pageNum,pageSize);
            ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
            if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
                HistoryLogListVO historyLogListVO = new HistoryLogListVO();
                List<HistoryLogVO> historyLogVOs = new ArrayList<>();
                HistoryLogDTO historyLogDTO;
                List<TeamVO> teamVOs  = new ArrayList<>();
                //封装historyLogVOs
                for (int i = 0; i < listResult.getHistoryLogDTOs().size(); i++) {
                    historyLogDTO = listResult.getHistoryLogDTOs().get(i);
                    HistoryLogVO historyLogVO = new HistoryLogVO();
                    CoreUserProfileListVO coreUserProfileListVO = new CoreUserProfileListVO();
                    List<CoreUserProfileDTO> coreUserProfileDTOs;
                    CoreUserProfileVO coreUserProfileVO;
                    BeanUtils.copyProperties(historyLogDTO, historyLogVO);
                    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();

                    //封装coreUserProfileListVO
                    coreUserProfileDTOs = listCoreUserProfile(historyLogDTO, authedAdminUserId);
                    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
                        coreUserProfileVO = new CoreUserProfileVO();
                        BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
                        coreUserProfileVOs.add(coreUserProfileVO);
                    }
                    coreUserProfileListVO.setCoreUserProfileVOs(coreUserProfileVOs);
                    historyLogVO.setCoreUserProfileListVO(coreUserProfileListVO);


                    if (historyLogDTO.getLogType() == 5){
                        TeamVO oldTeamVO = new TeamVO();
                        TeamVO newTeamVO = new TeamVO();
                        //添加旧team
                        TeamDTO teamDTO = facadeFactory.getUserFacade()
                                .getTeam(authedOrgId,Long.parseLong(historyLogDTO.getPreValue()),authedActorUserId,authedAdminUserId);
                        BeanUtils.copyProperties(teamDTO,oldTeamVO);
                        teamVOs.add(oldTeamVO);
                        //添加新team
                        teamDTO = facadeFactory.getUserFacade()
                                .getTeam(authedOrgId, Long.parseLong(historyLogDTO.getCurValue()), authedActorUserId, authedAdminUserId);
                        BeanUtils.copyProperties(teamDTO,newTeamVO);
                        teamVOs.add(newTeamVO);
                        historyLogVO.setTeamVOs(teamVOs);
                    }
                    historyLogVOs.add(historyLogVO);
                }
                historyLogListVO.setHistoryLogVOs(historyLogVOs);
                result.setData(historyLogListVO);
            }
            result.setCodeAndMsg(rpcStatus);
        } catch (Exception e) {
            LOGGER.info("listHistoryLog()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /**
     * 根据actionType的不同,生成相应的List<CoreUserProfileDTO>
     *
     * @param historyLogDTO
     * @return
     */
    private List<CoreUserProfileDTO> listCoreUserProfile(HistoryLogDTO historyLogDTO, long adminUserId) {
        List<CoreUserProfileDTO> result = new ArrayList<>();
        CoreUserProfileDTO coreUserProfileDTO = null;
        long orgId = historyLogDTO.getOrgId();
        long actorUserId = 0l;
        if (null!=historyLogDTO.getActorUserId()){
            actorUserId = historyLogDTO.getActorUserId();
        }

        long preSuperiorUserId = 0l;
        long curSuperiorUserId = 0l;
        Integer logType = historyLogDTO.getLogType();

        switch (logType) {
            //汇报对象变更时
            case 4:
                preSuperiorUserId = Long.parseLong(historyLogDTO.getPreValue());
                curSuperiorUserId = Long.parseLong(historyLogDTO.getCurValue());
                List<Long> userIds = new ArrayList<>();
                userIds.add(preSuperiorUserId);
                userIds.add(curSuperiorUserId);
                userIds.add(actorUserId);
                for (Long userId : userIds) {
                    coreUserProfileDTO = facadeFactory.getUserProfileFacade()
                            .getCoreUserProfile(orgId, userId, actorUserId, adminUserId);
                    result.add(coreUserProfileDTO);
                }
                break;
            //活动评价时
            case 3:
                result = Collections.emptyList();
                break;
            //其他情况时
            default:
                coreUserProfileDTO = facadeFactory.getUserProfileFacade()
                        .getCoreUserProfile(orgId, actorUserId, actorUserId, adminUserId);
                result.add(coreUserProfileDTO);
                break;
        }
        return result;
    }
}
