package hr.wozai.service.nlp.server.service.impl.labelcloud;

import java.util.List;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.nlp.server.dao.labelcloud.LabelCloudDao;
import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.model.labelcloud.SurveyResponseModel;
import hr.wozai.service.nlp.server.service.inter.labelcloud.LabelCloudService;

@Service("labelCloudServiceImpl")
public class LabelCloudServiceImpl implements LabelCloudService {

  @Autowired
  private LabelCloudDao labelCloudDao;

  @Override
  @LogAround
  public Long getMaxSurveyResponseId() {
    Long maxSurveyResponseId = this.labelCloudDao.getMaxSurveyResponseId();
    if (maxSurveyResponseId == null) {
      maxSurveyResponseId = -1L;
    }
    return maxSurveyResponseId;
  }

  @Override
  @LogAround
  public List<SurveyResponseModel> getAllSurveyResponses() {
    return this.labelCloudDao.getAllSurveyResponses();
  }

  @Override
  @LogAround
  public Integer getIsDeleted() {
    Integer isDeleted = this.labelCloudDao.getIsDeleted();
    if (isDeleted == null) {
      isDeleted = -1;
    }
    return isDeleted;
  }

  @Override
  @LogAround
  public Long getCurrentCloudVersion() {
    Long currentCloudVersion = this.labelCloudDao.getCurrentCloudVersion();
    if (currentCloudVersion == null) {
      currentCloudVersion = -1L;
    }
    return currentCloudVersion;
  }

  @Override
  @LogAround
  public Integer updateCloudVersion(long cloudVersion) {
    return this.labelCloudDao.updateCloudVersion(cloudVersion);
  }

  @Override
  @LogAround
  public Long insertLabelCloud(LabelCloudModel labelCloudModel) {
    if (labelCloudModel == null) {
      return -1L;
    }
    return this.labelCloudDao.insertLabelCloud(labelCloudModel);
  }

  @Override
  @LogAround
  public void batchInsertLabelClouds(List<LabelCloudModel> labelCloudModels) {
    if (!CollectionUtils.isEmpty(labelCloudModels)) {
      this.labelCloudDao.batchInsertLabelClouds(labelCloudModels);
    }
  }

  @Override
  @LogAround
  public Integer updateLabelCloud(LabelCloudModel labelCloudModel) {
    if (labelCloudModel == null) {
      return -1;
    }
    return this.labelCloudDao.updateLabelCloud(labelCloudModel);
  }

  @Override
  @LogAround
  public Integer batchUpdateLabelClouds(List<LabelCloudModel> labelCloudModels) {
    if (CollectionUtils.isEmpty(labelCloudModels)) {
      return -1;
    }
    return this.labelCloudDao.batchUpdateLabelClouds(labelCloudModels);
  }

  @Override
  @LogAround
  public LabelCloudModel findLabelCloud(
          long orgId, long surveyActivityId, long surveyItemId) {
    return this.labelCloudDao.findLabelCloud(orgId, surveyActivityId, surveyItemId);
  }

  @Override
  @LogAround
  public List<LabelCloudModel> listLabelCloudsByActivityId(
          long orgId, long surveyActivityId) {
    return this.labelCloudDao.listLabelCloudsByActivityId(orgId, surveyActivityId);
  }

  @Override
  @LogAround
  public List<LabelCloudModel> listLabelCloudsBySurveyItemIds(
          long orgId, List<Long> surveyItemIds) {
    if (CollectionUtils.isEmpty(surveyItemIds)) {
      return Collections.EMPTY_LIST;
    }
    return this.labelCloudDao.listLabelCloudsBySurveyItemIds(orgId, surveyItemIds);
  }

  @Override
  @LogAround
  public List<LabelCloudModel> listLabelCloudsByActivityIdAndSurveyItemIds(
          long orgId, long surveyActivityId, List<Long> surveyItemIds) {
    if (CollectionUtils.isEmpty(surveyItemIds)) {
      return Collections.EMPTY_LIST;
    }
    return this.labelCloudDao.listLabelCloudsByActivityIdAndSurveyItemIds(orgId, surveyActivityId, surveyItemIds);
  }
}
