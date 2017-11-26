// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.bean;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-17
 */
public class BeanHelper {

  private final static Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);

  /**
   * For any pair, if either is of type JSONObject and the other is String,
   *  convert between JSONObject and String
   *
   * @param source
   * @param target
   */
  public static void copyPropertiesHandlingJSON(
      final Object source,
      final Object target) {

    Assert.notNull(source);
    Assert.notNull(target);

    // copy JSONObject-typed fields
    final List<String> excludes = new ArrayList<String>();
    final Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(source);
    final Map<String, PropertyDescriptor> targetPdMap = getPropertyDescriptorMap(target);
    for (Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (targetPdMap.containsKey(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        PropertyDescriptor targetPd = targetPdMap.get(sourcePropertyName);
        Class sourcePropertyClass = sourcePd.getPropertyType();
        Class targetPropertyClass = targetPd.getPropertyType();
        if ((JSONObject.class.equals(sourcePropertyClass) && String.class.equals(targetPropertyClass))
            || (String.class.equals(sourcePropertyClass) && JSONObject.class.equals(targetPropertyClass))) {
          Method readMethod = sourcePd.getReadMethod();
          Method writeMethod = targetPd.getWriteMethod();
          try {
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
              readMethod.setAccessible(true);
            }
            Object value = readMethod.invoke(source);
            if (null != value) {
              if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
              }
              if (JSONObject.class.equals(sourcePropertyClass)) {
                writeMethod.invoke(target, value.toString());
              } else {
                writeMethod.invoke(target, JSON.parseObject((String) value));
              }
            }
          } catch (Exception e) {
            LOGGER.error("copyPropertiesHandlingJSON()-error: "
                         + "Could not copy property {} from source to target", targetPd.getName(), e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
          }
        }
      }
    }

    // copy other fields
    BeanUtils.copyProperties(
        source, target, excludes.toArray(new String[excludes.size()]));
  }

  /**
   * For any pair, if either is of type JSONObject and the other is String,
   *  convert between JSONObject and String
   *  convert between BigDecimal and String
   *
   * @param source
   * @param target
   */
  public static void copyPropertiesHandlingJSONAndBigDecimal(
          final Object source,
          final Object target) {

    // copy JSONObject-typed fields
    final List<String> excludes = new ArrayList<String>();
    final Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(source);
    final Map<String, PropertyDescriptor> targetPdMap = getPropertyDescriptorMap(target);
    for (Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (targetPdMap.containsKey(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        PropertyDescriptor targetPd = targetPdMap.get(sourcePropertyName);
        Class sourcePropertyClass = sourcePd.getPropertyType();
        Class targetPropertyClass = targetPd.getPropertyType();
        // copy json
        if ((JSONObject.class.equals(sourcePropertyClass) && String.class.equals(targetPropertyClass))
                || (String.class.equals(sourcePropertyClass) && JSONObject.class.equals(targetPropertyClass))) {
          Method readMethod = sourcePd.getReadMethod();
          Method writeMethod = targetPd.getWriteMethod();
          try {
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
              readMethod.setAccessible(true);
            }
            Object value = readMethod.invoke(source);
            if (null != value) {
              if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
              }
              if (JSONObject.class.equals(sourcePropertyClass)) {
                writeMethod.invoke(target, value.toString());
              } else {
                writeMethod.invoke(target, JSON.parseObject((String) value));
              }
            }
          } catch (Exception e) {
            LOGGER.error("copyPropertiesHandlingJSON()-error: "
                         + "Could not copy property {} from source to target", targetPd.getName(), e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
          }
        }
        // copy BigDecimal
        if ((BigDecimal.class.equals(sourcePropertyClass) && String.class.equals(targetPropertyClass))
                || (String.class.equals(sourcePropertyClass) && BigDecimal.class.equals(targetPropertyClass))) {
          Method readMethod = sourcePd.getReadMethod();
          Method writeMethod = targetPd.getWriteMethod();
          try {
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
              readMethod.setAccessible(true);
            }
            Object value = readMethod.invoke(source);
            if (null != value) {
              if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
              }
              if (BigDecimal.class.equals(sourcePropertyClass)) {
                writeMethod.invoke(target, ((BigDecimal)value).stripTrailingZeros().toPlainString());
              } else {
                writeMethod.invoke(target, new BigDecimal((String) value));
              }
            }
          } catch (Exception e) {
            LOGGER.error("copyPropertiesHandlingJSON()-error: "
                         + "Could not copy property {} from source to target", targetPd.getName(), e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
          }
        }
      }
    }

    // copy other fields
    BeanUtils.copyProperties(
            source, target, excludes.toArray(new String[excludes.size()]));
  }

  /**
   * Convert pdList to pdMap, more time-efficient to use
   *
   * @param obj
   * @return
   */
  private static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Object obj) {
    final PropertyDescriptor[] pdList = BeanUtils.getPropertyDescriptors(obj.getClass());
    final Map<String, PropertyDescriptor> pdMap = new HashMap<>();
    for (PropertyDescriptor pd : pdList) {
      pdMap.put(pd.getName(), pd);
    }
    return pdMap;
  }

  public static String[] getNullPropertyNames(Object source) {
    BeanWrapper beanWrapper = new BeanWrapperImpl(source);
    PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
    HashSet<String> emptyNames = new HashSet<>();
    for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      Object value = beanWrapper.getPropertyValue(propertyDescriptor.getName());
      if (value == null) {
        emptyNames.add(propertyDescriptor.getName());
      }
    }
    return emptyNames.toArray(new String[emptyNames.size()]);
  }

  public static void copyPropertiesHandlingJSONIgnoreNull(
          final Object source,
          final Object target) {

    Assert.notNull(source);
    Assert.notNull(target);

    // copy JSONObject-typed fields
    final Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(source);
    final Map<String, PropertyDescriptor> targetPdMap = getPropertyDescriptorMap(target);

    for (Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (targetPdMap.containsKey(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        PropertyDescriptor targetPd = targetPdMap.get(sourcePropertyName);
        Class sourcePropertyClass = sourcePd.getPropertyType();
        Class targetPropertyClass = targetPd.getPropertyType();
        if ((JSONObject.class.equals(sourcePropertyClass) && String.class.equals(targetPropertyClass))
                || (String.class.equals(sourcePropertyClass) && JSONObject.class.equals(targetPropertyClass))) {
          Method readMethod = sourcePd.getReadMethod();
          Method writeMethod = targetPd.getWriteMethod();
          try {
            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
              readMethod.setAccessible(true);
            }
            Object value = readMethod.invoke(source);
            if (null != value) {
              if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
              }
              if (JSONObject.class.equals(sourcePropertyClass)) {
                writeMethod.invoke(target, value.toString());
              } else {
                writeMethod.invoke(target, JSON.parseObject((String) value));
              }
            }
          } catch (Exception e) {
            LOGGER.error("copyPropertiesHandlingJSON()-error: "
                    + "Could not copy property {} from source to target", targetPd.getName(), e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
          }
        }
      }
    }

    // copy other fields
    BeanUtils.copyProperties(
            source, target, getNullPropertyNames(source));
  }
}
