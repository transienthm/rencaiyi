// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-14
 */
@Data
@NoArgsConstructor
public class AddressRegion {

  private Integer regionId;

  private Integer parentId;

  private String regionName;

  private Integer regionType;

  private Integer agencyId;

}
