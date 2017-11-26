// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.api.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-17
 */
@Data
@NoArgsConstructor
public class SuperAdminVO {

  /**
   * TODO: delete after @LPJ setup for existed orgs
   */
  private Long orgId;

  /**
   * TODO: delete after @LPJ setup for existed orgs
   */
  private String usageSecret;

  private String emailAddress;

  private String passwordPlainText;

}
