// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
@Data
@NoArgsConstructor
public class EncryptedPickOption {

  private String pickOptionId;

  private String optionValue;

  private Integer optionIndex;

  private Integer isDefault;

  private Integer isDeprecated;

}
