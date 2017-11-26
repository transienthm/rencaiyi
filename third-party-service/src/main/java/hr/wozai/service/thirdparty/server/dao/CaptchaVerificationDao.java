// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.dao;


import hr.wozai.service.thirdparty.server.model.CaptchaVerification;

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
@Repository("captchaVerificationDao")
public class CaptchaVerificationDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.thirdparty.server.dao.CaptchaVerificationMapper";
  private static final String INSERT = BASE_PACKAGE + "." + "insert";
  private static final String FIND_BY_CREATE_TIME_AND_VERIFICATION_CODE =
      BASE_PACKAGE + "." + "findByCreateTimeAndVerificationCode";
  private static final String UPDATE_BY_CREATE_TIME_AND_VERIFICATION_CODE_SELECTIVE =
      BASE_PACKAGE + "." + "updateByCreateTimeAndVerificationCodeSelective";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int insert(CaptchaVerification captchaVerification) {
    return sqlSessionTemplate.insert(INSERT, captchaVerification);
  }

  public CaptchaVerification findByCreateTimeAndVerificationCode(long createTime, String verificationCode) {
    Map<String, Object> params = new HashMap<>();
    params.put("createTime", createTime);
    params.put("verificationCode", verificationCode);
    return sqlSessionTemplate.selectOne(FIND_BY_CREATE_TIME_AND_VERIFICATION_CODE, params);
  }

  public int updateByCreateTimeAndVerificationCodeSelective(CaptchaVerification captchaVerification) {
    Map<String, Object> params = new HashMap<>();
    params.put("createTime", captchaVerification.getCreateTime());
    params.put("verificationCode", captchaVerification.getVerificationCode());
    return sqlSessionTemplate.update(UPDATE_BY_CREATE_TIME_AND_VERIFICATION_CODE_SELECTIVE, params);
  }

}
