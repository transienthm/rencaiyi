package hr.wozai.service.nlp.server.test.labelcloud.service;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.nlp.server.test.labelcloud.base.TestBase;
import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.model.labelcloud.SurveyResponseModel;
import hr.wozai.service.nlp.server.service.inter.labelcloud.LabelCloudService;

public class LabelCloudServiceImplTest extends TestBase {

  @Autowired
  private LabelCloudService labelCloudService;

  private LabelCloudModel labelCloudModel;

  private Long orgId = 6L;
  private Long surveyActivityId = 66L;
  private Long surveyItemId = 666L;
  private Long cloudVersion = 6666L;

  @Before
  public void preprocess() {
    this.labelCloudService.updateCloudVersion(this.cloudVersion);

    this.labelCloudModel = new LabelCloudModel();
    this.labelCloudModel.setOrgId(this.orgId);
    this.labelCloudModel.setSurveyActivityId(this.surveyActivityId);
    this.labelCloudModel.setSurveyItemId(this.surveyItemId);
    this.labelCloudModel.setCloudVersion(this.labelCloudService.getCurrentCloudVersion());

    String stringJsonLabelCloud = "{\"第二名\":\"0.301\", \"第一名\":\"0.529\"}";
    JSONObject jsonLabelCloud = JSON.parseObject(stringJsonLabelCloud);
    this.labelCloudModel.setLabelCloud(jsonLabelCloud);

    this.labelCloudModel.setCreatedTime(System.currentTimeMillis());
    this.labelCloudModel.setLastModifiedTime(this.labelCloudModel.getCreatedTime());

    this.labelCloudModel.setIsDeleted(0);
  }

  @Test
  public void testGetAllSurveyResponses() throws Throwable {
    List<SurveyResponseModel> surveyResponseModels = this.labelCloudService.getAllSurveyResponses();
    Assert.assertEquals(surveyResponseModels.size() > 0, true);
  }

  @Test
  public void testGetMaxSurveyResponseId() throws Throwable {
    Long maxSurveyResponseId = this.labelCloudService.getMaxSurveyResponseId();
    Assert.assertEquals(maxSurveyResponseId >= 0, true);

    List<SurveyResponseModel> surveyResponseModels = this.labelCloudService.getAllSurveyResponses();
    Assert.assertEquals(surveyResponseModels.size() > 0, true);

    Long lastSurveyResponseId = surveyResponseModels.get(surveyResponseModels.size() - 1).getSurveyResponseId();
    Assert.assertEquals(maxSurveyResponseId, lastSurveyResponseId);
  }

  @Test
  public void testGetIsDeleted() throws Throwable {
    Integer isDeleted = this.labelCloudService.getIsDeleted();
    Assert.assertEquals(isDeleted >= 0, true);
  }

  @Test
  public void testGetCurrentCloudVersion() throws Throwable {
    Long currentCloudVersion = this.labelCloudService.getCurrentCloudVersion();
    Assert.assertEquals(currentCloudVersion >= 0, true);
    Assert.assertEquals(currentCloudVersion, this.cloudVersion);
  }

  @Test
  public void testUpdateCloudVersion() throws Throwable {
    Integer updatedLineNumber = this.labelCloudService.updateCloudVersion(this.cloudVersion);
    Assert.assertEquals(updatedLineNumber, new Integer(1));
  }

  @Test
  public void testInsertLabelCloud() throws Throwable {
    Long cloudId = this.labelCloudService.insertLabelCloud(this.labelCloudModel);
    Assert.assertEquals(cloudId, this.labelCloudModel.getCloudId());
  }

  @Test
  public void testBatchInsertLabelClouds() throws Throwable {
    this.labelCloudService.batchInsertLabelClouds(
            new ArrayList<>(
                    Arrays.asList(this.labelCloudModel)
            )
    );
  }

  @Test
  public void testUpdateLabelCloud() throws Throwable {
    this.testInsertLabelCloud();
    Integer updatedLineNumber = this.labelCloudService.updateLabelCloud(this.labelCloudModel);
    Assert.assertEquals(updatedLineNumber >= 0, true);
    Assert.assertEquals(updatedLineNumber, new Integer(1));
  }

  @Test
  public void testBatchUpdateLabelClouds() throws Throwable {
    this.testInsertLabelCloud();
    List<LabelCloudModel> labelCloudModels = new ArrayList<>(
            Arrays.asList(this.labelCloudModel)
    );
    Integer updatedLineNumber = this.labelCloudService.batchUpdateLabelClouds(
            labelCloudModels
    );
    Assert.assertEquals(updatedLineNumber >= 0, true);
    Assert.assertEquals(updatedLineNumber, new Integer(labelCloudModels.size()));
  }

  @Test
  public void testFindLabelCloud() throws Throwable {
    this.testInsertLabelCloud();
    LabelCloudModel labelCloudModel = this.labelCloudService.findLabelCloud(
            this.labelCloudModel.getOrgId(),
            this.labelCloudModel.getSurveyActivityId(),
            this.labelCloudModel.getSurveyItemId()
    );
    Assert.assertEquals(labelCloudModel != null, true);
    Assert.assertEquals(labelCloudModel.getCloudId(), this.labelCloudModel.getCloudId());
  }

  @Test
  public void testListLabelCloudsByActivityId() throws Throwable {
    this.testBatchInsertLabelClouds();
    List<LabelCloudModel> labelCloudModels = this.labelCloudService.listLabelCloudsByActivityId(
            this.labelCloudModel.getOrgId(),
            this.labelCloudModel.getSurveyActivityId()
    );
    Assert.assertEquals(labelCloudModels.size(), 1);
  }

  @Test
  public void testListLabelCloudsBySurveyItemIds() throws Throwable {
    this.testBatchInsertLabelClouds();
    List<Long> surveyItemIds = new ArrayList<>(
            Arrays.asList(this.labelCloudModel.getSurveyItemId())
    );
    List<LabelCloudModel> labelCloudModels = this.labelCloudService.listLabelCloudsBySurveyItemIds(
            this.labelCloudModel.getOrgId(),
            surveyItemIds
    );
    Assert.assertEquals(labelCloudModels.size(), surveyItemIds.size());
  }

  @Test
  public void testListLabelCloudsByActivityIdAndSurveyItemIds() throws Throwable {
    this.testBatchInsertLabelClouds();
    List<Long> surveyItemIds = new ArrayList<>(
            Arrays.asList(this.labelCloudModel.getSurveyItemId())
    );
    List<LabelCloudModel> labelCloudModels = this.labelCloudService.listLabelCloudsByActivityIdAndSurveyItemIds(
            this.labelCloudModel.getOrgId(),
            this.labelCloudModel.getSurveyActivityId(),
            surveyItemIds
    );
    Assert.assertEquals(labelCloudModels.size(), surveyItemIds.size());
  }
}