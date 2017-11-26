package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.user.server.model.conversation.ConvrSourceUserChart;
import hr.wozai.service.user.server.service.ConvrScheduleChartService;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/12/8.
 */
public class ConvrScheduleChartServiceImplTest extends TestBase {

    @Autowired
    private ConvrScheduleChartService convrScheduleChartService;

    @Test
    public void testAll() {
        List<ConvrSourceUserChart> convrSourceUserChartList = convrScheduleChartService.listConvrSourceUserChart(3l, 1, 20);
        for (ConvrSourceUserChart convrSourceUserChart : convrSourceUserChartList) {
            System.out.println(convrSourceUserChart);
        }
    }
}
