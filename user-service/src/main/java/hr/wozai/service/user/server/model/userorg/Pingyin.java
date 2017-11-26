package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/14
 */
@Data
@NoArgsConstructor
public class Pingyin {
  private List<String> pingyinList;

  private List<String> firstSpellList;
}
