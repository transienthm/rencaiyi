package hr.wozai.service.nlp.server.test.labelcloud.facade;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;

import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudDTO;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudListDTO;
import hr.wozai.service.nlp.client.labelcloud.facade.LabelCloudFacade;

import hr.wozai.service.nlp.server.test.labelcloud.base.TestBase;
import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.service.inter.labelcloud.LabelCloudService;

public class LabelCloudFacadeImplTest extends TestBase {

  @Autowired
  private LabelCloudFacade labelCloudFacade;

  @Autowired
  private LabelCloudService labelCloudService;

  private LabelCloudModel labelCloudModel;

  private Long orgId = 6L;
  private Long surveyActivityId = 66L;
  private Long surveyItemId = 666L;
  private Long cloudVersion = 6666L;

  private Long actorUserId = 0L;
  private Long adminUserId = 0L;

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

    Long cloudId = this.labelCloudService.insertLabelCloud(this.labelCloudModel);
    Assert.assertEquals(cloudId, this.labelCloudModel.getCloudId());
  }

  @Test
  public void testFindLabelCloud() throws Throwable {
    LabelCloudDTO labelCloudDTO = this.labelCloudFacade.findLabelCloud(
            this.labelCloudModel.getOrgId(),
            this.labelCloudModel.getSurveyActivityId(),
            this.labelCloudModel.getSurveyItemId(),
            this.actorUserId,
            this.adminUserId
    );

    Assert.assertEquals(labelCloudDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(labelCloudDTO.getCloudId(), this.labelCloudModel.getCloudId());
    Assert.assertEquals(labelCloudDTO.getLabelClouds().size(), 2);
  }

  @Test
  public void testlistLabelCloudsByActivityId() throws Throwable {
    LabelCloudListDTO labelCloudListDTO = this.labelCloudFacade.listLabelCloudsByActivityId(
            this.labelCloudModel.getOrgId(),
            this.labelCloudModel.getSurveyActivityId(),
            this.actorUserId,
            this.adminUserId
    );

    Assert.assertEquals(labelCloudListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(labelCloudListDTO.getLabelCloudDTOs().size(), 1);
    Assert.assertEquals(labelCloudListDTO.getLabelCloudDTOs().get(0).getCloudId(), this.labelCloudModel.getCloudId());
  }

  @Test
  public void testlistLabelCloudsBySurveyItemIds() throws Throwable {
    List<Long> surveyItemIds = new ArrayList<>(
            Arrays.asList(this.labelCloudModel.getSurveyItemId())
    );
    LabelCloudListDTO labelCloudListDTO = this.labelCloudFacade.listLabelCloudsBySurveyItemIds(
            this.labelCloudModel.getOrgId(),
            surveyItemIds,
            this.actorUserId,
            this.adminUserId
    );

    Assert.assertEquals(labelCloudListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(labelCloudListDTO.getLabelCloudDTOs().size(), surveyItemIds.size());
    Assert.assertEquals(labelCloudListDTO.getLabelCloudDTOs().get(0).getCloudId(), this.labelCloudModel.getCloudId());
  }

  @Test
  public void testlistLabelCloudsByActivityIdAndSurveyItemIds() throws Throwable {
    List<Long> surveyItemIds = new ArrayList<>(
            Arrays.asList(this.labelCloudModel.getSurveyItemId())
    );
    LabelCloudListDTO labelCloudListDTO = this.labelCloudFacade.listLabelCloudsByActivityIdAndSurveyItemIds(
            this.labelCloudModel.getOrgId(),
            this.labelCloudModel.getSurveyActivityId(),
            surveyItemIds,
            this.actorUserId,
            this.adminUserId
    );

    Assert.assertEquals(labelCloudListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(labelCloudListDTO.getLabelCloudDTOs().size(), surveyItemIds.size());
    Assert.assertEquals(labelCloudListDTO.getLabelCloudDTOs().get(0).getCloudId(), this.labelCloudModel.getCloudId());
  }
}