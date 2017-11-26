// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.utils.persistence;

/**
 * List<String> set content or null; get EMPTY_LIST
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-07
 */
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringListHandler implements TypeHandler<Object> {

  @Override
  public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbc) throws SQLException {

    String listString = null;
    if(null != parameter) {
      List<String> str = (List<String>)parameter;
      if(0 != str.size()) {
        listString = StringUtils.join(str, ",");
      }
    }
    ps.setString(i, listString);

  }

  @Override
  public Object getResult(ResultSet rs, String columnName) throws SQLException {
    String listString = rs.getString(columnName);
    List<String> result;
    if(null == listString)
      result = Collections.EMPTY_LIST;
    else if(listString.isEmpty())
      result = Collections.EMPTY_LIST;
    else
      result = Arrays.asList(listString.split(","));
    return result;
  }

  @Override
  public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
    String listString = rs.getString(columnIndex);
    List<String> result;
    if(null == listString)
      result = Collections.EMPTY_LIST;
    else if(listString.isEmpty())
      result = Collections.EMPTY_LIST;
    else
      result = Arrays.asList(listString.split(","));
    return result;
  }

  @Override
  public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
    String listString = cs.getString(columnIndex);
    List<String> result;
    if(null == listString)
      result = Collections.EMPTY_LIST;
    else if(listString.isEmpty())
      result = Collections.EMPTY_LIST;
    else
      result = Arrays.asList(listString.split(","));
    return result;
  }

}
