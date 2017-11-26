// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.FastJSONUtils;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.service.OrgPickOptionService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-01
 */
@Service("profileFieldFactory")
public class ProfileFieldFactory {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldFactory.class);

  private List<ProfileField> presetFields = null;

  @Autowired
  OrgPickOptionService orgPickOptionService;

  /**
   * key: referenceName of contained field
   * val: referenceName of container field
   */
  private Map<String, String> relations = null;

  @PostConstruct
  public void init() {
    try {
      InputStream resource = getClass().getResourceAsStream("/preset/preset-profile-template.json");
      BufferedReader br = new BufferedReader(new InputStreamReader(resource));
      String line = null;
      StringBuilder stringBuilder = new StringBuilder();
      while (null != (line = br.readLine())) {
        stringBuilder.append(line);
      }
      JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
      // parse presetFields
      presetFields = FastJSONUtils.convertJSONArrayToObjectList(jsonObject.getJSONArray("fields"), ProfileField.class);
      // set indexes
      int logicalIndex = 0;
      int physicalIndex = 0;
      for (int i = 0; i < presetFields.size(); i++) {
        ProfileField profileField = presetFields.get(i);
        profileField.setLogicalIndex(logicalIndex++);
        if (DataType.CONTAINER != DataType.getEnumByCode(profileField.getDataType())) {
          profileField.setPhysicalIndex(physicalIndex++);
        }
      }
      // parse relations
      relations = new HashMap<>();
      JSONObject containObj = jsonObject.getJSONObject("relations");
      for (String containerRefName: containObj.keySet()) {
        List<String> containedRefNames =
            JSONArray.parseArray(containObj.getJSONArray(containerRefName).toJSONString(), String.class);
        for (String containedRefName: containedRefNames) {
          if (!relations.containsKey(containedRefName)) {
            relations.put(containedRefName, containerRefName);
          }
        }
      }
      LOGGER.info("init(): presetFields=" + presetFields);
    } catch (Exception e) {
      LOGGER.error("init()-error: FATAL !!! Fail to preset-profile-template", e);
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PROFILE_FIELDS);
    }
  }

  public List<ProfileField> listFieldOfPresetProfileTemplate() {
    List<ProfileField> clonedProfileFields = new ArrayList<>();
    for (ProfileField profileField: presetFields) {
      ProfileField clonedProfileField = new ProfileField();
      BeanUtils.copyProperties(profileField, clonedProfileField);
      clonedProfileFields.add(clonedProfileField);
    }

    return clonedProfileFields;
  }

  public Map<String, String> getContainingRelations() {
    return this.relations;
  }

}
