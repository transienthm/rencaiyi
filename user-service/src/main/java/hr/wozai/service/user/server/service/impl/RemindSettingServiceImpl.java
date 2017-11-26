package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.server.dao.common.RemindSettingDao;
import hr.wozai.service.user.server.model.common.RemindSetting;
import hr.wozai.service.user.server.service.RemindSettingService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/16
 */
@Service("remindSettingService")
public class RemindSettingServiceImpl implements RemindSettingService {
  private static Logger LOGGER = LoggerFactory.getLogger(RemindSettingServiceImpl.class);

  @Autowired
  RemindSettingDao remindSettingDao;

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void initRemindSettingByUserId(long orgId, long userId, long actorUserId) {
    List<RemindSetting> remindSettings = new ArrayList<>();
    for (RemindType remindType : RemindType.values()) {
      RemindSetting remindSetting = new RemindSetting();
      remindSetting.setOrgId(orgId);
      remindSetting.setUserId(userId);
      remindSetting.setRemindType(remindType.getCode());
      remindSetting.setStatus(remindType.getDefaultStatus());
      remindSetting.setCreatedUserId(actorUserId);
      remindSettings.add(remindSetting);
    }

    remindSettingDao.batchInsertRemindSetting(remindSettings);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchUpdateRemindSetting(List<RemindSetting> remindSettingList) {
    remindSettingDao.batchUpdateRemindSetting(remindSettingList);
  }

  @Override
  @LogAround
  public List<RemindSetting> listRemindSettingByUserId(long orgId, long userId) {
    return remindSettingDao.listRemindSettingByUserId(orgId, userId);
  }

  @Override
  @LogAround
  public RemindSetting getRemindSettingByUserIdAndRemindType(long orgId, long useId, int remindType) {
    RemindSetting result = remindSettingDao.getRemindSettingByUserIdAndRemindType(orgId, useId, remindType);
    if (null == result) {
      throw new ServiceStatusException(ServiceStatus.UO_REMINDSETTING_NOT_FOUND);
    }

    return result;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteRemindSettingByUserId(long orgId, long userId, long actorUserId) {
    remindSettingDao.deleteRemindSettingByUserId(orgId, userId, actorUserId);
  }
}
