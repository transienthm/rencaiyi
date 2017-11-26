package hr.wozai.service.api.controller.conversation;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ControllerExceptionHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.ConvrScheduleChartHelper;
import hr.wozai.service.api.vo.conversation.*;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.conversation.dto.ConvrScheduleChartDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleChartListDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrSourceChartDTO;
import hr.wozai.service.user.client.conversation.dto.ConvrSourceChartListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin on 2016/12/7.
 */
@Controller("convrChartController")
public class ConvrChartController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvrChartController.class);

    @Autowired
    FacadeFactory facadeFactory;

    @Autowired
    ConvrScheduleChartHelper convrScheduleChartHelper;

    @LogAround

    @RequestMapping(
            value = "/conversations/schedules/charts",
            method = RequestMethod.POST,
            produces = "application/json")
    @ResponseBody
    public Result<ConvrScheduleChartListVO> getConvrScheduleChart(
            @RequestBody ConvrScheduleChartInputVO convrScheduleChartInputVO
    ) {

        Result<ConvrScheduleChartListVO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        long convrScheduleId = -1;

        try {
            ConvrScheduleChartListVO convrScheduleChartListVO = new ConvrScheduleChartListVO();
            ConvrScheduleChartListDTO convrScheduleChartListDTO = facadeFactory.getConvrFacade().
                    listConvrScheduleChartByOrgId(authedOrgId, convrScheduleChartInputVO.getPeriod(),
                            authedActorUserId, authedAdminUserId);
            List<ConvrScheduleChartDTO> convrScheduleChartDTOs = convrScheduleChartListDTO.getConvrScheduleChartDTOList();
            List<ConvrScheduleChartVO> convrScheduleChartVOs = new ArrayList<>();
            if (!CollectionUtils.isEmpty(convrScheduleChartDTOs)) {
                for (ConvrScheduleChartDTO convrScheduleChartDTO : convrScheduleChartDTOs) {
                    ConvrScheduleChartVO convrScheduleChartVO = new ConvrScheduleChartVO();
                    BeanUtils.copyProperties(convrScheduleChartDTO, convrScheduleChartVO);
                    convrScheduleChartVOs.add(convrScheduleChartVO);
                }
            }
            convrScheduleChartListVO.setConvrScheduleChartVOList(convrScheduleChartVOs);

            ConvrScheduleChartListVO afterHandle = convrScheduleChartHelper.
                    convertConvrScheduleChartListVOByPeriodType(convrScheduleChartInputVO, convrScheduleChartListVO);

            result.setData(afterHandle);
        } catch (Exception e) {
            LOGGER.info("getConvrSchedule()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
        return result;
    }



    @LogAround

    @RequestMapping(
            value = "/conversations/schedules/charts/past30Days",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public Result<ConvrScheduleChartVO> getConvrScheduleChartInAMonth() {
        Result<ConvrScheduleChartVO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        long convrScheduleId = -1;

        try {
            ConvrScheduleChartDTO convrScheduleChartDTO = facadeFactory.getConvrFacade().getConvrScheduleChartInAMonth(authedOrgId, authedActorUserId, authedAdminUserId);
            ConvrScheduleChartVO convrScheduleChartVO = new ConvrScheduleChartVO();
            BeanUtils.copyProperties(convrScheduleChartDTO, convrScheduleChartVO);
            result.setData(convrScheduleChartVO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(convrScheduleChartDTO.getServiceStatusDTO().getCode()));
        } catch (Exception e) {
            LOGGER.info("getConvrScheduleChartInAMonth()", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return result;
    }


    @LogAround

    @RequestMapping(
            value = "/conversations/schedules/charts/sourceUsers",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public Result<ConvrSourceChartListVO> ListConvrSourceChart(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        Result<ConvrSourceChartListVO> result = new Result<>();
        long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
        long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
        long authedOrgId = AuthenticationInterceptor.orgId.get();
        ConvrSourceChartListVO convrSourceChartListVO = new ConvrSourceChartListVO();

        try {
            ConvrSourceChartListDTO convrSourceChartListDTO = facadeFactory.getConvrFacade().listConvrSourceChartListDTO(authedOrgId, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
            List<ConvrSourceChartDTO> convrSourceChartDTOs = convrSourceChartListDTO.getConvrSourceChartDTOs();
            List<ConvrSourceChartVO> convrSourceChartVOs = new ArrayList<>();

            for (ConvrSourceChartDTO convrSourceChartDTO : convrSourceChartDTOs) {
                ConvrSourceChartVO convrSourceChartVO = new ConvrSourceChartVO();
                BeanUtils.copyProperties(convrSourceChartDTO, convrSourceChartVO);
                CoreUserProfileDTO coreUserProfileDTO = convrSourceChartDTO.getSourceUser();
                CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
                convrSourceChartVO.setSourceUser(coreUserProfileVO);
                convrSourceChartVOs.add(convrSourceChartVO);
            }
            convrSourceChartListVO.setConvrSourceChartVOList(convrSourceChartVOs);
            convrSourceChartListVO.setTotalNumber(convrSourceChartListDTO.getTotalNumber());
            result.setData(convrSourceChartListVO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(convrSourceChartListDTO.getServiceStatusDTO().getCode()));
        } catch (Exception e) {
            LOGGER.info("ListConvrSourceChart()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
