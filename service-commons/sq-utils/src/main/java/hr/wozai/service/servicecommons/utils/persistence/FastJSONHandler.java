// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.persistence;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handle the serialization and deserialization between JSON in Java and JSON in MySQL
 *
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-17
 */
public class FastJSONHandler implements TypeHandler<Object> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FastJSONHandler.class);

  @Override
  public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
    String jsonString = null;
    if (null != parameter) {
      jsonString = ((JSONObject) parameter).toString();
    }
    ps.setString(i, jsonString);
  }

  @Override
  public Object getResult(ResultSet rs, String columnName) throws SQLException {
    String jsonString = rs.getString(columnName);
    JSONObject jsonObject = null;
    if (null != jsonString) {
      try {
        jsonObject = JSON.parseObject(jsonString);
      } catch (Exception e) {
        LOGGER.error("getResult(): invalid JSON String format, jsonString=" + jsonString);
      }
    }
    return jsonObject;
  }

  @Override
  public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
    String jsonString = rs.getString(columnIndex);
    JSONObject jsonObject = null;
    if (null != jsonString) {
      try {
        jsonObject = JSON.parseObject(jsonString);
      } catch (Exception e) {
        LOGGER.error("getResult(): invalid JSON String format, jsonString=" + jsonString);
      }
    }
    return jsonObject;
  }

  @Override
  public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
    String jsonString = cs.getString(columnIndex);
    JSONObject jsonObject = null;
    if (null != jsonString) {
      try {
        jsonObject = JSON.parseObject(jsonString);
      } catch (Exception e) {
        LOGGER.error("getResult(): invalid JSON String format, jsonString=" + jsonString);
      }
    }
    return jsonObject;
  }
}
