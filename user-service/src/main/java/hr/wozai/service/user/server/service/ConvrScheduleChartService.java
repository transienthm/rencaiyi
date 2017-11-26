package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.conversation.ConvrScheduleChart;
import hr.wozai.service.user.server.model.conversation.ConvrSourceUserChart;

import java.util.List;

/**
 * Created by wangbin on 2016/12/6.
 */
public interface ConvrScheduleChartService {
    public List<ConvrScheduleChart> listConvrScheduleChartByOrgId(int period, long orgId);

    public ConvrScheduleChart getConvrScheduleChartInAMonth(long orgId);

    public List<ConvrSourceUserChart> listConvrSourceUserChart(long orgId, int pageNumber, int pageSize);
}
