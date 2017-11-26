package hr.wozai.service.thirdparty.server.dao;

import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 16/5/10.
 */
@Repository("trustedMobilePhoneDao")
public class TrustedMobilePhoneDao {

    private static final String BASE_PACKAGE = "hr.wozai.service.thirdparty.server.dao.TrustedMobilePhoneMapper.";

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    public long insertTrustedMobilePhone(TrustedMobilePhone trustedMobilePhone){
        sqlSessionTemplate.insert(BASE_PACKAGE + "insert", trustedMobilePhone);
        return trustedMobilePhone.getTrustedMobilePhoneId();
    }

    public TrustedMobilePhone findTrustedMobilePhoneByPrimaryKey(long trustedMobilePhoneId){
        TrustedMobilePhone trustedMobilePhone = sqlSessionTemplate.selectOne(BASE_PACKAGE+"selectTMPByPrimaryKey",trustedMobilePhoneId);
        return trustedMobilePhone;
    }

    public TrustedMobilePhone findTrustedMobilePhoneByMobilePhone(String mobilePhone){
        return sqlSessionTemplate.selectOne(BASE_PACKAGE+"findTMPByMobilePhone",mobilePhone);

    }

    public List<TrustedMobilePhone> listTrustedMobilePhoneByName(String name){
        List<TrustedMobilePhone> trustedMobilePhones = sqlSessionTemplate.selectList(BASE_PACKAGE+"listTMPByName",name);
        if (!CollectionUtils.isEmpty(trustedMobilePhones)){
            return trustedMobilePhones;
        }else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<TrustedMobilePhone> listTrustedMobilePhoneByEmail(String email){
        List<TrustedMobilePhone> trustedMobilePhones = sqlSessionTemplate.selectList(BASE_PACKAGE+"listTMPByEmail",email);
        if (!CollectionUtils.isEmpty(trustedMobilePhones)){
            return trustedMobilePhones;
        }else {
            return Collections.EMPTY_LIST;
        }
    }

    public int deleteTrustedMobilePhone(TrustedMobilePhone trustedMobilePhone){
        int result = sqlSessionTemplate.delete(BASE_PACKAGE+"delete",trustedMobilePhone);
        return result;
    }

    public int updateTrustedMobilePhone(TrustedMobilePhone trustedMobilePhone){
        int result = sqlSessionTemplate.update(BASE_PACKAGE + "update", trustedMobilePhone);
        return result;
    }
}
