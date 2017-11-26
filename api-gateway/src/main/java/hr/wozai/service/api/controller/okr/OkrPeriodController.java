package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.okr.enums.*;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.api.vo.okr.LevelOneTimeSpanVO;
import hr.wozai.service.api.vo.okr.ObjectivePeriodVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/8
 */
@Controller("okrPeriodController")
public class OkrPeriodController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrPeriodController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  PermissionUtil permissionUtil;

  @LogAround

  @RequestMapping(value = "/okrs/objective-periods/time-span/level-one",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listLevelOnePeriodTimeSpan(
          @RequestParam(required = false, defaultValue = "0") Integer periodTimeSpanId) {
    Result<Object> result = new Result<>();

    LevelOneTimeSpanVO month = new LevelOneTimeSpanVO();
    month.setType(PeriodTimeSpanType.MONTH.getCode());
    month.setName("月");

    LevelOneTimeSpanVO quarter = new LevelOneTimeSpanVO();
    quarter.setType(PeriodTimeSpanType.QUARTER.getCode());
    quarter.setName("季度");

    LevelOneTimeSpanVO halfYear = new LevelOneTimeSpanVO();
    halfYear.setType(PeriodTimeSpanType.HALF_YEAR.getCode());
    halfYear.setName("半年");

    LevelOneTimeSpanVO year = new LevelOneTimeSpanVO();
    year.setType(PeriodTimeSpanType.YEAR.getCode());
    year.setName("年");

    if (IntegerUtils.equals(0, periodTimeSpanId)) {
      quarter.setDefault(true);
    } else {
      int type = PeriodTimeSpan.getEnumByCode(periodTimeSpanId).getType();
      if (type == PeriodTimeSpanType.YEAR.getCode()) {
        year.setDefault(true);
      } else if (type == PeriodTimeSpanType.HALF_YEAR.getCode()) {
        halfYear.setDefault(true);
      } else if (type == PeriodTimeSpanType.QUARTER.getCode()) {
        quarter.setDefault(true);
      } else if (type == PeriodTimeSpanType.MONTH.getCode()) {
        month.setDefault(true);
      } else {
        quarter.setDefault(true);
      }
    }

    List<LevelOneTimeSpanVO> list = new ArrayList<>();
    list.add(month);
    list.add(quarter);
    list.add(halfYear);
    list.add(year);

    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(list);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objective-periods/time-span/level-two",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listLevelTwoPeriodTimeSpan(
          @RequestParam Integer type,
          @RequestParam(required = false, defaultValue = "") String year,
          @RequestParam(required = false, defaultValue = "0") Integer periodTimeSpanId) {
    Result<Object> result = new Result<>();

    List<ObjectivePeriodVO> objectivePeriodVOs = new ArrayList<>();
    if (IntegerUtils.equals(type, PeriodTimeSpanType.MONTH.getCode())) {

    } else if (IntegerUtils.equals(type, PeriodTimeSpanType.QUARTER.getCode())) {

    } else if (IntegerUtils.equals(type, PeriodTimeSpanType.HALF_YEAR.getCode())) {

    } else if (IntegerUtils.equals(type, PeriodTimeSpanType.YEAR.getCode())) {

    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    Integer currentYear;
    if (StringUtils.isNullOrEmpty(year)) {
      currentYear = TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING);
    } else {
      currentYear = Integer.valueOf(year);
    }

    setTimeSpan(currentYear, type, objectivePeriodVOs, periodTimeSpanId);
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(objectivePeriodVOs);
    return result;
  }

  @LogAround
  private void setTimeSpan(Integer year, Integer type, List<ObjectivePeriodVO> objectivePeriodVOs,
                           Integer defaultPeriodTimeSpanId) {
    Integer month = TimeUtils.getCurrentMonthWithTimeZone(TimeUtils.BEIJING);
    Integer lastYear = year - 1;
    Integer nextYear = year + 1;
    List<PeriodTimeSpan> periodTimeSpen = PeriodTimeSpan.getPeriodListByType(type);
    for (PeriodTimeSpan periodTimeSpan : periodTimeSpen) {
      ObjectivePeriodVO objectivePeriodVO = new ObjectivePeriodVO();
      objectivePeriodVO.setYear(lastYear);
      objectivePeriodVO.setPeriodTimeSpanId(periodTimeSpan.getCode());
      objectivePeriodVO.setName(PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(lastYear, periodTimeSpan));
      objectivePeriodVOs.add(objectivePeriodVO);
    }
    for (PeriodTimeSpan periodTimeSpan : periodTimeSpen) {
      ObjectivePeriodVO objectivePeriodVO = new ObjectivePeriodVO();
      objectivePeriodVO.setYear(year);
      objectivePeriodVO.setPeriodTimeSpanId(periodTimeSpan.getCode());
      objectivePeriodVO.setName(PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, periodTimeSpan));
      if (defaultPeriodTimeSpanId != 0) {
        if (IntegerUtils.equals(defaultPeriodTimeSpanId, periodTimeSpan.getCode())) {
          objectivePeriodVO.setDefault(true);
        }
      } else {
        if (PeriodTimeSpan.isInMonthRegion(month, periodTimeSpan)) {
          objectivePeriodVO.setDefault(true);
        }
      }
      objectivePeriodVOs.add(objectivePeriodVO);
    }
    for (PeriodTimeSpan periodTimeSpan : periodTimeSpen) {
      ObjectivePeriodVO objectivePeriodVO = new ObjectivePeriodVO();
      objectivePeriodVO.setYear(nextYear);
      objectivePeriodVO.setPeriodTimeSpanId(periodTimeSpan.getCode());
      objectivePeriodVO.setName(PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(nextYear, periodTimeSpan));
      objectivePeriodVOs.add(objectivePeriodVO);
    }
  }

  /*public static void main(String[] args) {
    List<ObjectivePeriodVO> objectivePeriodVOs = new ArrayList<>();
    OkrPeriodController.setTimeSpan(2018, 1, objectivePeriodVOs, 200);
  }*/
}
