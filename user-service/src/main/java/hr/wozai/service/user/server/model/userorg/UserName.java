package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/29
 */
@Data
@NoArgsConstructor
public class UserName {
  String userNameId;

  Long orgId;

  Long userId;

  String userName;

  String pinyin;

  String abbreviation;

  Integer isDeleted;
}
