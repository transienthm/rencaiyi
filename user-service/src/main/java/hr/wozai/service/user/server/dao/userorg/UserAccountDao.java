// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.UserAccount;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/1/15
 */
@Repository("userAccountDao")
public class UserAccountDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.UserAccountMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertUserAccount(UserAccount userAccount) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertUserAccount", userAccount);
    return userAccount.getUserId();
  }

  public int deleteUserAccountByPrimaryKey(long userId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("actorUserId", actorUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteByPrimaryKey", params);
    return result;
  }

  public int updateByPrimaryKeySelective(UserAccount userAccount) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "updateByPrimaryKeySelective", userAccount);
    return result;
  }

  public UserAccount findByPrimaryKey(long userId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findByPrimaryKey", userId);
  }

  public UserAccount findByEmailAddress(String emailAddress) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findByEmailAddress", emailAddress);
  }

  public List<UserAccount> listUserAccountByEmailAddress(List<String> emailAddresses) {
    List<UserAccount> userAccounts = null;
    if (!CollectionUtils.isEmpty(emailAddresses)) {
      userAccounts = sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserAccountByEmailAddress", emailAddresses);
    }
    if (CollectionUtils.isEmpty(userAccounts)) {
      userAccounts = Collections.EMPTY_LIST;
    }
    return userAccounts;
  }

}
