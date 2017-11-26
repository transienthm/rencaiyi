package hr.wozai.service.api.util;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/31
 */
public class PageUtils {

  private static final Integer MAX_PAGE_SIZE = 50;

  public static boolean isPageParamValid(int pageNumber, int pageSize) {

    if (pageNumber <= 0)
      return false;
    if (pageSize <=0 || pageSize > MAX_PAGE_SIZE)
      return false;

    return true;
  }

}
