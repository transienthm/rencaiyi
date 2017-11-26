package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.survey.dto.*;
import hr.wozai.service.user.client.survey.facade.SurveyFacade;
import hr.wozai.service.user.server.enums.SurveyFrequency;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.service.SurveyService;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/29
 */
public class SurveyFacadeImplTest extends TestBase {
  @Autowired
  private SurveyFacade surveyFacade;

  @Autowired
  private SurveyService surveyService;

  private long orgId = 199L;
  private long userId = 199L;
  private String question = "question";
  private String description = "description";
  private String lowLabel = "low";
  private String highLabel = "high";

  private SurveyItemDTO surveyItemDTO;

  @Before
  public void setUp() throws Exception {
    surveyItemDTO = new SurveyItemDTO();
    surveyItemDTO.setOrgId(orgId);
    surveyItemDTO.setCreatedUserId(userId);
  }

  @Test
  public void testSurveyItem() throws Exception {
    long curTs = System.currentTimeMillis();
    long oneDay = 3600 * 24 * 1000;
    // ScaleQuestion
    surveyItemDTO.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyItemDTO.setQuestion(question);
    surveyItemDTO.setDescription(description);
    surveyItemDTO.setLowLabel(lowLabel);
    surveyItemDTO.setHighLabel(highLabel);
    surveyItemDTO.setStartTime(curTs + oneDay);
    surveyItemDTO.setEndTime(curTs + 5 * oneDay);

    LongDTO surveyItemId = surveyFacade.addSurveyItem(orgId, surveyItemDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), surveyItemId.getServiceStatusDTO().getCode());
    Assert.assertNotEquals(0L, surveyItemId.getData());

    SurveyItemListDTO surveyItemListDTO = surveyFacade.listSurveyItemsByOrgId(orgId, 1, 10, userId, userId);
    Assert.assertEquals(1, surveyItemListDTO.getSurveyItemDTOs().size());

    surveyItemListDTO = surveyFacade.listSurveyItemsByStartAndEndTime(orgId, curTs + oneDay, curTs + 5 * oneDay, userId, userId);
    Assert.assertEquals(1, surveyItemListDTO.getSurveyItemDTOs().size());

    SurveyItemDTO inDb = surveyItemListDTO.getSurveyItemDTOs().get(0);
    inDb.setQuestion("update");
    VoidDTO voidDTO = surveyFacade.updateSurveyItem(orgId, inDb, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    voidDTO = surveyFacade.deleteSurveyItem(orgId, inDb.getSurveyItemId(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    surveyItemListDTO = surveyFacade.listSurveyItemsByOrgId(orgId, 1, 10, userId, userId);
    Assert.assertEquals(0, surveyItemListDTO.getSurveyItemDTOs().size());
  }

  @Test
  public void testSurveyConfig() throws Exception {
    SurveyConfigDTO surveyConfigDTO = surveyFacade.getSurveyConfig(orgId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), surveyConfigDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(surveyConfigDTO.getFrequency().intValue(), SurveyFrequency.ONE_WEEK.getCode().intValue());

    surveyConfigDTO.setFrequency(SurveyFrequency.TWO_WEEK.getCode());
    LongDTO result = surveyFacade.updateSurveyConfig(orgId, surveyConfigDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());

    surveyConfigDTO = surveyFacade.getSurveyConfig(orgId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), surveyConfigDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(surveyConfigDTO.getFrequency().intValue(), SurveyFrequency.TWO_WEEK.getCode().intValue());
    Assert.assertNotNull(surveyConfigDTO.getSurveyConfigId());
  }

  @Test
  public void testSurveyActivityAndResponse() throws Exception {
    long curTs = System.currentTimeMillis();
    long oneDay = 3600 * 24 * 1000;
    // ScaleQuestion
    surveyItemDTO.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyItemDTO.setQuestion(question);
    surveyItemDTO.setDescription(description);
    surveyItemDTO.setLowLabel(lowLabel);
    surveyItemDTO.setHighLabel(highLabel);
    surveyItemDTO.setStartTime(curTs + 5000L);
    surveyItemDTO.setEndTime(curTs + 5 * oneDay);

    LongDTO surveyItemId = surveyFacade.addSurveyItem(orgId, surveyItemDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), surveyItemId.getServiceStatusDTO().getCode());
    Assert.assertNotEquals(0L, surveyItemId.getData());

    SurveyActivity surveyActivity = new SurveyActivity();
    surveyActivity.setOrgId(orgId);
    surveyActivity.setCreatedUserId(userId);
    surveyActivity.setCreatedTime(curTs + 10000L);

    surveyService.initSurveyActivity(orgId, surveyActivity, Arrays.asList(userId));

    SurveyActivityDTO surveyActivityDTO = surveyFacade.getInPeriodAndUnSubmitedSurveyActivity(orgId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), surveyActivityDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, surveyActivityDTO.getSurveyResponseDTOs().size());

    List<SurveyResponseDTO> surveyResponseDTOs = new ArrayList<>();
    for (SurveyResponseDTO surveyResponseDTO : surveyResponseDTOs) {
      surveyResponseDTO.setResponse(1);
      surveyResponseDTO.setResponseDetail("detail");
      surveyResponseDTO.setIsSubmit(1);
    }
    surveyActivityDTO.setSurveyResponseDTOs(surveyResponseDTOs);
    VoidDTO voidDTO = surveyFacade.submitSurveyActivityByUser(orgId, surveyActivityDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), surveyActivityDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void test() {
    SurveyItemHistoryListDTO result = surveyFacade.listSurveyItemHistorysByOrgIdAndTimeRange(3, 0, Long.MAX_VALUE, 0L, 0L);
    System.out.println(result);
  }
}