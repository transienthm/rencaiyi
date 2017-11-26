package hr.wozai.service.user.server.dao.conversation;

import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/11/30.
 */
@Repository("convrScheduleTaskDao")
public class ConvrScheduleTaskDao {

    private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ConvrScheduleTaskMapper.";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<ConvrSchedule> listConvrScheduleByOrgId(long orgId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        List<ConvrSchedule> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleByOrg", map);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }
}
