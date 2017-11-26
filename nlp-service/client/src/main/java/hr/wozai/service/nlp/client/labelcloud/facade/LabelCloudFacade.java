package hr.wozai.service.nlp.client.labelcloud.facade;

import java.util.List;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudDTO;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudListDTO;

@ThriftService
public interface LabelCloudFacade {

  @ThriftMethod
  LabelCloudDTO findLabelCloud(
          long orgId, long surveyActivityId, long surveyItemId, long actorUserId, long adminUserId
  );

  @ThriftMethod
  LabelCloudListDTO listLabelCloudsByActivityId(
          long orgId, long surveyActivityId, long actorUserId, long adminUserId
  );

  @ThriftMethod
  LabelCloudListDTO listLabelCloudsBySurveyItemIds(
          long orgId, List<Long> surveyItemIds, long actorUserId, long adminUserId
  );

  @ThriftMethod
  LabelCloudListDTO listLabelCloudsByActivityIdAndSurveyItemIds(
          long orgId, long surveyActivityId, List<Long> surveyItemIds, long actorUserId, long adminUserId
  );
}