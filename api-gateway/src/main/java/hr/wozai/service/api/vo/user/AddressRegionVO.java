// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-16
 */
@Data
@NoArgsConstructor
public class AddressRegionVO {

  private Integer regionId;

  private Integer parentId;

  private String regionName;

  private Integer regionType;

  private Integer agencyId;

}
