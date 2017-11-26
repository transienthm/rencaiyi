package hr.wozai.service.thirdparty.client.bean;

import lombok.Data;

/**
 * Created by wangbin on 2016/10/26.
 */
@Data
public class EmailContent {
  private String subject;
  private String html;
  private String dstEmailAddress;
}
