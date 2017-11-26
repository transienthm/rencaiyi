package hr.wozai.service.nlp.server.service.inter.labelcloud;

import java.util.List;

import hr.wozai.service.nlp.server.model.labelcloud.LabelCloudModel;
import hr.wozai.service.nlp.server.model.labelcloud.SurveyResponseModel;
import hr.wozai.service.nlp.server.util.lda.Inferencer;

public interface LabelCloudService {

  Long getMaxSurveyResponseId();

  List<SurveyResponseModel> getAllSurveyResponses();

  Integer getIsDeleted();

  Long getCurrentCloudVersion();

  Integer updateCloudVersion(long cloudVersion);

  Long insertLabelCloud(
          LabelCloudModel labelCloudModel
  );

  void batchInsertLabelClouds(
          List<LabelCloudModel> labelCloudModels
  );

  Integer updateLabelCloud(
          LabelCloudModel labelCloudModel
  );

  Integer batchUpdateLabelClouds(
          List<LabelCloudModel> labelCloudModels
  );

  LabelCloudModel findLabelCloud(
          long orgId, long surveyActivityId, long surveyItemId
  );

  List<LabelCloudModel> listLabelCloudsByActivityId(
          long orgId, long surveyActivityId
  );

  List<LabelCloudModel> listLabelCloudsBySurveyItemIds(
          long orgId, List<Long> surveyItemIds
  );

  List<LabelCloudModel> listLabelCloudsByActivityIdAndSurveyItemIds(
          long orgId, long surveyActivityId, List<Long> surveyItemIds
  );
}