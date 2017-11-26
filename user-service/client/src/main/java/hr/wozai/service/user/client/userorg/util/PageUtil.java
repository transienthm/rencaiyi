package hr.wozai.service.user.client.userorg.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/18
 */
public class PageUtil {
  public static List<Long> filterUserIds(List<Long> searchResult, List<Long> teamUserIds) {
    List<Long> result = new ArrayList<>();
    for (Long id : searchResult) {
      if (teamUserIds.contains(id)) {
        result.add(id);
      }
    }
    return result;
  }
  // 对List进行分页
  public static <T> List<T> getPagingList(List<T> userIds, int pageNumber, int pageSize) {
    int fromIndex = (pageNumber - 1) * pageSize;
    int toIndex = pageNumber * pageSize;
    if (toIndex >= userIds.size()) {
      toIndex = userIds.size();
    }

    List<T> result = new ArrayList<>();
    if (fromIndex > toIndex) {
      return result;
    }

    result = userIds.subList(fromIndex, toIndex);
    return  result;
  }
}
