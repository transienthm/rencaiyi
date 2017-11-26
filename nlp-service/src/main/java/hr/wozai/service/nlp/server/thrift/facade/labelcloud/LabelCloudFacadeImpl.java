package hr.wozai.service.nlp.server.thrift.facade.labelcloud;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import hr.wozai.service.nlp.client.labelcloud.dto.LabelDTO;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudDTO;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudListDTO;
import hr.wozai.service.nlp.client.labelcloud.facade.LabelCloudFacade;

import hr.wozai.service.nlp.server.helper.FacadeExceptionHelper;
import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.service.inter.labelcloud.LabelCloudService;

@Service("labelCloudFacadeImpl")
public class LabelCloudFacadeImpl implements LabelCloudFacade {

  private Logger logger = LoggerFactory.getLogger(LabelCloudFacadeImpl.class);

  @Autowired
  private LabelCloudService labelCloudService;

  @LogAround
  private void __formatLabelCloud(JSONObject jsonLabelCloud, List<LabelDTO> labelDTOs) {
    if (jsonLabelCloud != null && !CollectionUtils.isEmpty(jsonLabelCloud.entrySet())) {
      for (Map.Entry<String, Object> entry : jsonLabelCloud.entrySet()) {
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLabel(entry.getKey());
        labelDTO.setWeight(entry.getValue().toString());
        labelDTOs.add(labelDTO);
      }
      Collections.sort(labelDTOs);
      Collections.reverse(labelDTOs);
    }
  }

  @LogAround
  @Override
  public LabelCloudDTO findLabelCloud(
          long orgId,
          long surveyActivityId,
          long surveyItemId,
          long actorUserId,
          long adminUserId) {
    LabelCloudDTO result = new LabelCloudDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    List<LabelDTO> labelDTOs = new ArrayList<>();
    result.setLabelClouds(labelDTOs);

    try {
      LabelCloudModel labelCloudModel =
              this.labelCloudService.findLabelCloud(orgId, surveyActivityId, surveyItemId);
      if (labelCloudModel == null) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      BeanHelper.copyPropertiesHandlingJSON(labelCloudModel, result);
      this.__formatLabelCloud(labelCloudModel.getLabelCloud(), labelDTOs);
    } catch (Exception e) {
      this.logger.error("findLabelCloud(): error ", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public LabelCloudListDTO listLabelCloudsByActivityId(
          long orgId, long surveyActivityId, long actorUserId, long adminUserId) {
    LabelCloudListDTO result = new LabelCloudListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    List<LabelCloudDTO> labelCloudDTOs = new ArrayList<>();
    result.setLabelCloudDTOs(labelCloudDTOs);

    try {
      List<LabelCloudModel> labelCloudModels =
              this.labelCloudService.listLabelCloudsByActivityId(orgId, surveyActivityId);
      if (!CollectionUtils.isEmpty(labelCloudModels)) {
        for (LabelCloudModel labelCloudModel : labelCloudModels) {
          if (labelCloudModel == null) {
            continue;
          }
          LabelCloudDTO labelCloudDTO = new LabelCloudDTO();
          BeanHelper.copyPropertiesHandlingJSON(labelCloudModel, labelCloudDTO);

          List<LabelDTO> labelDTOs = new ArrayList<>();
          labelCloudDTO.setLabelClouds(labelDTOs);
          this.__formatLabelCloud(labelCloudModel.getLabelCloud(), labelDTOs);

          labelCloudDTOs.add(labelCloudDTO);
        }
      }
    } catch (Exception e) {
      this.logger.error("listLabelCloudsByActivityId(): error ", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public LabelCloudListDTO listLabelCloudsBySurveyItemIds(
          long orgId, List<Long> surveyItemIds, long actorUserId, long adminUserId) {
    LabelCloudListDTO result = new LabelCloudListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    List<LabelCloudDTO> labelCloudDTOs = new ArrayList<>();
    result.setLabelCloudDTOs(labelCloudDTOs);

    try {
      List<LabelCloudModel> labelCloudModels =
              this.labelCloudService.listLabelCloudsBySurveyItemIds(orgId, surveyItemIds);
      if (!CollectionUtils.isEmpty(labelCloudModels)) {
        for (LabelCloudModel labelCloudModel : labelCloudModels) {
          if (labelCloudModel == null) {
            continue;
          }
          LabelCloudDTO labelCloudDTO = new LabelCloudDTO();
          BeanHelper.copyPropertiesHandlingJSON(labelCloudModel, labelCloudDTO);

          List<LabelDTO> labelDTOs = new ArrayList<>();
          labelCloudDTO.setLabelClouds(labelDTOs);
          this.__formatLabelCloud(labelCloudModel.getLabelCloud(), labelDTOs);

          labelCloudDTOs.add(labelCloudDTO);
        }
      }
    } catch (Exception e) {
      this.logger.error("listLabelCloudsBySurveyItemIds(): error ", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public LabelCloudListDTO listLabelCloudsByActivityIdAndSurveyItemIds(
          long orgId, long surveyActivityId, List<Long> surveyItemIds, long actorUserId, long adminUserId) {
    LabelCloudListDTO result = new LabelCloudListDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    List<LabelCloudDTO> labelCloudDTOs = new ArrayList<>();
    result.setLabelCloudDTOs(labelCloudDTOs);

    try {
      List<LabelCloudModel> labelCloudModels =
              this.labelCloudService.listLabelCloudsByActivityIdAndSurveyItemIds(orgId, surveyActivityId, surveyItemIds);
      if (!CollectionUtils.isEmpty(labelCloudModels)) {
        for (LabelCloudModel labelCloudModel : labelCloudModels) {
          if (labelCloudModel == null) {
            continue;
          }
          LabelCloudDTO labelCloudDTO = new LabelCloudDTO();
          BeanHelper.copyPropertiesHandlingJSON(labelCloudModel, labelCloudDTO);

          List<LabelDTO> labelDTOs = new ArrayList<>();
          labelCloudDTO.setLabelClouds(labelDTOs);
          this.__formatLabelCloud(labelCloudModel.getLabelCloud(), labelDTOs);

          labelCloudDTOs.add(labelCloudDTO);
        }
      }
    } catch (Exception e) {
      this.logger.error("listLabelCloudsByActivityIdAndSurveyItemIds(): error ", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }
}
