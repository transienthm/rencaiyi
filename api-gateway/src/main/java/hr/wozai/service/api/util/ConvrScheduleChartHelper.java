package hr.wozai.service.api.util;

import hr.wozai.service.api.vo.conversation.ConvrScheduleChartInputVO;
import hr.wozai.service.api.vo.conversation.ConvrScheduleChartListVO;
import hr.wozai.service.api.vo.conversation.ConvrScheduleChartVO;
import hr.wozai.service.user.client.conversation.utils.ConvrUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by wangbin on 2016/12/7.
 */
@Component("convrScheduleChartHelper")
public class ConvrScheduleChartHelper {
    private final static Integer WEEK = 1;
    private final static Integer HALF_MONTH = 2;
    private final static Integer MONTH = 3;

    public ConvrScheduleChartListVO convertConvrScheduleChartListVOByPeriodType(ConvrScheduleChartInputVO convrScheduleChartInputVO,
                                                                                ConvrScheduleChartListVO convrScheduleChartListVO) {
        ConvrScheduleChartListVO result = handleChart(convrScheduleChartInputVO.getMinTimeUnit(), convrScheduleChartListVO);
        sortByLocalDate(result);
        return result;
    }

    private void sortByLocalDate(ConvrScheduleChartListVO convrScheduleChartListVO) {
        List<ConvrScheduleChartVO> convrScheduleChartVOs = convrScheduleChartListVO.getConvrScheduleChartVOList();
        if (CollectionUtils.isEmpty(convrScheduleChartVOs)) {
            return;
        }
        for (int i = 0; i < convrScheduleChartVOs.size() - 1; i++) {
            for (int j = 0; j < convrScheduleChartVOs.size() - 1 - i; j++) {
                LocalDate jDate = LocalDate.parse(convrScheduleChartVOs.get(j).getDate());
                LocalDate jpDate = LocalDate.parse(convrScheduleChartVOs.get(j + 1).getDate());
                if (jDate.isAfter(jpDate)) {
                    ConvrScheduleChartVO tmp = convrScheduleChartVOs.get(j);
                    convrScheduleChartVOs.set(j, convrScheduleChartVOs.get(j + 1));
                    convrScheduleChartVOs.set(j + 1, tmp);
                }
            }
        }
    }

    private ConvrScheduleChartListVO handleChart(Integer minTimeUnit, ConvrScheduleChartListVO convrScheduleChartListVO) {
        if (minTimeUnit == 0) {
            return convrScheduleChartListVO;
        }
        Map<String, List<ConvrScheduleChartVO>> timeStrAndConvrScheduleChartVOMap = new HashMap<>();
        ConvrScheduleChartListVO result = new ConvrScheduleChartListVO();
        List<ConvrScheduleChartVO> resultList = new ArrayList<>();
        List<ConvrScheduleChartVO> convrScheduleChartVOs = convrScheduleChartListVO.getConvrScheduleChartVOList();
        if (!CollectionUtils.isEmpty(convrScheduleChartVOs)) {
            for (ConvrScheduleChartVO convrScheduleChartVO : convrScheduleChartVOs) {
                String oldDateStr = convrScheduleChartVO.getDate();
                String newDateStr = ConvrUtils.getPeriodStartDayAndEndDay(minTimeUnit, oldDateStr);
                if (timeStrAndConvrScheduleChartVOMap.containsKey(newDateStr)) {
                    timeStrAndConvrScheduleChartVOMap.get(newDateStr).add(convrScheduleChartVO);
                } else {
                    List<ConvrScheduleChartVO> newList = new ArrayList<>();
                    newList.add(convrScheduleChartVO);
                    timeStrAndConvrScheduleChartVOMap.put(newDateStr, newList);
                }
            }
            for (Map.Entry<String, List<ConvrScheduleChartVO>> entry : timeStrAndConvrScheduleChartVOMap.entrySet()) {
                ConvrScheduleChartVO convrScheduleChartVO = new ConvrScheduleChartVO();
                List<ConvrScheduleChartVO> convrScheduleChartVOList = entry.getValue();
                float actualTimes = 0;
                float planedTimes = 0;
                for (ConvrScheduleChartVO vo : convrScheduleChartVOList) {
                    actualTimes += vo.getActualTimes();
                    planedTimes = vo.getPlanedTimes();
                }
                convrScheduleChartVO.setActualTimes(actualTimes);
                convrScheduleChartVO.setDate(entry.getKey());
                convrScheduleChartVO.setPlanedTimes(planedTimes * (minTimeUnit == WEEK ? 7 : minTimeUnit == HALF_MONTH ? 15 : 30));
                resultList.add(convrScheduleChartVO);
            }
            result.setConvrScheduleChartVOList(resultList);
        }

        return result;
    }
}
