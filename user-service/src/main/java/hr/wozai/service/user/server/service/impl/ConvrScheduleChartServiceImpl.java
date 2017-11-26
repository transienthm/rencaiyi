package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.conversation.utils.ConvrUtils;
import hr.wozai.service.user.server.dao.conversation.ConvrScheduleChartDao;
import hr.wozai.service.user.server.model.conversation.ConvrScheduleChart;
import hr.wozai.service.user.server.model.conversation.ConvrSourceUserChart;
import hr.wozai.service.user.server.model.conversation.ConvrSourceUserInfo;
import hr.wozai.service.user.server.service.ConvrScheduleChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by wangbin on 2016/12/6.
 */
@Service("convrScheduleChartService")
public class ConvrScheduleChartServiceImpl implements ConvrScheduleChartService {

    @Autowired
    private ConvrScheduleChartDao convrScheduleChartDao;

    @Override
    @LogAround
    public List<ConvrScheduleChart> listConvrScheduleChartByOrgId(int period, long orgId) {
        List<ConvrScheduleChart> result = convrScheduleChartDao.listConvrScheduleChartByOrgId(period, orgId);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }


    @Override
    @LogAround
    public ConvrScheduleChart getConvrScheduleChartInAMonth(long orgId) {
        return convrScheduleChartDao.getConvrChartInAMonth(orgId);
    }

    @Override
    @LogAround
    public List<ConvrSourceUserChart> listConvrSourceUserChart(long orgId, int pageNumber, int pageSize) {
        List<ConvrSourceUserChart> result = new ArrayList<>();
        LocalDate now = ConvrUtils.convertDateToLocalDate(new Date());
        LocalDate firstDayOfCurMonth = ConvrUtils.getFirstDayOfCurMonth(now);
        LocalDate lastDayOfCurMonth = ConvrUtils.getLastDayOfCurMonth(now);
        LocalDate firstDayOfCurQuarter = getFirstDayOfCurQuarter(now);
        LocalDate lastDayOfCurQuarter = getLastDayOfCurQuarter(now);

        Map<Long, Integer> sourceUserIdAndTotalCountMap = new HashMap<>();
        Map<Long, Integer> sourceUserIdAndMonthCountMap = new HashMap<>();
        Map<Long, Integer> sourceUserIdAndQuarterCountMap = new HashMap<>();
        Map<Long, LocalDate> sourceUserIdAndDateMap = new HashMap<>();

        Set<Long> sourceUserIds = new HashSet<>();

        List<ConvrSourceUserInfo> convrSourceUserInfos = convrScheduleChartDao.getConvrSourceUserInfoByOrgId(orgId, pageNumber, pageSize);
        if (CollectionUtils.isEmpty(convrSourceUserInfos)) {
            return result;
        }
        for (ConvrSourceUserInfo convrSourceUserInfo : convrSourceUserInfos) {
            long sourceUserId = convrSourceUserInfo.getSourceUserId();
            LocalDate convrRecordDate = LocalDate.parse(convrSourceUserInfo.getLastDate());

            if (!sourceUserIdAndDateMap.containsKey(sourceUserId)) {
                sourceUserIdAndDateMap.put(sourceUserId, convrRecordDate);
            }

            if (sourceUserIdAndTotalCountMap.containsKey(sourceUserId)) {
                Integer totalCount = sourceUserIdAndTotalCountMap.get(sourceUserId) + 1;
                sourceUserIdAndTotalCountMap.put(sourceUserId, totalCount);
            }else {
                sourceUserIdAndTotalCountMap.put(sourceUserId, 1);
            }
            if (ConvrUtils.isBetweenTwoDays(now, firstDayOfCurMonth, lastDayOfCurMonth)) {
                if (sourceUserIdAndMonthCountMap.containsKey(sourceUserId)) {
                    Integer monthCount = sourceUserIdAndMonthCountMap.get(sourceUserId);
                    sourceUserIdAndMonthCountMap.put(sourceUserId, monthCount + 1);
                } else {
                    sourceUserIdAndMonthCountMap.put(sourceUserId, 1);
                }
            }
            if (ConvrUtils.isBetweenTwoDays(now, firstDayOfCurQuarter, lastDayOfCurQuarter)) {
                if (sourceUserIdAndQuarterCountMap.containsKey(sourceUserId)) {
                    Integer monthCount = sourceUserIdAndQuarterCountMap.get(sourceUserId);
                    sourceUserIdAndQuarterCountMap.put(sourceUserId, monthCount + 1);
                } else {
                    sourceUserIdAndQuarterCountMap.put(sourceUserId, 1);
                }
            }
        }
        for (ConvrSourceUserInfo convrSourceUserInfo : convrSourceUserInfos) {
            long sourceUserId = convrSourceUserInfo.getSourceUserId();
            if (sourceUserIds.contains(sourceUserId)) {
                continue;
            } else {
                sourceUserIds.add(sourceUserId);
            }
            ConvrSourceUserChart convrSourceUserChart = new ConvrSourceUserChart();
            convrSourceUserChart.setSourceUserId(sourceUserId);
            convrSourceUserChart.setConvrTimesInThisMonth(sourceUserIdAndMonthCountMap.get(sourceUserId));
            convrSourceUserChart.setConvrTimesInThisQuarter(sourceUserIdAndQuarterCountMap.get(sourceUserId));
            convrSourceUserChart.setLastDate(ConvrUtils.getFormatedTimeStrFromLocalDate(sourceUserIdAndDateMap.get(sourceUserId)));
            convrSourceUserChart.setTotalCount(sourceUserIdAndTotalCountMap.get(sourceUserId));
            result.add(convrSourceUserChart);
        }
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    private LocalDate getFirstDayOfCurQuarter(LocalDate now) {
        return ConvrUtils.getFirstDayOfCurrentQuarterByLocalDate(now);
    }

    private LocalDate getLastDayOfCurQuarter(LocalDate now) {
        return ConvrUtils.getLastDayOfCurrentQuarterByLocalDate(now);
    }

    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        LocalDate firstDayOfYear = localDate.with(TemporalAdjusters.firstDayOfYear());
        LocalDate plus2Month = firstDayOfYear.plusMonths(2);
        LocalDate endOfFirstQuarter = plus2Month.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(firstDayOfYear);
        System.out.println(plus2Month);
        System.out.println(endOfFirstQuarter);
    }
}
