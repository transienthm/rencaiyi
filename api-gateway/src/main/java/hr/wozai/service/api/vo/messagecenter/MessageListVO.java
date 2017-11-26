package hr.wozai.service.api.vo.messagecenter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by wangbin on 16/7/5.
 */
@Data
@NoArgsConstructor
public class MessageListVO {
    List<MessageVO> messageVOs;
    Integer totalNumber;
}
