package hr.wozai.service.thirdparty.server.service;

import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;

import java.util.List;

/**
 * Created by wangbin on 16/5/11.
 */
public interface TrustedMobilePhoneService {

    /***
     * 增加信任电话
     * @param mobilePhone 电话号码
     * @param name 姓名
     * @param emailAddress 邮件
     * @return trustedMobilePhoneId
     */
    public long insertTrustedMobilePhone(String mobilePhone,String name,String emailAddress,Integer isDeleted);

    /**
     * 从信任电话列表中删掉一条
     * @param mobilePhone 需删除的电话号码
     * @return
     */
    public boolean deleteTrustedMobilePhone(String mobilePhone);

    public TrustedMobilePhone findTrustedMobilePhoneByPrimaryKey(long trustedMobilePhoneId);

    public TrustedMobilePhone findTrustedMobilePhoneByMobilePhone(String mobilePhone);

    public List<TrustedMobilePhone> listTrustedMobilePhoneByName(String name);

    public List<TrustedMobilePhone> listTrustedMobilePhoneByEmail(String emailAddress);

    public boolean updateTrustedMobilePhone(TrustedMobilePhone trustedMobilePhone);
}
