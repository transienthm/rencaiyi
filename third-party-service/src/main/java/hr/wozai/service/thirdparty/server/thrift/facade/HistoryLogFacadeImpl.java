package hr.wozai.service.thirdparty.server.thrift.facade;


import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.client.facade.HistoryLogFacade;
import hr.wozai.service.thirdparty.client.enums.HistoryLogTemplate;
import hr.wozai.service.thirdparty.server.helper.FacadeExceptionHelper;
import hr.wozai.service.thirdparty.server.model.HistoryLog;
import hr.wozai.service.thirdparty.server.service.HistoryLogService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.facade.HistoryLogFacade;
import hr.wozai.service.thirdparty.client.dto.HistoryLogDTO;
import hr.wozai.service.thirdparty.client.dto.HistoryLogListDTO;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangbin on 16/4/25.
 */
@Service("historyLogFacade")
public class HistoryLogFacadeImpl implements HistoryLogFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryLogFacadeImpl.class);

    @Autowired
    HistoryLogService historyLogService;

    @Override
    @LogAround
    public LongDTO addHistoryLog(long orgId, long userId, long actorUserId, long adminUserId, String preValue, String curValue, Integer logType) {

        LongDTO result = new LongDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            HistoryLog historyLog = new HistoryLog();
            /**
             * orgId userId actorUserId logType createdTime createdUserId content preValue curValue isDeleted
             */
            //orgId
            historyLog.setOrgId(orgId);
            //userId
            historyLog.setUserId(userId);
            //actorUserId
            historyLog.setActorUserId(actorUserId);
            //logType
            historyLog.setLogType(logType);
            //createdUserId
            historyLog.setCreatedUserId(actorUserId);
            //content
            HistoryLogTemplate historyLogTemplate = HistoryLogTemplate.getEnumByLogType(logType);
            historyLog.setContent(historyLogTemplate.getContent());
            //preValue
            historyLog.setPreValue(preValue);
            //curValue
            historyLog.setCurValue(curValue);
            //isDeleted
            historyLog.setIsDeleted(0);

            long historyLogId = historyLogService.insertHistoryLog(historyLog);
            result.setData(historyLogId);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("addHistoryLog()error", e);
        }
        return result;
    }

    @Override
    @LogAround
    public HistoryLogDTO getHistoryLog(long orgId, long historyLogId, long actorUserId, long adminUserId) {
        LOGGER.info("getHistoryLog()-request");

        HistoryLogDTO result = new HistoryLogDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            HistoryLog historyLog = historyLogService.findHistoryLogByPrimaryKey(orgId, historyLogId);
            if (null != historyLog) {
                BeanUtils.copyProperties(historyLog, result);
            }
        } catch (Exception e) {
            LOGGER.error("getHistoryLog()-error:{}", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        LOGGER.info("getHistoryLog()-response: result=" + result);
        return result;
    }

    @Override
    @LogAround
    public HistoryLogListDTO listHistoryLogByActorUserId(long orgId, long actorUserId, long adminUserId, int pageNum, int pageSize) {
        HistoryLogListDTO result = new HistoryLogListDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        List<HistoryLogDTO> historyLogDTOs = Collections.EMPTY_LIST;
        try {
            List<HistoryLog> historyLogs = historyLogService.listHistoryLogByActorUserId(orgId, actorUserId, pageNum, pageSize);

            if (!CollectionUtils.isEmpty(historyLogs)) {
                historyLogDTOs = new ArrayList<>();
                for (HistoryLog historyLog : historyLogs) {
                    HistoryLogDTO historyLogDTO = new HistoryLogDTO();
                    BeanUtils.copyProperties(historyLog, historyLogDTO);
                    historyLogDTO.setServiceStatusDTO(new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg()));
                    historyLogDTOs.add(historyLogDTO);
                }
            }
            result.setHistoryLogDTOs(historyLogDTOs);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("ListHistoryLogByActorUserId()-error:{}", e);
            historyLogDTOs = Collections.EMPTY_LIST;
            result.setHistoryLogDTOs(historyLogDTOs);
            return result;
        }
        return result;
    }

    @Override
    @LogAround
    public HistoryLogListDTO listHistoryLogByUserId(long orgId, long userId, long adminUserId, int pageNum, int pageSize) {
        HistoryLogListDTO result = new HistoryLogListDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<HistoryLog> historyLogs = historyLogService.listHistoryLogByUserId(orgId, userId, pageNum, pageSize);
            List<HistoryLogDTO> historyLogDTOs = new ArrayList<>();
            if (!CollectionUtils.isEmpty(historyLogs)) {
                historyLogDTOs = new ArrayList<>();
                for (HistoryLog historyLog : historyLogs) {
                    HistoryLogDTO historyLogDTO = new HistoryLogDTO();
                    BeanUtils.copyProperties(historyLog, historyLogDTO);
                    historyLogDTOs.add(historyLogDTO);
                }
            }
            result.setHistoryLogDTOs(historyLogDTOs);
        } catch (ServiceStatusException e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("ListHistoryByUserId()-error:{}", e);
            result.setHistoryLogDTOs(Collections.EMPTY_LIST);
            return result;
        }
        return result;
    }

    @Override
    @LogAround
    public VoidDTO deleteHistoryLog(long orgId, long historyLogId, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            historyLogService.deleteHistoryLogByPrimaryKey(orgId, historyLogId);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("deleteHistoryLog()-error", e);
        }
        return result;
    }
}
