package hr.wozai.service.user.server.dao.common;

import hr.wozai.service.user.server.model.common.RemindSetting;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/15
 */
@Repository("remindSettingDao")
public class RemindSettingDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.common.RemindSettingMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertRemindSetting(List<RemindSetting> remindSettingList) {
    if (CollectionUtils.isEmpty(remindSettingList)) {
      return 0;
    }

    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertRemindSetting", remindSettingList);
  }

  public RemindSetting getRemindSettingByUserIdAndRemindType(long orgId, long userId, int remindType) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("remindType", remindType);

    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getRemindSettingByUserIdAndRemindType", params);
  }

  public List<RemindSetting> listRemindSettingByUserId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listRemindSettingByUserId", params);
  }

  public int batchUpdateRemindSetting(List<RemindSetting> remindSettingList) {
    if (CollectionUtils.isEmpty(remindSettingList)) {
      return 0;
    }

    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateRemindSetting", remindSettingList);
  }

  public int deleteRemindSettingByUserId(long orgId, long userId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("actorUserId", actorUserId);

    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteRemindSettingByUserId", params);
  }
}
