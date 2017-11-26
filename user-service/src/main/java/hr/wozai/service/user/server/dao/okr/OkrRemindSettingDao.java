package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.model.okr.OkrRemindSetting;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/10/9
 */
@Repository("okrRemindSettingDao")
public class OkrRemindSettingDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.OkrRemindSettingMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertOkrRemindSetting(List<OkrRemindSetting> okrRemindSettings) {
    if (null == okrRemindSettings || okrRemindSettings.isEmpty()) {
      return 0;
    }
    int result = sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertOkrRemindSetting", okrRemindSettings);
    return result;
  }

  public int deleteOkrRemindSettingByOrgId(long orgId) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteOkrRemindSettingByOrgId", orgId);
  }

  public OkrRemindSetting getOkrRemindSettingByOrgIdAndRemindType(long orgId, int remindType) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("remindType", remindType);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getOkrRemindSettingByOrgIdAndRemindType", map);
  }
}
