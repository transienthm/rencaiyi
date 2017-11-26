// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.lang.reflect.Field;

/**
 * 使用swift的注解对象需要重写equals 和 hashcode 继承自本类可以不用重写equals 和 hashcode 方法了
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-8-30 下午9:41
 */
public class BaseThriftObject {

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    return hashCode() == (o.hashCode());
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public String toString() {
    Field[] fields = this.getClass().getDeclaredFields();

    StringBuffer strBuf = new StringBuffer();
    strBuf.append(this.getClass().getName());
    strBuf.append("(");
    for (int i = 0; i < fields.length; i++) {
      Field fd = fields[i];
      fd.setAccessible(true);
      strBuf.append(fd.getName() + ":");
      try {
        strBuf.append(fd.get(this));
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (i != fields.length - 1) {
        strBuf.append("|");
      }
    }

    strBuf.append(")");
    return strBuf.toString();
  }

}
