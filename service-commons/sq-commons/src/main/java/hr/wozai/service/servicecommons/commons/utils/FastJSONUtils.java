// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-26
 */
public class FastJSONUtils {

  /**
   * Convert JSONArray to Java List of given type
   *
   * @param jsonArray
   * @param objectClass
   * @param <T>
   * @return
   */
  public static <T> List<T> convertJSONArrayToObjectList(JSONArray jsonArray, Class<T> objectClass) {

    if (null == jsonArray
        || 0 == jsonArray.size()) {
      return Collections.EMPTY_LIST;
    }
    List<T> parsedObjects = new ArrayList<>();
    try {
      for (int i = 0; i < jsonArray.size(); i++) {
        parsedObjects.add(JSON.toJavaObject(jsonArray.getJSONObject(i), objectClass));
      }
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_JSON);
    }
    return parsedObjects;
  }

  /**
   * Convert list of objects to JSONArray
   *
   * @param instances
   * @param <T>
   * @return
   */
  public static <T> JSONArray convertObjectListToJSONArray(List<T> instances) {
    JSONArray jsonArray = new JSONArray();
    if (!CollectionUtils.isEmpty(instances)) {
      for (T instance: instances) {
        jsonArray.add(instance);
      }
    }
    return jsonArray;
  }

}
