// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.dao;


import hr.wozai.service.thirdparty.server.model.SmsVerification;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-25
 */
@Repository("smsVerificationDao")
public class SmsVerificationDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.thirdparty.server.dao.SmsVerificationMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int insert(SmsVerification smsVerification) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "insert", smsVerification);
  }

  public SmsVerification findByMobilePhoneAndVerificationCode(String mobilePhone, String verificationCode) {
    Map<String, Object> params = new HashMap<>();
    params.put("mobilePhone", mobilePhone);
    params.put("verificationCode", verificationCode);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findByMobilePhoneAndVerificationCode", params);
  }

  public SmsVerification findByMobilePhoneAndOptionalIdentifierAndVerificationCode(String mobilePhone,
                                                                                   String optionalIdentifier,
                                                                                   String verificationCode) {
    Map<String, Object> params = new HashMap<>();
    params.put("mobilePhone", mobilePhone);
    params.put("optionalIdentifier", optionalIdentifier);
    params.put("verificationCode", verificationCode);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findByMobilePhoneAndVerificationCode", params);
  }

  public int updateByPrimaryKeySelective(SmsVerification smsVerification) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateByPrimaryKeySelective", smsVerification);
  }


}
