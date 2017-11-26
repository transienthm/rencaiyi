package hr.wozai.service.thirdparty.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.server.dao.TrustedMobilePhoneDao;
import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;
import hr.wozai.service.thirdparty.server.service.TrustedMobilePhoneService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin on 16/5/11.
 */
@Service("trustedMobilePhoneService")
public class TrustedMobilePhoneServiceImpl implements TrustedMobilePhoneService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrustedMobilePhoneServiceImpl.class);

    @Autowired
    TrustedMobilePhoneDao trustedMobilePhoneDao;

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long insertTrustedMobilePhone(String mobilePhone, String name, String emailAddress,Integer isDeleted) {
        TrustedMobilePhone trustedMobilePhone = new TrustedMobilePhone();
        trustedMobilePhone.setMobilePhone(mobilePhone);
        trustedMobilePhone.setName(name);
        trustedMobilePhone.setEmailAddress(emailAddress);
        trustedMobilePhone.setIsDeleted(isDeleted);
        long result = trustedMobilePhoneDao.insertTrustedMobilePhone(trustedMobilePhone);
        return result;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteTrustedMobilePhone(String mobilePhone) {
        TrustedMobilePhone trustedMobilePhone = trustedMobilePhoneDao.findTrustedMobilePhoneByMobilePhone(mobilePhone);

        int result = trustedMobilePhoneDao.deleteTrustedMobilePhone(trustedMobilePhone);
        if (result > 0) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public TrustedMobilePhone findTrustedMobilePhoneByMobilePhone(String mobilePhone) {
        TrustedMobilePhone trustedMobilePhone = trustedMobilePhoneDao.findTrustedMobilePhoneByMobilePhone(mobilePhone);
        if (null == trustedMobilePhone){
            throw new ServiceStatusException(ServiceStatus.TP_MOBILE_NOT_FOUND);
        }
        return trustedMobilePhone;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public List<TrustedMobilePhone> listTrustedMobilePhoneByName(String name) {
        List<TrustedMobilePhone> trustedMobilePhones = trustedMobilePhoneDao.listTrustedMobilePhoneByName(name);
        if (CollectionUtils.isEmpty(trustedMobilePhones)) {
            throw new ServiceStatusException(ServiceStatus.TP_MOBILE_NOT_FOUND);
        }
        return trustedMobilePhones;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public List<TrustedMobilePhone> listTrustedMobilePhoneByEmail(String emailAddress) {
        List<TrustedMobilePhone> trustedMobilePhones = trustedMobilePhoneDao.listTrustedMobilePhoneByEmail(emailAddress);
        if (CollectionUtils.isEmpty(trustedMobilePhones)) {
            throw new ServiceStatusException(ServiceStatus.TP_MOBILE_NOT_FOUND);
        }
        return trustedMobilePhones;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public TrustedMobilePhone findTrustedMobilePhoneByPrimaryKey(long trustedMobilePhoneId) {
        TrustedMobilePhone trustedMobilePhone = trustedMobilePhoneDao.findTrustedMobilePhoneByPrimaryKey(trustedMobilePhoneId);
        if (null == trustedMobilePhone){
            throw new ServiceStatusException(ServiceStatus.TP_MOBILE_NOT_FOUND);
        }
        return trustedMobilePhone;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateTrustedMobilePhone(TrustedMobilePhone trustedMobilePhone) {
        int result = trustedMobilePhoneDao.updateTrustedMobilePhone(trustedMobilePhone);
        if (result > 0 ){
            return true;
        }else {
            return false;
        }
    }
}
