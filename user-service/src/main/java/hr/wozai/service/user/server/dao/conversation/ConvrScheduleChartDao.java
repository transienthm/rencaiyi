package hr.wozai.service.user.server.dao.conversation;

import hr.wozai.service.user.client.conversation.utils.ConvrUtils;
import hr.wozai.service.user.server.model.conversation.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by wangbin on 2016/11/30.
 */
@Repository("convrScheduleChartDao")
public class ConvrScheduleChartDao {

    private final static Integer WEEK = 1;
    private final static Integer HALF_MONTH = 2;
    private final static Integer MONTH = 3;

    private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ConvrScheduleChartMapper.";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<ConvrScheduleChart> listConvrScheduleChartByOrgId(int period, long orgId) {
        List<ConvrScheduleChart> result = new ArrayList<>();
        Map<String, ConvrScheduleChart> timeStrAndConvrScheduleChartMap = new HashMap<>();
        Map map = new HashMap();
//        map.put("period", period);
        map.put("orgId", orgId);

        List<LocalDate> pastSomeDays = new ArrayList<>();
        Date now = new Date();
        LocalDate today = ConvrUtils.convertDateToLocalDate(now);
        pastSomeDays.add(today);
        for (int i = 1; i <= period; i++) {
            LocalDate past = today.minusDays(i);
            pastSomeDays.add(past);
        }

        List<ConvrSchedulePartInfoChart> convrSchedulePartInfoCharts = sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleForChart", map);
        if (CollectionUtils.isEmpty(convrSchedulePartInfoCharts)) {
            return result;
        }

        for (LocalDate day : pastSomeDays) {
            Float ref = new Float(0);
            ConvrScheduleChart convrScheduleChart = new ConvrScheduleChart();
            for (ConvrSchedulePartInfoChart convrSchedulePartInfoChart : convrSchedulePartInfoCharts) {
                LocalDate createDay = LocalDate.parse(convrSchedulePartInfoChart.getDate());
                if (day.isAfter(createDay) || day.isEqual(createDay)) {
                    float refValue = getRefValueFromPeriodType(convrSchedulePartInfoChart.getPeriodType(), convrSchedulePartInfoChart.getAmount());
                    if (refValue == -1) {
                        continue;
                    }
                    ref += refValue;
                }
            }
            convrScheduleChart.setPlanedTimes(ref);
            String dateStr = ConvrUtils.getFormatedTimeStrFromLocalDate(day);
            convrScheduleChart.setDate(dateStr);
            timeStrAndConvrScheduleChartMap.put(dateStr, convrScheduleChart);
        }

        LocalDate startLocalDate = pastSomeDays.get(pastSomeDays.size() - 1);
        long startTime = ConvrUtils.convertLocalDateToDate(startLocalDate).getTime();
        long endTime = ConvrUtils.convertLocalDateToDate(today).getTime();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        List<ConvrRecord> convrRecords = sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrRecord", map);
        for (ConvrRecord convrRecord : convrRecords) {
            LocalDate recordLocalDate = ConvrUtils.convertDateToLocalDate(new Date(convrRecord.getCreatedTime()));
            String recordDateStr = ConvrUtils.getFormatedTimeStrFromLocalDate(recordLocalDate);
            if (timeStrAndConvrScheduleChartMap.containsKey(recordDateStr)) {
                ConvrScheduleChart convrScheduleChart = timeStrAndConvrScheduleChartMap.get(recordDateStr);
                float actualTimes = convrScheduleChart.getActualTimes();
                convrScheduleChart.setActualTimes(actualTimes + 1);
                timeStrAndConvrScheduleChartMap.put(recordDateStr, convrScheduleChart);
            } else {
                ConvrScheduleChart convrScheduleChart = new ConvrScheduleChart();
                convrScheduleChart.setActualTimes(1);
                convrScheduleChart.setDate(ConvrUtils.getFormatedTimeStrFromLocalDate(recordLocalDate));
                timeStrAndConvrScheduleChartMap.put(recordDateStr, convrScheduleChart);
            }
        }
        for (Map.Entry<String, ConvrScheduleChart> entry : timeStrAndConvrScheduleChartMap.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public ConvrScheduleChart getConvrChartInAMonth(long orgId) {

        Map map = new HashMap();
//        map.put("period", period);
        map.put("orgId", orgId);
        List<ConvrSchedulePartInfoChart> convrSchedulePartInfoCharts = sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleForChart", map);
        float planedTimes=0f;
        for (ConvrSchedulePartInfoChart convrSchedulePartInfoChart : convrSchedulePartInfoCharts) {
            int periodType = convrSchedulePartInfoChart.getPeriodType();
            long amount = convrSchedulePartInfoChart.getAmount();
            planedTimes += getPlanedAmount(periodType, amount);
        }
        ConvrScheduleChart convrScheduleChart = new ConvrScheduleChart();
        convrScheduleChart.setPlanedTimes(planedTimes);

        Date now = new Date();
        LocalDate nowLocalDate = ConvrUtils.convertDateToLocalDate(now);
        LocalDate firstDayOfMonth = nowLocalDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = nowLocalDate.with(TemporalAdjusters.lastDayOfMonth());
        long startTime = ConvrUtils.convertLocalDateToDate(firstDayOfMonth).getTime();
        long endTime = ConvrUtils.convertLocalDateToDate(lastDayOfMonth).getTime();

        map.put("startTime", startTime);
        map.put("endTime", endTime);
        List<ConvrRecord> convrRecords = sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrRecord", map);
        convrScheduleChart.setActualTimes(convrRecords.size());

        return convrScheduleChart;
    }

    public List<ConvrSourceUserInfo> getConvrSourceUserInfoByOrgId(long orgId, int pageNumber, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("pageStart", (pageNumber - 1) * pageSize);
        params.put("pageSize", pageSize);
        return sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrSourceUserInfoByOrgId", params);
    }

    private float getPlanedAmount(int periodType, long amount) {
        if (periodType == WEEK) {
            return amount * 4f;
        } else if (periodType == HALF_MONTH) {
            return amount * 2f;
        } else if (periodType == MONTH) {
            return amount;
        }
        return 0;
    }

    private float getRefValueFromPeriodType(int periodType, long amount) {
        if (periodType == WEEK) {
            return 1f / 7f * amount;
        } else if (periodType == HALF_MONTH) {
            return 1f / 15f * amount;
        } else if (periodType == MONTH) {
            return 1f / 30f * amount;
        }
        return -1f;
    }

    public static void main(String[] args) {
        System.out.println(1f / 7f *2);
    }

}
