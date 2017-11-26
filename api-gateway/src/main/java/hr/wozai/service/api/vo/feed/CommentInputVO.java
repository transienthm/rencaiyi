// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.feed;

import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import hr.wozai.service.api.vo.IdVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-17
 */
@Data
@NoArgsConstructor
public class CommentInputVO {

    @StringLengthConstraint(lengthConstraint = 140)
    String content;

    List<IdVO> atUsers;
}
