package hr.wozai.service.user.server.model.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/12/8.
 */
@Data
@NoArgsConstructor
public class ConvrSourceUserInfo {
    private long sourceUserId;

    private String lastDate;
}
