package hr.wozai.service.nlp.server.dao.labelcloud;

import java.util.List;
import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.model.labelcloud.SurveyResponseModel;

@Repository("labelCloudDao")
public class LabelCloudDao {

  private static final String BASE_PACKAGE =
          "hr.wozai.service.nlp.server.dao.labelcloud.LabelCloudMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public Long getMaxSurveyResponseId() {
      return this.sqlSessionTemplate.selectOne(this.BASE_PACKAGE + "getMaxSurveyResponseId");
  }

  @LogAround
  public List<SurveyResponseModel> getAllSurveyResponses() {
      return this.sqlSessionTemplate.selectList(this.BASE_PACKAGE + "getAllSurveyResponses");
  }

  @LogAround
  public Integer getIsDeleted() {
      return this.sqlSessionTemplate.selectOne(this.BASE_PACKAGE + "getIsDeleted");
  }

  @LogAround
  public Long getCurrentCloudVersion() {
      return this.sqlSessionTemplate.selectOne(this.BASE_PACKAGE + "getCurrentCloudVersion");
  }

  @LogAround
  public Integer updateCloudVersion(long cloudVersion) {
      return this.sqlSessionTemplate.update(
              this.BASE_PACKAGE + "updateCloudVersion", cloudVersion
      );
  }

  @LogAround
  public Long insertLabelCloud(LabelCloudModel labelCloudModel) {
      this.sqlSessionTemplate.insert(this.BASE_PACKAGE + "insertLabelCloud", labelCloudModel);
      return labelCloudModel.getCloudId();
  }

  @LogAround
  public void batchInsertLabelClouds(List<LabelCloudModel> labelCloudModels) {
      this.sqlSessionTemplate.insert(this.BASE_PACKAGE + "batchInsertLabelClouds", labelCloudModels);
  }

  @LogAround
  public Integer updateLabelCloud(LabelCloudModel labelCloudModel) {
      return this.sqlSessionTemplate.update(
              this.BASE_PACKAGE + "updateLabelCloud", labelCloudModel
      );
  }

  @LogAround
  public Integer batchUpdateLabelClouds(List<LabelCloudModel> labelCloudModels) {
      return this.sqlSessionTemplate.update(
              this.BASE_PACKAGE + "batchUpdateLabelClouds", labelCloudModels
      );
  }

  @LogAround
  public LabelCloudModel findLabelCloud(
          long orgId, long surveyActivityId, long surveyItemId) {
      return this.sqlSessionTemplate.selectOne(
              this.BASE_PACKAGE + "findLabelCloud",
              new HashMap() {{
                      put("orgId", orgId);
                      put("surveyActivityId", surveyActivityId);
                      put("surveyItemId", surveyItemId);
              }}
      );
  }

  @LogAround
  public List<LabelCloudModel> listLabelCloudsByActivityId(
          long orgId, long surveyActivityId) {
      return this.sqlSessionTemplate.selectList(
              this.BASE_PACKAGE + "listLabelCloudsByActivityId",
              new HashMap() {{
                      put("orgId", orgId);
                      put("surveyActivityId", surveyActivityId);
              }}
      );
  }

  @LogAround
  public List<LabelCloudModel> listLabelCloudsBySurveyItemIds(
          long orgId, List<Long> surveyItemIds) {
      return this.sqlSessionTemplate.selectList(
              this.BASE_PACKAGE + "listLabelCloudsBySurveyItemIds",
              new HashMap() {{
                      put("orgId", orgId);
                      put("surveyItemIds", surveyItemIds);
              }}
      );
  }

  @LogAround
  public List<LabelCloudModel> listLabelCloudsByActivityIdAndSurveyItemIds(
          long orgId, long surveyActivityId, List<Long> surveyItemIds) {
      return this.sqlSessionTemplate.selectList(
              this.BASE_PACKAGE + "listLabelCloudsByActivityIdAndSurveyItemIds",
              new HashMap() {{
                      put("orgId", orgId);
                      put("surveyActivityId", surveyActivityId);
                      put("surveyItemIds", surveyItemIds);
              }}
      );
  }
}
