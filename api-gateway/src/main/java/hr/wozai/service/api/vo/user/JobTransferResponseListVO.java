// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
@Data
@NoArgsConstructor
public class JobTransferResponseListVO {

  private List<JobTransferResponseVO> jobTransferResponseVOs;

  private int totalNumber;

}
