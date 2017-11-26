package hr.wozai.service.thirdparty.client.bean;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/10/19.
 */
@Data
@NoArgsConstructor
public class BatchEmail {
  private Map<String, String> fixedParamsMap;
  private List<String> dynamicParamSeq;
  private List<List<String>> dynamicParams;
  private EmailTemplate emailTemplate;

  public void selfCheck() {
    int position = -1;
    for (int i = 0; i < this.getDynamicParamSeq().size(); i++) {
      if (StringUtils.isEqual(this.getDynamicParamSeq().get(i), "dstEmailAddress")) {
        position = i;
      }
    }

    if (position < 0) {
      ServiceStatusException batchEmailAddressError = new ServiceStatusException(ServiceStatus.COMMON_BATCH_EMAIL_ADDRESS_ERROR);
      batchEmailAddressError.setErrInfo("请确认dynamicParamSeq中包含参数名：dstEmailAddress");
      throw batchEmailAddressError;
    }

    for (List<String> innerList : dynamicParams) {
      if (null == innerList || innerList.size() != dynamicParamSeq.size()) {
        ServiceStatusException batchEmailParameterLengthError = new ServiceStatusException(ServiceStatus.COMMON_BATCH_EMAIL_PARAMETER_LENGTH_ERROR);
        batchEmailParameterLengthError.setErrInfo("请确认dynamicParams中每组参数的长度与dynamicParamSeq中约定的一致");
        throw batchEmailParameterLengthError;
      }
    }
  }

  public String unicode() {
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmssSSS");
    return simpleDateFormat.format(date) + "_" + (this.hashCode() & 0x7FFFFFFF);
  }

}
